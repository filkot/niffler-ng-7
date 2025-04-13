package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import guru.qa.niffler.userdata.wsdl.FriendshipStatus;
import guru.qa.niffler.userdata.wsdl.UserResponse;
import guru.qa.niffler.userdata.wsdl.UsersResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SoapTest
public class SoapUsersTest {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @User
    @Test
    void shouldReturnCurrentUser(UserJson user) throws IOException {
        UserResponse response = userdataSoapClient.currentUser(user.username());
        assertEquals(user.username(), response.getUser().getUsername());
    }

    @User(friends = 2)
    @Test
    void shouldReturnPaginatedFriends(UserJson user) throws IOException {
        UsersResponse response = userdataSoapClient
                .friendsWithPagination(user.username(), 0, 10, null);

        assertAll(
                () -> assertEquals(2, response.getTotalElements()),
                () -> assertEquals(1, response.getTotalPages()),
                () -> assertEquals(2, response.getUser().size())
        );
    }

    @User(friends = 5)
    @Test
    void shouldFilterByQuery(UserJson user) throws IOException {
        UsersResponse response = userdataSoapClient.friendsWithPagination(user.username(), 0, 10, ".");

        assertTrue(response.getUser().stream()
                .allMatch(u -> u.getUsername().contains(".")));
    }

    @User
    @Test
    void shouldSendInvitation(UserJson user) throws IOException {
        String friendToBeRequested = "filkot";
        UserResponse response = userdataSoapClient.sendInvitation(user.username(), friendToBeRequested);

        assertEquals(FriendshipStatus.INVITE_SENT, response.getUser().getFriendshipStatus());
        assertEquals(friendToBeRequested, response.getUser().getUsername());
    }

    @User(incomeInvitations = 1)
    @Test
    void shouldAcceptInvitation(UserJson user) throws IOException {
        String friendToBeAdded = user.testData().incomeInvitations().getFirst().username();
        UserResponse response = userdataSoapClient.acceptInvitation(user.username(), friendToBeAdded);

        assertEquals(FriendshipStatus.FRIEND, response.getUser().getFriendshipStatus());
        assertEquals(friendToBeAdded, response.getUser().getUsername());
    }

    @User(incomeInvitations = 1)
    @Test
    void shouldDeclineInvitation(UserJson user) throws IOException {
        String invitationToBeDeclined = user.testData().incomeInvitations().getFirst().username();
        UserResponse response = userdataSoapClient.declineInvitation(user.username(), invitationToBeDeclined);

        assertEquals(FriendshipStatus.VOID, response.getUser().getFriendshipStatus());
        assertEquals(invitationToBeDeclined, response.getUser().getUsername());
    }

    @User(friends = 1)
    @Test
    void shouldDeleteFriend(UserJson user) throws IOException {
        String friendToBeRemoved = user.testData().friends().getFirst().username();

        assertDoesNotThrow(() ->
                userdataSoapClient.removeFriend(user.username(), friendToBeRemoved)
        );

        UsersResponse verifyResponse = userdataSoapClient.friends(user.username());
        assertEquals(0, verifyResponse.getUser().size());
    }

    @Test
    void shouldFailForNonExistingUser() {
        assertThrows(IOException.class, () ->
                userdataSoapClient
                        .friendsWithPagination("non_existing_user", 0, 10, null)
        );
    }
}
