package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerLink = $(".form__register");
    private final SelenideElement errorText = $(".form__error-container");


    @Step("Заполняем username: {username} password: {password} и жмем Log in")
    @Nonnull
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Nonnull
    public RegisterPage openRegistrationPage() {
        registerLink.click();
        return new RegisterPage();
    }

    @Step("Проверка сообщении об ошибке с текстом {error}")
    public void shouldSeeErrorWithBadCredentialsText(String errorMessage) {
        errorText.should(visible);
        errorText.shouldHave(text(errorMessage));
    }
}
