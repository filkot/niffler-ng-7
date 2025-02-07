package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.user.UserApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;

import static guru.qa.niffler.utils.RandomDataUtils.getRandomUsername;


public class UsersAPIClient implements UsersClient {
    private static final Config CFG = Config.getInstance();

    private final UserApiClient userApiClient = new UserApiClient();

    @Override
    public UserJson createUser(String username, String password) {
        String user = userApiClient.createUser(username, password);
        return null;
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = getRandomUsername();
                String password = "12345";

                createUser(username, password);

                userApiClient.sendInvitation(targetUser.username(), username);
            }
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = getRandomUsername();
                String password = "12345";

                createUser(username, password);

                userApiClient.sendInvitation(username, targetUser.username());
            }
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = getRandomUsername();
                String password = "12345";

                createUser(username, password);
                userApiClient.sendInvitation(username, targetUser.username());
                userApiClient.sendInvitation(targetUser.username(), username);
                userApiClient.acceptInvitation(username, targetUser.username());
                userApiClient.acceptInvitation(targetUser.username(), username);
            }
        }
    }

    @Override
    public void createFriend(UserJson requester, UserJson addressee) {
        userApiClient.sendInvitation(requester.username(), addressee.username());
        userApiClient.sendInvitation(addressee.username(), requester.username());
        userApiClient.acceptInvitation(requester.username(), addressee.username());
        userApiClient.acceptInvitation(addressee.username(), requester.username());
    }

    @Override
    public void createIncomeInvitation(UserJson requester, UserJson addressee) {
        userApiClient.sendInvitation(requester.username(), addressee.username());
    }

    @Override
    public void createOutcomeInvitation(UserJson requester, UserJson addressee) {
        userApiClient.sendInvitation(addressee.username(), requester.username());

    }


}
