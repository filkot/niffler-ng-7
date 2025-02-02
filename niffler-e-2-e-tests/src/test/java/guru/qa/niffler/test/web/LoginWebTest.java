package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;

@WebTest
public class LoginWebTest {

    private static final Config CFG = Config.getInstance();


    @User(
            categories = {
                    @Category(
                            name = "Магазины", archived = false
                    ),
                    @Category(
                            name = "Бары", archived = true
                    )
            },
            spendings = {
                    @Spending(
                            category = "Обучение",
                            amount = 80000,
                            description = "qaguru Advanced 7"
                    )
            }
    )
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatMainPageVisible();
    }


    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(RandomDataUtils.getRandomUsername(),
                        RandomDataUtils.getRandomPassword());
        new LoginPage().shouldSeeErrorWithBadCredentialsText();
    }


}