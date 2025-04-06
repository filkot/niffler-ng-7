package guru.qa.niffler.test.grpc;

import guru.qa.grpc.userdata.*;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserdataGrpcTest extends BaseGrpcTest {

    @User(friends = 2)
    @ApiLogin
    @Test
    void getFriendsPageable_shouldReturnPaginatedFriends(UserJson user) {
        PageableRequest request = PageableRequest.newBuilder()
                .setUsername(user.username())
                .setPage(1)
                .setSize(10)
                .build();

        UserPageResponse response = USERDATA_SERVICE_BLOCKING_STUB.getFriendsPageable(request);

        assertAll(
                () -> assertEquals(2, response.getTotalElements()),
                () -> assertEquals(1, response.getTotalPages()),
                () -> assertTrue(response.getFirst()),
                () -> assertTrue(response.getLast()),
                () -> assertEquals(2, response.getEdgesCount())
        );
    }

    @User(friends = 5)
    @ApiLogin
    @Test
    void searchFriends_shouldFilterByQuery(UserJson user) {
        SearchFriendsRequest request = SearchFriendsRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery("friend")
                .setPage(1)
                .setSize(10)
                .build();

        UserPageResponse response = USERDATA_SERVICE_BLOCKING_STUB.searchFriends(request);

        assertTrue(response.getEdgesList().stream()
                .allMatch(u -> u.getUsername().contains("friend")));
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void removeFriendship_shouldDeleteFriend(UserJson user) {
        String friendUsername = user.testData().friends().get(0).username();
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setFriendUsername(friendUsername)
                .build();

        assertDoesNotThrow(() -> {
            USERDATA_SERVICE_BLOCKING_STUB.removeFriendship(request);
        });

        UserPageResponse response = USERDATA_SERVICE_BLOCKING_STUB.getFriendsPageable(
                PageableRequest.newBuilder()
                        .setUsername(user.username())
                        .setPage(1)
                        .setSize(10)
                        .build()
        );
        assertEquals(0, response.getTotalElements());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void acceptFriendship_shouldChangeStatus(UserJson user) {
        String friendUsername = user.testData().incomeInvitations().get(0).username();
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setFriendUsername(friendUsername)
                .build();

        FriendshipResponse response = USERDATA_SERVICE_BLOCKING_STUB.acceptFriendship(request);

        assertEquals(FriendshipStatus.ACCEPTED, response.getStatus());
        assertEquals(friendUsername, response.getUser().getUsername());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void declineFriendship_shouldChangeStatus(UserJson user) {
        String friendUsername = user.testData().incomeInvitations().get(0).username();
        FriendshipRequest request = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setFriendUsername(friendUsername)
                .build();

        FriendshipResponse response = USERDATA_SERVICE_BLOCKING_STUB.declineFriendship(request);

        assertEquals(FriendshipStatus.DECLINED, response.getStatus());
        assertEquals(friendUsername, response.getUser().getUsername());
    }

    @User
    @ApiLogin
    @Test
    void sendFriendshipInvite_shouldCreateRequest(UserJson user) {
        String friendUsername = "new_friend";
        FriendshipInviteRequest request = FriendshipInviteRequest.newBuilder()
                .setUsername(user.username())
                .setFriendUsername(friendUsername)
                .setMessage("Let's be friends!")
                .build();

        FriendshipResponse response = USERDATA_SERVICE_BLOCKING_STUB.sendFriendshipInvite(request);

        assertEquals(FriendshipStatus.PENDING, response.getStatus());
        assertEquals(friendUsername, response.getUser().getUsername());
    }

    @Test
    @ApiLogin
    void getFriendsPageable_shouldFailForNonExistingUser() {
        PageableRequest request = PageableRequest.newBuilder()
                .setUsername("non_existing_user")
                .setPage(1)
                .setSize(10)
                .build();

        assertThrows(StatusRuntimeException.class, () ->
                USERDATA_SERVICE_BLOCKING_STUB.getFriendsPageable(request)
        );
    }
}