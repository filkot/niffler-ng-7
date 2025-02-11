package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataUserApiClient;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;

import static guru.qa.niffler.utils.RandomDataUtils.getRandomUsername;


public class UsersApiClient implements UsersClient {
    private static final String defaultPassword = "12345";

    private final UserdataUserApiClient userApiClient = new UserdataUserApiClient();

    @Override
    public UserJson createUser(String username, String password) {
        return userApiClient.createUser(username, password);
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = getRandomUsername();

                createUser(username, defaultPassword);

                userApiClient.sendInvitation(targetUser.username(), username);
            }
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = getRandomUsername();

                createUser(username, defaultPassword);

                userApiClient.sendInvitation(username, targetUser.username());
            }
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = getRandomUsername();

                createUser(username, defaultPassword);
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
