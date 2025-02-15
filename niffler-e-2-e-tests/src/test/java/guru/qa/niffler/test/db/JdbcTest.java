package guru.qa.niffler.test.db;

import guru.qa.niffler.jupiter.extension.UsersClientExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersClientExtension.class)
public class JdbcTest {

    private UsersClient usersClient;

    @Test
    public void successfulTaTxTest() {
        String username = RandomDataUtils.getRandomUsername();
        String password = "12345";
        System.out.println("!!!!!!!! username = " + username);

        UserJson user = usersClient.createUser(username, password);
        usersClient.createIncomeInvitations(user, 1);
        usersClient.createOutcomeInvitations(user, 1);

        System.out.println("!!!!!!!! " + user);

    }

    @Test
    public void successApiTest() {
        String username = RandomDataUtils.getRandomUsername();
        String password = "12345";
        System.out.println("!!!!!!!! username = " + username);

        UserJson user = usersClient.createUser(username, password);

        System.out.println("!!!!!!!! " + user);

    }


}
