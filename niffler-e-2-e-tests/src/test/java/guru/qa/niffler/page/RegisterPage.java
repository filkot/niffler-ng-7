package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {
    private static final Config CFG = Config.getInstance();
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement successText = $(".form__paragraph_success");
    private final SelenideElement errorText = $(".form__error");

    public static RegisterPage register(String username, String password, String passwordSubmit) {
        return Selenide.open(CFG.frontUrl(), LoginPage.class)
                .openRegistrationPage()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(passwordSubmit)
                .submitRegistration();
    }

    public RegisterPage setUsername(String username) {
        usernameInput.clear();
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.clear();
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.clear();
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    public RegisterPage submitRegistration() {
        submitButton.click();
        return this;
    }

    public void shouldSeeSuccessRegistrationText() {
        successText.should(visible);
    }

    public void shouldSeeUsernameAlreadyExistErrorText(String username) {
        errorText.should(visible);
        String expected = "Username `" + username + "` already exists";
        assertEquals(expected, errorText.getText());
    }

    public void shouldSeePasswordsShouldBeEqualErrorText() {
        errorText.should(visible);
        String expected = "Passwords should be equal";
        errorText.shouldHave(text(expected));
    }
}
