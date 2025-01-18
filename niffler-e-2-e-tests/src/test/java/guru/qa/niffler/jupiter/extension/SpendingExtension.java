package guru.qa.niffler.jupiter.extension;


import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;
import java.util.Optional;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.spendings().length > 0) {
                        Spending anno = userAnno.spendings()[0];

                        Optional<CategoryJson> categoryByUsernameAndCategoryName = spendDbClient
                                .findCategoryByUsernameAndCategoryName(userAnno.username(), anno.category());

                        SpendJson spend = new SpendJson(
                                null,
                                new Date(),

                                categoryByUsernameAndCategoryName.orElseGet(() ->
                                        new CategoryJson(
                                                null,
                                                anno.category(),
                                                userAnno.username(),
                                                false
                                        )),
                                CurrencyValues.RUB,
                                anno.amount(),
                                anno.description(),
                                userAnno.username()
                        );
                        SpendJson createdSpend = spendDbClient.createSpend(spend);
                        context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpend);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }

}