package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    private final Header header = new Header();
    private final SpendingTable spendings = new SpendingTable();


    private final SelenideElement historyOfSpending = $("#spendings");
    private final SelenideElement statistic = $("#stat");

    @Nonnull
    public SpendingTable spendingTable() {
        return spendings;
    }

    @Nonnull
    public EditSpendingPage editSpending(String spendingDescription) {
        return spendings.editSpending(spendingDescription);
    }

    @Nonnull
    public EditSpendingPage addNewSpending() {
        return header.addSpending();
    }

    @Nonnull
    public ProfilePage openProfilePage() {
        return header.goToProfilePage();
    }

    @Nonnull
    public FriendsPage openFriendsPage() {
        return header.goToFriendsPage();
    }

    @Nonnull
    public AllPeoplePage openAllPeoplePage() {
        return header.goToAllPeoplePage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        spendings.checkTableContainsSpending(spendingDescription);
    }

    public void checkThatMainPageVisible() {
        historyOfSpending.should(visible);
        statistic.should(visible);
    }
}
