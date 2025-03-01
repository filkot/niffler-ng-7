package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;

public class RegistrationTest {

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(chromeConfig);

    @Test
    void shouldRegisterNewUser() {
        browserExtension.drivers().add(chrome);
        String newUsername = randomUsername();
        String password = "12345";
        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .doRegister()
                .fillRegisterPage(newUsername, password, password)
                .successSubmit()
                .fillLoginPage(newUsername, password)
                .submit(new MainPage())
                .checkThatPageLoaded();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        browserExtension.drivers().add(chrome);
        String existingUsername = "duck";
        String password = "12345";

        chrome.open(LoginPage.URL);
        LoginPage loginPage = new LoginPage(chrome);
        loginPage.doRegister()
                .fillRegisterPage(existingUsername, password, password)
                .errorSubmit();
        loginPage.checkError("Username `" + existingUsername + "` already exists");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        browserExtension.drivers().add(chrome);
        String newUsername = randomUsername();
        String password = "12345";

        chrome.open(LoginPage.URL);
        LoginPage loginPage = new LoginPage(chrome);
        loginPage.doRegister()
                .fillRegisterPage(newUsername, password, "bad password submit")
                .errorSubmit();
        loginPage.checkError("Passwords should be equal");
    }
}
