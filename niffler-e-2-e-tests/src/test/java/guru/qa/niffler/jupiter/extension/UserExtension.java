package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Friend;
import guru.qa.niffler.jupiter.annotation.IncomeInvitation;
import guru.qa.niffler.jupiter.annotation.OutcomeInvitation;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class UserExtension implements BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";

    private final UsersClient usersClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                            if ("".equals(userAnno.username())) {
                                final String username = RandomDataUtils.getRandomUsername();

                                UserJson user = usersClient.createUser(username, defaultPassword);

                                List<UserJson> friends = new ArrayList<>();
                                for (Friend friend : userAnno.friends()) {
                                    if("".equals(friend.username())){
                                        final String usernameFriend = RandomDataUtils.getRandomUsername();
                                        UserJson userFriend = usersClient.createUser(usernameFriend, defaultPassword);
                                        usersClient.createFriend(user , userFriend);
                                        friends.add(userFriend);
                                    }
                                }

                                List<UserJson> incomeInvitations = new ArrayList<>();
                                for (IncomeInvitation incomeInvitation : userAnno.incomeInvitations()) {
                                    if("".equals(incomeInvitation.username())){
                                        final String usernameIncomeInvitation = RandomDataUtils.getRandomUsername();
                                        UserJson userIncomeInvitation = usersClient.createUser(usernameIncomeInvitation, defaultPassword);
                                        usersClient.createIncomeInvitation(user , userIncomeInvitation);
                                        incomeInvitations.add(userIncomeInvitation);
                                    }
                                }

                                List<UserJson> outcomeInvitations = new ArrayList<>();
                                for (OutcomeInvitation outcomeInvitation : userAnno.outcomeInvitations()) {
                                    if("".equals(outcomeInvitation.username())){
                                        final String usernameOutcomeInvitation = RandomDataUtils.getRandomUsername();
                                        UserJson userOutcomeInvitation = usersClient.createUser(usernameOutcomeInvitation, defaultPassword);
                                        usersClient.createOutcomeInvitation(user , userOutcomeInvitation);
                                        outcomeInvitations.add(userOutcomeInvitation);
                                    }
                                }

                                user = user.addTestData(
                                        new TestData(defaultPassword,
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                incomeInvitations,
                                                outcomeInvitations,
                                                friends));

                                context.getStore(NAMESPACE).put(
                                        context.getUniqueId(),
                                        user
                                );
                            }
                        }
                );


    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(
                extensionContext.getUniqueId(),
                UserJson.class
        );
    }
}
