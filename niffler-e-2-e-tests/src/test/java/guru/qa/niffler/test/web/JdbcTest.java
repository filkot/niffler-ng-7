package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class JdbcTest {


    @Test
    void addFriendInvitationIncomeTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        String requesterUsername = "dima";
        String addresseeUsername = "bee";

        usersDbClient.addIncomeInvitation(requesterUsername, addresseeUsername);
    }

    @Test
    void addFriendInvitationOutcomeTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        String requesterUsername = "barsik";
        String addresseeUsername = "filkot";

        usersDbClient.addOutcomeInvitation(requesterUsername, addresseeUsername);
    }

    @Test
    void addFriendTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        String requesterUsername = "dima";
        String addresseeUsername = "duck";

        usersDbClient.addFriend(requesterUsername, addresseeUsername);
    }

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        String username = "filkot";
        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                RandomDataUtils.getRandomCategory(),
                                username,
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        RandomDataUtils.getRandomSentence(3),
                        username
                )
        );

        System.out.println(spend);
    }


    @Test
    public void successfulTaTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        String username = RandomDataUtils.getRandomUsername();
        System.out.println("!!!!!!!! username = " + username);

        UserJson user = usersDbClient.createCorrectUserSpringJdbc(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println("!!!!!!!! " + user);
        assertTrue(usersDbClient.findByUsername(username).isPresent());
    }

    @Test
    public void unsuccessfulTaTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        String username = RandomDataUtils.getRandomUsername();
        System.out.println("!!!!!!!! username = " + username);

        try {
            UserJson user = usersDbClient.createIncorrectUserSpringJdbc(
                    new UserJson(
                            null,
                            username,
                            null,
                            null,
                            null,
                            CurrencyValues.RUB,
                            null,
                            null,
                            null
                    )
            );
            System.out.println("!!!!!!!! " + user);
        } catch (Exception e) {
            //NOP
        }
    }


    @Test
    void springChainedManagerWithIncorrectDataTest() {
        UsersDbClient userDbClient = new UsersDbClient();
        String username = RandomDataUtils.getRandomUsername();
        UserJson user = userDbClient.createWithChainedTxManager(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Negative Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        System.out.println(user);
    }
}
