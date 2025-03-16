package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {

    @Test
    @User(friends = 1)
    @ApiLogin()
    void oauthTest(@Token String token, UserJson user) {
        System.out.println(user);
        assertNotNull(token);
    }

    @Test
    @ApiLogin(username = "filkot", password = "12345")
    void oauthTest2(@Token String token, UserJson user) {
        System.out.println(user);
        assertNotNull(token);
    }
}
