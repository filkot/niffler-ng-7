package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

    private final SelenideElement menuBtn = self.find("button");
    private final SelenideElement headerMenu = $("ul[role='menu']");
    private final SelenideElement profileBtn = headerMenu.findAll("li").find(text("Profile"));
    private final SelenideElement friendsBtn = headerMenu.findAll("li").find(text("Friends"));
    private final SelenideElement allPeopleBtn = headerMenu.findAll("li").find(text("All People"));
    private final SelenideElement signOutBtn = headerMenu.findAll("li").find(text("Sign out"));
    private final SelenideElement addSpendingLnk = self.find("a[href='/spending']");
    private final SelenideElement mainPageLnk = self.find("a[href='/main']");

    public Header() {
        super($("#root header"));
    }

    @Step("Открываем страницу Profile")
    @Nonnull
    public ProfilePage goToProfilePage() {
        openMenuAndClick(profileBtn);
        return new ProfilePage();
    }

    @Step("Открываем страницу Friends")
    @Nonnull
    public FriendsPage goToFriendsPage() {
        openMenuAndClick(friendsBtn);
        return new FriendsPage();
    }

    @Step("Открываем страницу All People")
    @Nonnull
    public AllPeoplePage goToAllPeoplePage() {
        openMenuAndClick(allPeopleBtn);
        return new AllPeoplePage();
    }

    @Step("Делаем Log out")
    @Nonnull
    public LoginPage signOut() {
        openMenuAndClick(signOutBtn);
        return new LoginPage();
    }

    @Step("Добавляем новый Spending")
    @Nonnull
    public EditSpendingPage addSpending() {
        addSpendingLnk.click();
        return new EditSpendingPage();
    }

    @Step("Открываем главную страницу")
    @Nonnull
    public MainPage goToMainPage() {
        mainPageLnk.click();
        return new MainPage();
    }

    private void openMenuAndClick(SelenideElement element) {
        menuBtn.click();
        element.click();
    }
}