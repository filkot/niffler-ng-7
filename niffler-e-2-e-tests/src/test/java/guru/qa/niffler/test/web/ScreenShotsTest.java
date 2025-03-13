package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
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
    @ApiLogin
    @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
    void checkStatComponentTest(BufferedImage expected) throws IOException {
        StatComponent statComponent =
                Selenide.open(MainPage.URL, MainPage.class).getStatComponent();
        statComponent.checkStatImage(expected);
    }


    @User
    @ApiLogin
    @ScreenShotTest(value = "img/expected-avatar.png")
    void checkAvatarTest(BufferedImage expected) throws IOException {

        Selenide.open(ProfilePage.URL, ProfilePage.class)
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
    @ApiLogin
    @ScreenShotTest(value = "img/expected-edited-stat.png", rewriteExpected = true)
    void checkStatComponentAfterEditSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(MainPage.URL, MainPage.class)
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
    @ApiLogin
    @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
    void checkStatComponentAfterDeletedSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage
                .getSpendingTable()
                .deleteSpending(user.testData().spends().getFirst().description());

        Selenide.refresh();

        mainPage.getStatComponent()
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
    @ApiLogin
    @ScreenShotTest(value = "img/expected-archived-stat.png", rewriteExpected = true)
    void checkStatComponentWithArchivedCategoriesTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .archiveCategory(user.testData().spends().getLast().category().name())
                .getHeader()
                .toMainPage()
                .getStatComponent()
                .checkStatImage(expected);
    }
}