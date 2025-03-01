package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;
import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;

public class ProfileTest {

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(chromeConfig);

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        browserExtension.drivers().add(chrome);
        final String categoryName = user.testData().categoryDescriptions()[0];

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded();

        chrome.open(ProfilePage.URL);
        new ProfilePage()
                .checkArchivedCategoryExists(categoryName);
    }

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        browserExtension.drivers().add(chrome);
        final String categoryName = user.testData().categoryDescriptions()[0];

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded();

        chrome.open(ProfilePage.URL);
        new ProfilePage()
                .checkCategoryExists(categoryName);
    }

    @User
    @Test
    void shouldUpdateProfileWithAllFieldsSet(UserJson user) {
        browserExtension.drivers().add(chrome);
        final String newName = randomName();

        chrome.open(LoginPage.URL);
        ProfilePage profilePage =
                new LoginPage(chrome)
                        .fillLoginPage(user.username(), user.testData().password())
                        .submit(new MainPage())
                        .checkThatPageLoaded()
                        .getHeader()
                        .toProfilePage()
                        .uploadPhotoFromClasspath("img/cat.jpeg")
                        .setName(newName)
                        .submitProfile()
                        .checkAlertMessage("Profile successfully updated");

        chrome.refresh();

        profilePage.checkName(newName)
                .checkPhotoExist();
    }

    @User
    @Test
    void shouldUpdateProfileWithOnlyRequiredFields(UserJson user) {
        browserExtension.drivers().add(chrome);
        final String newName = randomName();

        chrome.open(LoginPage.URL);
        ProfilePage profilePage =
                new LoginPage(chrome)
                        .fillLoginPage(user.username(), user.testData().password())
                        .submit(new MainPage())
                        .checkThatPageLoaded()
                        .getHeader()
                        .toProfilePage()
                        .setName(newName)
                        .submitProfile()
                        .checkAlertMessage("Profile successfully updated");

        chrome.refresh();

        profilePage.checkName(newName);
    }

    @User
    @Test
    void shouldAddNewCategory(UserJson user) {
        browserExtension.drivers().add(chrome);
        String newCategory = randomCategoryName();

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded()
                .getHeader()
                .toProfilePage()
                .addCategory(newCategory)
                .checkAlertMessage("You've added new category:")
                .checkCategoryExists(newCategory);
    }

    @User(
            categories = {
                    @Category(name = "Food"),
                    @Category(name = "Bars"),
                    @Category(name = "Clothes"),
                    @Category(name = "Friends"),
                    @Category(name = "Music"),
                    @Category(name = "Sports"),
                    @Category(name = "Walks"),
                    @Category(name = "Books")
            }
    )
    @Test
    void shouldForbidAddingMoreThat8Categories(UserJson user) {
        browserExtension.drivers().add(chrome);
        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded()
                .getHeader()
                .toProfilePage()
                .checkThatCategoryInputDisabled();
    }
}
