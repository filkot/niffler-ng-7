package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayV2ApiClient extends RestClient {

    private final GatewayV2Api gatewayApi;

    public GatewayV2ApiClient() {
        super(CFG.gatewayUrl());
        gatewayApi = create(GatewayV2Api.class);
    }

    @Step("Sent GET request /api/v2/friends/all to niffler-gateway")
    @Nonnull
    public RestResponsePage<UserJson> allFriends(String bearerToken,
                                                 int page,
                                                 int size,
                                                 @Nullable String sort,
                                                 @Nullable String searchQuery) {
        final Response<RestResponsePage<UserJson>> response;
        try {
            response = gatewayApi.allFriends(bearerToken, page, size, sort, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

    @Step("Sent GET request /api/v2/users/all to niffler-gateway")
    @Nonnull
    public RestResponsePage<UserJson> allUsers(String bearerToken,
                                               int page,
                                               int size,
                                               @Nullable String sort,
                                               @Nullable String searchQuery) {
        final Response<RestResponsePage<UserJson>> response;
        try {
            response = gatewayApi.allUsers(bearerToken, page, size, sort, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(SC_OK, response.code());
        return requireNonNull(response.body());
    }

}
