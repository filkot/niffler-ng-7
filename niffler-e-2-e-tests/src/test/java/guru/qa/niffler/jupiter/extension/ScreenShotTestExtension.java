package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                BufferedImage.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ScreenShotTest annotation = getAnnotation(extensionContext);

        try (InputStream input = new ClassPathResource(annotation.value()).getInputStream()) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new ParameterResolutionException("Failed to read image from resource: " + annotation.value());
            }
            return image;
        } catch (IOException e) {
            throw new ParameterResolutionException("Failed to read image from resource: " + annotation.value(), e);
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // Получаем аннотацию @ScreenShotTest
        ScreenShotTest annotation = getAnnotation(context);

        // Проверяем, нужно ли перезаписывать ожидаемый скриншот
        if (annotation.rewriteExpected()) {
            rewriteExpected(annotation);
        }

        // Создаем и добавляем вложение с различиями
        ScreenDiff screenDiff = new ScreenDiff(
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToByte(getExpected())),
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToByte(getActual())),
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToByte(getDiff()))
        );
        Allure.addAttachment(
                "ScreenShot diff",
                "application/vnd.allure.image.diff",
                objectMapper.writeValueAsString(screenDiff)
        );

        // Пробрасываем исключение дальше
        throw throwable;
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    private static ScreenShotTest getAnnotation(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ScreenShotTest.class)
                .orElseThrow(() -> new ParameterResolutionException(
                        "No @ScreenShotTest annotation found on method: " + context.getRequiredTestMethod().getName()));
    }

    private static void rewriteExpected(ScreenShotTest annotation) {
        BufferedImage actual = getActual();
        if (actual != null) {
            // Перезаписываем ожидаемый скриншот
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ImageIO.write(actual, "png", outputStream);

                // Получаем путь к ресурсу из аннотации
                String resourcePath = annotation.value();
                Path destinationPath = Paths.get("src/test/resources", resourcePath);

                // Создаем директории, если они не существуют
                Files.createDirectories(destinationPath.getParent());

                // Сохраняем новый ожидаемый скриншот в ресурсы
                Files.write(destinationPath, outputStream.toByteArray(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Expected screenshot rewritten: " + destinationPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to rewrite expected screenshot", e);
            }
        }
    }

    public static byte[] imageToByte(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
