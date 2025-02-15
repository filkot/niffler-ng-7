package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    private final SelenideElement alert = $(".MuiAlert-message");
    private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

    @Step("Проверка, что alert появился и содержит текст {text}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkAlertMessage(String text) {
        alert.should(visible).should(text(text));
        return (T) this;
    }

    @Step("Проверка, что formErrors появился и содержит текст {text}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkFormErrorMessage(String... text) {
        formErrors.should(CollectionCondition.textsInAnyOrder(text));
        return (T) this;
    }
}
