package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        gatewayApi = create(GatewayApi.class);
    }

    @Step("Sent GET request /api/friends/all to niffler-gateway")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = gatewayApi.allFriends(bearerToken, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Step("Sent DELETE request /api/friends/remove to niffler-gateway")
    public void deleteFriend(String bearerToken, @Nullable String targetUsername) {
        final Response<Void> response;
        try {
            response = gatewayApi.removeFriend(bearerToken, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
    }

    @Step("Sent POST request /api/invitations/send to niffler-gateway")
    @Nonnull
    public UserJson sendInvitation(String bearerToken, FriendJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.sendInvitation(bearerToken, friend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Step("Sent POST request /api/invitations/accept to niffler-gateway")
    @Nonnull
    public UserJson acceptInvitation(String bearerToken, FriendJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.acceptInvitation(bearerToken, friend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Step("Sent POST request /api/invitations/decline to niffler-gateway")
    @Nonnull
    public UserJson declineInvitation(String bearerToken, FriendJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.declineInvitation(bearerToken, friend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }


    @Step("Sent GET request /api/users/all to niffler-gateway")
    @Nonnull
    public List<UserJson> allUsers(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = gatewayApi.allUsers(bearerToken, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }


    @Step("Sent GET request /api/users/current to niffler-gateway")
    @Nonnull
    public UserJson currentUser(String bearerToken) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.currentUser(bearerToken)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

}
