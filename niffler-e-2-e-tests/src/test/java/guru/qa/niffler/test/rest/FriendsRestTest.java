package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.model.FriendshipStatus.*;
import static guru.qa.niffler.utils.AssertionUtils.assertUserJsonEquals;
import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class FriendsRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @User(friends = 1, incomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldReturnFriendsAndIncomeInvitations(UserJson user, @Token String token) {
        UserJson expectedFriend = user.testData().friends().getFirst();
        UserJson expectedIncomeInvitation = user.testData().incomeInvitations().getFirst();

        List<UserJson> friendsAndInvitations = gatewayApiClient.allFriends(token, null);
        Assertions.assertEquals(2, friendsAndInvitations.size());

        UserJson actualFriend = friendsAndInvitations.getLast();
        UserJson actualIncomeInvitation = friendsAndInvitations.getFirst();

        assertUserJsonEquals(expectedFriend, actualFriend);
        assertUserJsonEquals(expectedIncomeInvitation, actualIncomeInvitation);
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void shouldDisplayFriendInFriendsList(UserJson user, @Token String token) {
        UserJson expectedFriend = user.testData().friends().getFirst();
        List<UserJson> friendsList = gatewayApiClient.allFriends(token, null);

        Assertions.assertEquals(1, friendsList.size());
        assertUserJsonEquals(expectedFriend, friendsList.getLast());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldDisplayIncomeInvitationInFriendsList(UserJson user, @Token String token) {
        UserJson expectedIncomeInvitation = user.testData().incomeInvitations().getFirst();
        List<UserJson> friendsAndInvitations = gatewayApiClient.allFriends(token, null);

        Assertions.assertEquals(1, friendsAndInvitations.size());

        final UserJson actualIncomeInvitation = friendsAndInvitations.getFirst();
        assertUserJsonEquals(expectedIncomeInvitation, actualIncomeInvitation);
        assertThat(expectedIncomeInvitation.friendshipStatus()).isEqualTo(INVITE_RECEIVED);
        assertThat(actualIncomeInvitation.friendshipStatus()).isEqualTo(INVITE_RECEIVED);

    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldAcceptFriendInvitation(UserJson user, @Token String token) {
        UserJson expectedFriend = user.testData().incomeInvitations().getFirst();
        UserJson acceptedFriend = gatewayApiClient.acceptInvitation(token, new FriendJson(expectedFriend.username()));

        assertUserJsonEquals(expectedFriend, acceptedFriend, "friendshipStatus");
        assertThat(acceptedFriend.friendshipStatus()).isEqualTo(FRIEND);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldDeclineFriendInvitation(UserJson user, @Token String token) {
        UserJson expectedInvitation = user.testData().incomeInvitations().getFirst();
        UserJson declinedInvitation = gatewayApiClient.declineInvitation(token, new FriendJson(expectedInvitation.username()));
        UserJson currentUser = gatewayApiClient.currentUser(token);

        assertThat(declinedInvitation.friendshipStatus()).isNull();
        assertThat(currentUser.friendshipStatus()).isNull();
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void shouldRemoveFriendFromFriendsList(UserJson user, @Token String token) {
        UserJson friendToRemove = user.testData().friends().getFirst();
        gatewayApiClient.deleteFriend(token, friendToRemove.username());

        UserJson currentUser = gatewayApiClient.currentUser(token);
        assertThat(currentUser.friendshipStatus()).isNull();
    }
}

