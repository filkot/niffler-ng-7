package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.SpendConditions.spends;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

    private final SearchField searchField = new SearchField();
    private final SelenideElement periodMenu = self.$("#period");
    private final SelenideElement currencyMenu = self.$("#currency");
    private final ElementsCollection menuItems = $$(".MuiList-padding li");
    private final SelenideElement deleteBtn = self.$("#delete");
    private final SelenideElement popup = $("div[role='dialog']");

    private final SelenideElement tableHeader = self.$(".MuiTableHead-root");
    private final ElementsCollection headerCells = tableHeader.$$(".MuiTableCell-root");

    private final ElementsCollection tableRows = self.$("tbody").$$("tr");


    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Select table period '{period}'")
    @Nonnull
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodMenu.click();
        menuItems.find(text(period.text)).click();
        return this;
    }

    @Step("Edit spending with description '{description}'")
    @Nonnull
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Step("Delete spending with description '{description}'")
    @Nonnull
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(0).click();
        deleteBtn.click();
        popup.$(byText("Delete")).click(usingJavaScript());
        return this;
    }

    @Step("Search spending with description '{description}'")
    @Nonnull
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Check that table contains data '{expectedSpend}'")
    @Nonnull
    public SpendingTable checkTableContains(String expectedSpend) {
        searchSpendingByDescription(expectedSpend);
        tableRows.find(text(expectedSpend)).should(visible);
        return this;
    }

    @Step("Check that table have size '{expectedSize}'")
    @Nonnull
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.should(size(expectedSize));
        return this;
    }

    @Step("Check spending table")
    @Nonnull
    public SpendingTable checkTable(SpendJson... expectedSpends) {
        tableRows.should(spends(expectedSpends));
        return this;
    }
}
