package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {
    UserJson createUser(String username, String password);

    void createIncomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson targetUser, int count);

    void createFriends(UserJson targetUser, int count);

    void createFriend(UserJson requester, UserJson addressee);

    void createIncomeInvitation(UserJson requester, UserJson addressee);

    void createOutcomeInvitation(UserJson requester, UserJson addressee);
}
