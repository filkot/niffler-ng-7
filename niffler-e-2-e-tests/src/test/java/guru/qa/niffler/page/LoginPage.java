package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitButton;
    private final SelenideElement registerButton;
    private final SelenideElement errorContainer;

    public LoginPage(SelenideDriver chrome) {
        super(chrome);
        this.usernameInput = chrome.$("input[name='username']");
        this.passwordInput = chrome.$("input[name='password']");
        this.submitButton = chrome.$("button[type='submit']");
        this.registerButton = chrome.$("a[href='/register']");
        this.errorContainer = chrome.$(".form__error");
    }

    public LoginPage() {
        this.usernameInput = Selenide.$("input[name='username']");
        this.passwordInput = Selenide.$("input[name='password']");
        this.submitButton = Selenide.$("button[type='submit']");
        this.registerButton = Selenide.$("a[href='/register']");
        this.errorContainer = Selenide.$(".form__error");
    }

    @Nonnull
    public RegisterPage doRegister() {
        registerButton.click();
        return new RegisterPage();
    }

    @Step("Fill login page with credentials: username: '{login}', password: {password}")
    @Nonnull
    public LoginPage fillLoginPage(String login, String password) {
        setUsername(login);
        setPassword(password);
        return this;
    }

    @Step("Set username: '{username}'")
    @Nonnull
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: '{password}'")
    @Nonnull
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Submit login")
    @Nonnull
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
    }

    @Step("Check error on page: {error}")
    @Nonnull
    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }

    @Override
    @Step("Check that page is loaded")
    @Nonnull
    public LoginPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        return this;
    }
}
