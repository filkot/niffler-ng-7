package guru.qa.niffler.jupiter.extension;


import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.spendings().length > 0) {
                        UserJson user = context.getStore(UserExtension.NAMESPACE).get(
                                context.getUniqueId(),
                                UserJson.class
                        );

                        final String username = user != null ?
                                user.username() : userAnno.username();

                        final List<SpendJson> createdSpends = new ArrayList<>();
                        for (Spending anno : userAnno.spendings()) {

                            Optional<CategoryJson> categoryByUsernameAndCategoryName = spendDbClient
                                    .findCategoryByUsernameAndCategoryName(username, anno.category());

                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),

                                    categoryByUsernameAndCategoryName.orElseGet(() ->
                                            new CategoryJson(
                                                    null,
                                                    anno.category(),
                                                    username,
                                                    false
                                            )),
                                    anno.currency(),
                                    anno.amount(),
                                    anno.description(),
                                   username
                            );

                            createdSpends.add(spendDbClient.createSpend(spend));
                        }
                        if (user != null) {
                            user.testData().spends().addAll(createdSpends);
                        } else {
                            context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpends);
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (SpendJson[])extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class).stream().toArray(SpendJson[]::new);
    }

}