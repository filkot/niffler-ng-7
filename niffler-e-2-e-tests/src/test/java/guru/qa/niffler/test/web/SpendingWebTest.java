package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

@WebTest
public class SpendingWebTest {

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(MainPage.URL, MainPage.class)
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingDescription(newDescription)
                .saveSpending();

        new MainPage().getSpendingTable()
                .checkTableContains(newDescription);
    }

    @User
    @ApiLogin
    @Test
    void shouldAddNewSpending(UserJson user) {
        String category = "Friends";
        int amount = 100;
        Date currentDate = new Date();
        String description = RandomDataUtils.randomSentence(3);

        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingCategory(category)
                .setNewSpendingAmount(amount)
                .setNewSpendingDate(currentDate)
                .setNewSpendingDescription(description)
                .saveSpending()
                .checkAlertMessage("New spending is successfully created");

        new MainPage().getSpendingTable()
                .checkTableContains(description);
    }

    @User
    @ApiLogin
    @Test
    void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingAmount(100)
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Please choose category");
    }

    @User
    @ApiLogin
    @Test
    void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingCategory("Friends")
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Amount has to be not less then 0.01");
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void deleteSpendingTest(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getSpendingTable()
                .deleteSpending(user.testData().spends().getFirst().description())
                .checkTableSize(0);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990),
                    @Spending(
                            category = "Магазины",
                            description = "Продукты",
                            amount = 50000)
            }
    )
    @ApiLogin
    @ScreenShotTest(value = "img/expected-archived-stat.png", rewriteExpected = true)
    void checkBubblesWithArchivedCategoriesTest(UserJson user) {
        String archivedCategory = user.testData().spends().getLast().category().name();

        Bubble bubble1 = new Bubble(Color.yellow, String.format("%s %.0f ₽",
                user.testData().spends().getFirst().category().name(),
                user.testData().spends().getFirst().amount()));
        Bubble bubble2 = new Bubble(Color.green, String.format("%s %.0f ₽",
                "Archived",
                user.testData().spends().getLast().amount()));

        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .archiveCategory(archivedCategory)
                .getHeader()
                .toMainPage()
                .getStatComponent()
                .checkBubbles(bubble1, bubble2)
                .checkBubblesInAnyOrder(bubble2, bubble1)
                .checkBubblesContains(bubble2);
    }


    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990),
                    @Spending(
                            category = "Магазины",
                            description = "Продукты",
                            amount = 50000)
            }
    )
    @ApiLogin
    @Test
    void checkSpendingTableTest(UserJson user) {
        List<SpendJson> spends = user.testData().spends();

        Selenide.open(MainPage.URL, MainPage.class)
                .getSpendingTable()
                .checkTable(spends.toArray(new SpendJson[0]));
    }
}
