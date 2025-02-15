package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "filkot",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldNotPresentInCategoryList(CategoryJson[] category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("filkot", "12345")
                .addNewSpending()
                .shouldNotSeeArchivedCategoryInCategoryList(category[0].name());
    }

    @User(
            username = "filkot",
            categories = @Category
    )
    @Test
    void activeCategoryShouldPresentInCategoryList(CategoryJson[] category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("filkot", "12345")
                .addNewSpending()
                .shouldSeeActiveCategoryInCategoryList(category[0].name());
    }

    @User
    @Test
    void shouldUpdateProfileWithAllFieldsSet(UserJson user) {
        final String newName = RandomDataUtils.getRandomName();

        ProfilePage profilePage = Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfilePage()
                .uploadPhotoFromClasspath("img/cat.jpeg")
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        profilePage.checkName(newName)
                .checkPhotoExist();
    }

    @User
    @Test
    void shouldUpdateProfileWithOnlyRequiredFields(UserJson user) {
        final String newName = RandomDataUtils.getRandomName();

        ProfilePage profilePage = Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfilePage()
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        profilePage.checkName(newName);
    }

    @User
    @Test
    void shouldAddNewCategory(UserJson user) {
        final String newCategory = RandomDataUtils.getRandomCategory();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfilePage()
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
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfilePage()
                .checkThatCategoryInputDisabled();
    }
}