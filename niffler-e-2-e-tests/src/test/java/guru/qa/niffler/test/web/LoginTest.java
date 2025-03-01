package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.convector.Browser;
import guru.qa.niffler.jupiter.convector.BrowserConverter;
import guru.qa.niffler.jupiter.extension.NonStaticBrowsersExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;

public class LoginTest {

    @RegisterExtension
    private static final NonStaticBrowsersExtension browserExtension = new NonStaticBrowsersExtension();
    //    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(chromeConfig);

    @User
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        browserExtension.drivers().add(chrome);

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        browserExtension.drivers().add(chrome);

        chrome.open(LoginPage.URL);
        new LoginPage(chrome)
                .fillLoginPage(randomUsername(), "BAD")
                .submit(new LoginPage(chrome))
                .checkError("Неверные учетные данные пользователя");
    }


    @ParameterizedTest
    @EnumSource(Browser.class)
    void userShouldStayOnLoginPageAfterLoginWithBadCredentialsMultiBrowsers(
            @ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        browserExtension.drivers().add(driver);

        driver.open(LoginPage.URL);
        new LoginPage(driver)
                .fillLoginPage(randomUsername(), "BAD")
                .submit(new LoginPage(driver))
                .checkError("Неверные учетные данные пользователя");
    }

}
