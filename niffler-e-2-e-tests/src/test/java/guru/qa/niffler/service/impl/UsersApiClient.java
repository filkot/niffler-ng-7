package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient.EmtyRestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.utils.OauthUtils.extractCodeFromUrl;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "client";
    private static final String SCOPE = "openid";
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String GRANT_TYPE = "authorization_code";

    private final AuthApi authApi = new EmtyRestClient(CFG.authUrl(), true).create(AuthApi.class);
    private final UserdataApi userdataApi = new EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);

    @Step("authorize with codeChallenge using REST API")
    public void authorize(String codeChallenge) {
        final Response<Void> response;
        try {
            response = authApi.authorize(
                            RESPONSE_TYPE,
                            CLIENT_ID,
                            SCOPE,
                            REDIRECT_URI,
                            codeChallenge,
                            CODE_CHALLENGE_METHOD)
                    .execute();

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }


    @Step("login user with username '{username}' using REST API")
    public String login(String username, String password) {
        final Response<Void> response;
        try {
            authApi.requestRegisterForm().execute();
            response = authApi.login(
                    username,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return extractCodeFromUrl(response.raw().request().url().toString());
    }


    @Step("get token using REST API")
    public String token(String code, String codeVerifier) {
        final Response<JsonNode> response;
        try {
            response = authApi.token(
                    CLIENT_ID,
                    REDIRECT_URI,
                    GRANT_TYPE,
                    code,
                    codeVerifier
            ).execute();

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        assertNotEquals(null, response.body());

        return response.body().path("id_token").asText();
    }

    @Override
    @Step("Create user with username '{username}' using REST API")
    @NotNull
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
            UserJson createdUser = requireNonNull(userdataApi.currentUser(username).execute().body());
            return createdUser.addTestData(
                    new TestData(
                            password
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Step("Add {1} income invitation(s) for user using REST API")
    public void addIncomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);

                    response = userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .incomeInvitations()
                        .add(newUser);
            }
        }
    }

    @Override
    @Step("Add {1} outcome invitation(s) for user using REST API")
    public void addOutcomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);

                    response = userdataApi.sendInvitation(
                            targetUser.username(),
                            newUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .outcomeInvitations()
                        .add(newUser);
            }
        }
    }

    @Override
    @Step("Add {1} friend(s) for user using REST API")
    public void addFriend(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                try {
                    userdataApi.sendInvitation(
                            createUser(
                                    username,
                                    defaultPassword
                            ).username(),
                            targetUser.username()
                    ).execute();
                    response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .friends()
                        .add(response.body());
            }
        }
    }

    @Step("Get all users for user using REST API")
    public @Nonnull List<UserJson> getAllUsers(String username) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.allUsers(username, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }
}
