package guru.qa.niffler.test.web.fake;

import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @Test
    void oauthTest() {
        final String codeVerifier = generateCodeVerifier();
        final String codeChallenge = generateCodeChallenge(codeVerifier);

        usersApiClient.authorize(codeChallenge);
        String code = usersApiClient.login("filkot", "12345");
        String token = usersApiClient.token(code, codeVerifier);
        assertNotNull(token);
    }
}
