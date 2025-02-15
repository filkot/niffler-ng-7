package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

    private final ElementsCollection tableRows = self.$$("tr");
    private final SelenideElement periodInput = $("#period");
    private final ElementsCollection dropdownList = $$("ul[role=listbox]");
    private final SelenideElement deleteBtn = $("#delete");
    private final SearchField searchField = new SearchField();

    private final SelenideElement popUp = $("div[role='dialog']");

    public SpendingTable() {
        super($("#spendings tbody"));
    }

    @Step("Выбираем период {period} в таблице трат")
    @Nonnull
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodInput.click();
        dropdownList.find(text(period.name())).click();
        return this;
    }

    @Step("Редактируем трату с описанием {description}")
    @Nonnull
    public EditSpendingPage editSpending(String description) {
        findSpendingRow(description).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Удаляем трату с описанием {description}")
    @Nonnull
    public SpendingTable deleteSpending(String description) {
        findSpendingRow(description).$$("td").get(0).click();
        deleteBtn.click();
        confirmDelete();
        return this;
    }

    @Step("Проверяем наличие траты с категорией {categoryName} и с описанием - {description}")
    @Nonnull
    public SpendingTable checkSpendingWith(String categoryName, String description) {
        findSpendingRow(description).$$("td").get(1).shouldHave(text(categoryName));
        return this;
    }

    @Step("Проверяем наличие траты с описанием - {spendingDescription}")
    @Nonnull
    public SpendingTable checkTableContainsSpending(String spendingDescription) {
        findSpendingRow(spendingDescription).shouldBe(visible);
        return this;
    }

    @Step("Проверяем наличие трат по описанию - {expectedSpends}")
    @Nonnull
    public SpendingTable checkTableContains(String... expectedSpends) {
        Set<String> actualSpends = tableRows
                .stream()
                .map(SelenideElement::getText)
                .collect(Collectors.toSet());
        assertTrue(
                Arrays.stream(expectedSpends).collect(Collectors.toSet())
                        .containsAll(actualSpends)
        );
        return this;
    }

    @Step("Проверяем количество трат в таблице - {expectedSize}")
    @Nonnull
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.shouldHave(size(expectedSize));
        return this;
    }

    @Step("Ищем трату с описанием {description}")
    @Nonnull
    private SelenideElement findSpendingRow(String description) {
        searchField.search(description);
        return tableRows.find(text(description));
    }

    private void confirmDelete() {
        popUp.$(byText("Delete")).click();
    }
}