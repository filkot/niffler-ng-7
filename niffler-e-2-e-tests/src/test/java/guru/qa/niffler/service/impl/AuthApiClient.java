package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();

    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID = "client";
    private static final String SCOPE = "openid";
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String GRANT_TYPE = "authorization_code";


    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @Step("login user with username '{username}' using REST API")
    public String login(String username, String password) {
        final String codeVerifier = generateCodeVerifier();
        final String codeChallenge = generateCodeChallenge(codeVerifier);
        try {
            authApi.authorize(
                            RESPONSE_TYPE,
                            CLIENT_ID,
                            SCOPE,
                            REDIRECT_URI,
                            codeChallenge,
                            CODE_CHALLENGE_METHOD)
                    .execute();

            authApi.login(
                    username,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();

            Response<JsonNode> tokenResponse = authApi.token(
                    CLIENT_ID,
                    REDIRECT_URI,
                    GRANT_TYPE,
                    ApiLoginExtension.getCode(),
                    codeVerifier
            ).execute();

            return tokenResponse.body().get("id_token").asText();

        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }


}
