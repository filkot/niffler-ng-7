package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.SelectField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {
    private final SelenideElement descriptionInput = $("#description");
    private final SelectField currencySelect = new SelectField($("#currency"));

    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement categoryInput = $("#category");
    private final ElementsCollection categories = $$(".MuiChip-root");

    private final SelenideElement cancelBtn = $("#cancel");
    private final SelenideElement saveBtn = $("#save");
    private final ElementsCollection categoryList = $$(By.xpath("//li//span"));


    private final Calendar calendar = new Calendar();

    @Step("Устанавливаем значение поля description {description}")
    @Nonnull
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Устанавливаем значения для полей {spend}")
    @Nonnull
    public EditSpendingPage fillPage(SpendJson spend) {
        return setNewSpendingDate(spend.spendDate())
                .setNewSpendingAmount(spend.amount())
                .setNewSpendingCurrency(spend.currency())
                .setNewSpendingCategory(spend.category().name())
                .setNewSpendingDescription(spend.description());
    }

    @Step("Устанавливаем значение поля currency {currency}")
    @Nonnull
    public EditSpendingPage setNewSpendingCurrency(CurrencyValues currency) {
        currencySelect.setValue(currency.name());
        return this;
    }

    @Step("Устанавливаем значение поля category {category}")
    @Nonnull
    public EditSpendingPage setNewSpendingCategory(String category) {
        categoryInput.clear();
        categoryInput.setValue(category);
        return this;
    }

    @Step("Устанавливаем значение поля amount {amount}")
    @Nonnull
    public EditSpendingPage setNewSpendingAmount(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Устанавливаем значение поля amount {amount}")
    @Nonnull
    public EditSpendingPage setNewSpendingAmount(int amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Устанавливаем значение поля date {date}")
    @Nonnull
    public EditSpendingPage setNewSpendingDate(Date date) {
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Step("Нажимаем submit для создания траты")
    @Nonnull
    public EditSpendingPage saveSpending() {
        saveBtn.click();
        return this;
    }

    public void shouldNotSeeArchivedCategoryInCategoryList(String category) {
        assertFalse(categoryList.stream().anyMatch(e -> e.text().equals(category)));
    }

    public void shouldSeeActiveCategoryInCategoryList(String category) {
        assertTrue(categoryList.stream().anyMatch(e -> e.text().equals(category)));
    }
}
