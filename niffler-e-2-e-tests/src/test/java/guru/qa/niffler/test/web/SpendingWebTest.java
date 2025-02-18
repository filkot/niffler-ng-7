package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingDescription(newDescription)
                .saveSpending();

        new MainPage().getSpendingTable()
                .checkTableContains(newDescription);
    }

    @User
    @Test
    void shouldAddNewSpending(UserJson user) {
        String category = "Friends";
        int amount = 100;
        Date currentDate = new Date();
        String description = RandomDataUtils.randomSentence(3);

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .addSpendingPage()
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
    @Test
    void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .addSpendingPage()
                .setNewSpendingAmount(100)
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Please choose category");
    }

    @User
    @Test
    void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .addSpendingPage()
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
    @Test
    void deleteSpendingTest(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
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
    @ScreenShotTest(value = "img/expected-archived-stat.png", rewriteExpected = true)
    void checkBubblesWithArchivedCategoriesTest(UserJson user) {
        String archivedCategory = user.testData().spends().getLast().category().name();

        List<String> expectedBubbles = new ArrayList<>();
        for (SpendJson spend : user.testData().spends()) {
            String category = spend.category().name().equals(archivedCategory) ? "Archived" : spend.category().name();
            double amount = spend.amount();
            String formattedString = String.format("%s %.0f ₽", category, amount);
            expectedBubbles.add(formattedString);
        }

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toProfilePage()
                .archiveCategory(archivedCategory)
                .getHeader()
                .toMainPage()
                .getStatComponent()
                .checkBubbles(expectedBubbles);
    }
}

