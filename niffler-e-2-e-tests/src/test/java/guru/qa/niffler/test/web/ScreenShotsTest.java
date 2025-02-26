package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.component.StatComponent;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class ScreenShotsTest {


    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage());

        StatComponent statComponent = new MainPage().getStatComponent();
        statComponent.checkStatImage(expected);
    }


    @User
    @ScreenShotTest(value = "img/expected-avatar.png")
    void checkAvatarTest(UserJson user, BufferedImage expected) throws IOException {

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toProfilePage()
                .uploadPhotoFromClasspath("img/cat.jpeg")
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();
        new ProfilePage().checkAvatarImage(expected);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest(value = "img/expected-edited-stat.png", rewriteExpected = true)
    void checkStatComponentAfterEditSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getSpendingTable()
                .editSpending(user.testData().spends().getFirst().description())
                .setNewSpendingAmount(50000)
                .saveSpending();

        new MainPage().getStatComponent()
                .checkStatImage(expected);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990),
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990)
            }
    )
    @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
    void checkStatComponentAfterDeletedSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getSpendingTable()
                .deleteSpending(user.testData().spends().getFirst().description());

        new MainPage().getStatComponent()
                .checkStatImage(expected);
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
    void checkStatComponentWithArchivedCategoriesTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getHeader()
                .toProfilePage()
                .archiveCategory(user.testData().spends().getLast().category().name())
                .getHeader()
                .toMainPage()
                .getStatComponent()
                .checkStatImage(expected);
    }
}
