package guru.qa.niffler.test.fake;

import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Isolated
public class FullDbTest {

    private final UsersApiClient usersApiClient = new UsersApiClient();


    @Test
    void checkNotEmptyUsersList() {
        List<UserJson> users = usersApiClient.getAllUsers("filkot");
        assertFalse(users.isEmpty());
    }
}
