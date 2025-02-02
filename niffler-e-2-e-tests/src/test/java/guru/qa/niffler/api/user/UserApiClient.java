package guru.qa.niffler.api.user;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.RegistrationModel;
import guru.qa.niffler.model.UserJson;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Config.getInstance().userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApi = retrofit.create(UserApi.class);

    private final Retrofit retrofitAuth = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Config.getInstance().authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApiAuth = retrofitAuth.create(UserApi.class);

    public String createUser(String username, String password) {
        final Response<String> response;
        try {
            RegistrationModel registrationModel = new RegistrationModel(username, password, password);
            response = userApiAuth.registerUser(registrationModel)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_CREATED, response.code());
        return response.body();
    }

    public UserJson updateUser(UserJson user) {
        final Response<UserJson> response;
        try {
            response = userApi.updateUser(user)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public UserJson getUser(String username) {
        final Response<UserJson> response;
        try {
            response = userApi.getUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public List<UserJson> getAllUsers(String username) {
        final Response<List<UserJson>> response;
        try {
            response = userApi.getUsers(username, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public UserJson sendInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.sendInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public UserJson acceptInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.acceptInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public UserJson declineInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.declineInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }
}
