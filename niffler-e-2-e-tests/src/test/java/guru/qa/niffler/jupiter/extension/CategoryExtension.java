package guru.qa.niffler.jupiter.extension;


import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class CategoryExtension implements
        BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                            UserJson user = context.getStore(UserExtension.NAMESPACE).get(
                                    context.getUniqueId(),
                                    UserJson.class
                            );

                            final String username = user != null ?
                                    user.username() : userAnno.username();

                            final List<CategoryJson> createdCategories = new ArrayList<>();
                            for (Category categoryAnno : userAnno.categories()) {

                                CategoryJson category = new CategoryJson(
                                        null,
                                        "".equals(categoryAnno.name()) ?
                                                RandomDataUtils.getRandomCategory() : categoryAnno.name(),
                                        username,
                                        categoryAnno.archived()
                                );
                                createdCategories.add(spendDbClient.createCategory(category));
                            }

                            if (user != null) {
                                user.testData().categories().addAll(createdCategories);
                            } else {
                                context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategories);
                            }
                        }
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (CategoryJson[]) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class).stream().toArray(CategoryJson[]::new);
    }


}