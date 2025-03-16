package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED;
import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static guru.qa.niffler.utils.AssertionUtils.assertUserJsonEquals;
import static org.assertj.core.api.Assertions.assertThat;

@RestTest
public class FriendsV2RestTest {

    @RegisterExtension
    private static ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();


    @User(friends = 1, incomeInvitations = 1)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturned(UserJson user, @Token String token) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final UserJson expectedIncomeInvitation = user.testData().incomeInvitations().getFirst();

        final RestResponsePage<UserJson> response = gatewayApiClient
                .allFriends(token, 0, 10, "username,ASC", null);
        Assertions.assertEquals(2, response.getContent().size());


        final UserJson actualFriend = response.getContent().getLast();
        final UserJson actualIncomeInvitation = response.getContent().getFirst();
        assertUserJsonEquals(expectedFriend, actualFriend);
        assertUserJsonEquals(expectedIncomeInvitation, actualIncomeInvitation);
    }

    //    исходящих предложений дружить
    @User(outcomeInvitations = 1)
    @ApiLogin
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user, @Token String token) {
        final UserJson expectedOutcomeInvitation = user.testData().outcomeInvitations().getFirst();

        final int size = 10;
        final RestResponsePage<UserJson> response = gatewayApiClient
                .allUsers(token, 0, size, "username,ASC", null);
        Assertions.assertEquals(size, response.getContent().size());

        final UserJson actualOutcomeInvitation = response.getContent().getFirst();
        assertUserJsonEquals(expectedOutcomeInvitation, actualOutcomeInvitation, "friendshipStatus");
        assertThat(actualOutcomeInvitation.friendshipStatus()).isEqualTo(INVITE_SENT);
        assertThat(expectedOutcomeInvitation.friendshipStatus()).isEqualTo(INVITE_RECEIVED);
    }


}
