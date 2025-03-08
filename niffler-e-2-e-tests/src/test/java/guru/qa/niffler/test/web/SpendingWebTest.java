package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Date;
import java.util.List;

import static guru.qa.niffler.jupiter.convector.Browser.chromeConfig;


public class SpendingWebTest {

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(chromeConfig);

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
        browserExtension.addDriver(chrome);
        final String newDescription = "Обучение Niffler Next Generation";

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
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
        browserExtension.addDriver(chrome);
        String category = "Friends";
        int amount = 100;
        Date currentDate = new Date();
        String description = RandomDataUtils.randomSentence(3);

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
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
        browserExtension.addDriver(chrome);

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
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
        browserExtension.addDriver(chrome);

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
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
        browserExtension.addDriver(chrome);

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
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
        browserExtension.addDriver(chrome);
        String archivedCategory = user.testData().spends().getLast().category().name();

        Bubble bubble1 = new Bubble(Color.yellow, String.format("%s %.0f ₽",
                user.testData().spends().getFirst().category().name(),
                user.testData().spends().getFirst().amount()));
        Bubble bubble2 = new Bubble(Color.green, String.format("%s %.0f ₽",
                "Archived",
                user.testData().spends().getLast().amount()));

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toProfilePage()
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
    @Test
    void checkSpendingTableTest(UserJson user) {
        browserExtension.addDriver(chrome);
        List<SpendJson> spends = user.testData().spends();

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getSpendingTable()
                .checkTable(spends.toArray(new SpendJson[0]));
    }
}

