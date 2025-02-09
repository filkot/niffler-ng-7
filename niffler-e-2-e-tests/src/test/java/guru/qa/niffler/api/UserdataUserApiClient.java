package guru.qa.niffler.api;


import guru.qa.niffler.api.auth.AuthUserApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.user.UserdataUserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UserdataUserApiClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private final AuthUserApi authUserApi =
            new RestClient.EmptyRestClient(CFG.authUrl()).retrofit().create(AuthUserApi.class);
    private final UserdataUserApi userApiUserdata =
            new RestClient.EmptyRestClient(CFG.userdataUrl()).retrofit().create(UserdataUserApi.class);


    public @Nonnull UserJson createUser(String username, String password) {
        try {
            authUserApi.getCsrfToken().execute();
            authUserApi.registerUser(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();

            StopWatch sw = StopWatch.createStarted();
            while (sw.getTime(TimeUnit.MILLISECONDS) < 30) {
                UserJson createdUser = userApiUserdata.getUser(username).execute().body();

                if (createdUser != null && createdUser.id() != null) {
                    return createdUser.addTestData(
                            new TestData(defaultPassword,
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    new ArrayList<>()));
                } else {
                    Thread.sleep(100);
                }
            }
            throw new RuntimeException("Юзер не создался через API");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public @Nullable UserJson updateUser(UserJson user) {
        final Response<UserJson> response;
        try {
            response = userApiUserdata.updateUser(user)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public @Nullable UserJson getUser(String username) {
        final Response<UserJson> response;
        try {
            response = userApiUserdata.getUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public @Nonnull List<UserJson> getAllUsers(String username) {
        final Response<List<UserJson>> response;
        try {
            response = userApiUserdata.getUsers(username, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }

    public @Nullable UserJson sendInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApiUserdata.sendInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public @Nullable UserJson acceptInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApiUserdata.acceptInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public @Nullable UserJson declineInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApiUserdata.declineInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }
}
