package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class NonStaticBrowsersExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    // Используем ThreadLocal для хранения драйверов каждого теста
    private final ThreadLocal<List<SelenideDriver>> drivers = ThreadLocal.withInitial(ArrayList::new);

    // Метод для получения драйверов текущего теста
    public List<SelenideDriver> drivers() {
        return drivers.get();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // Инициализация Allure Selenide Listener
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        // Закрываем все драйверы текущего теста
        for (SelenideDriver driver : drivers.get()) {
            if (driver.hasWebDriverStarted()) {
                driver.close();
            }
        }
        // Очищаем список драйверов для текущего теста
        drivers.remove();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // Делаем скриншот при возникновении исключения
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // Делаем скриншот при возникновении исключения
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // Делаем скриншот при возникновении исключения
        doScreenshot();
        throw throwable;
    }

    private void doScreenshot() {
        // Делаем скриншот для каждого драйвера текущего теста
        for (SelenideDriver driver : drivers.get()) {
            if (driver.hasWebDriverStarted()) {
                Allure.addAttachment(
                        "Screen on fail for browser " + driver.getSessionId(),
                        new ByteArrayInputStream(
                                ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                        )
                );
            }
        }
    }
}