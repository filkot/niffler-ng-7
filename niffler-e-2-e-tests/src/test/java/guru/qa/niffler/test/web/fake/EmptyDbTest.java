package guru.qa.niffler.test.web.fake;

import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
public class EmptyDbTest {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @Test
    void checkEmptyUsersList() {
        List<UserJson> users = usersApiClient.getAllUsers("filkot");
        assertTrue(users.isEmpty());
    }

}
