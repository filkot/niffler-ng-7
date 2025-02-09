package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;


public class JdbcTest {


    private static final UsersDbClient usersDbClient = new UsersDbClient();

    private static final UsersApiClient usersAPIClient = new UsersApiClient();

    @Test
    public void successfulTaTxTest() {
        String username = RandomDataUtils.getRandomUsername();
        String password = "12345";
        System.out.println("!!!!!!!! username = " + username);

        UserJson user = usersDbClient.createUser(username, password);
        usersDbClient.createIncomeInvitations(user, 1);
        usersDbClient.createOutcomeInvitations(user, 1);

        System.out.println("!!!!!!!! " + user);

    }

    @Test
    public void successApiTest() {
        String username = RandomDataUtils.getRandomUsername();
        String password = "12345";
        System.out.println("!!!!!!!! username = " + username);

        UserJson user = usersAPIClient.createUser(username, password);

        System.out.println("!!!!!!!! " + user);

    }


}
