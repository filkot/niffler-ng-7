package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    private final SelenideElement alert;
    private final ElementsCollection formErrors;

    protected static final Config CFG = Config.getInstance();

    public abstract T checkThatPageLoaded();

    protected BasePage(SelenideDriver driver) {
        this.alert = driver.$(".MuiSnackbar-root");
        this.formErrors = driver.$$("p.Mui-error, .input__helper-text");
    }

    protected BasePage() {
        this.alert = Selenide.$(".MuiSnackbar-root");
        this.formErrors = Selenide.$$("p.Mui-error, .input__helper-text");
    }

    @Step("Check that alert message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(Condition.visible).should(Condition.text(expectedText));
        return (T) this;
    }

    @Step("Check that form error message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkFormErrorMessage(String... expectedText) {
        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }
}
