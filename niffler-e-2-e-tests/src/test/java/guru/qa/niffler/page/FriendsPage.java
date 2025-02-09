package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage extends BasePage<FriendsPage> {

    private final SearchField searchField = new SearchField();

    private final SelenideElement emptyTabPanelText = $("#simple-tabpanel-friends").$("p");
    private final ElementsCollection friendsTableRows = $$("#friends tr");
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final SelenideElement popUp = $("div[role='dialog']");

    @Step("Проверяем, что панель друзей пуста")
    public void shouldSeeEmptyTabPanelFriends() {
        emptyTabPanelText.shouldBe(visible)
                .shouldHave(text("There are no users yet"));
    }

    @Step("Проверяем наличие друга {friendName} в таблице друзей")
    public void shouldSeeFriendInFriendsTable(String friendName) {
        searchAndFindRow(friendsTableRows, friendName).shouldBe(visible);
    }

    @Step("Проверяем наличие запроса от друга {friendNameRequest} в таблице запросов")
    public void shouldSeeFriendNameRequestInRequestsTable(String friendNameRequest) {
        searchAndFindRow(requestsTableRows, friendNameRequest).shouldBe(visible);
    }

    @Step("Проверяем количество друзей - {size}")
    public FriendsPage checkAmountOfFriends(int size) {
        friendsTableRows.shouldHave(size(size));
        return this;
    }

    @Step("Принимаем заявку в друзья от {username}")
    public FriendsPage acceptFriendInvitationFromUser(String username) {
        findRequestRow(username).$(byText("Accept")).click();
        return this;
    }

    @Step("Принимаем заявку в друзья")
    public FriendsPage acceptFriendInvitation() {
        findFirstRequestRow().$(byText("Accept")).click();
        return this;
    }

    @Step("Отклоняем заявку в друзья от {username}")
    public FriendsPage declineFriendInvitationFromUser(String username) {
        findRequestRow(username).$(byText("Decline")).click();
        confirmDecline();
        return this;
    }

    @Step("Отклоняем заявку в друзья")
    public FriendsPage declineFriendInvitation() {
        findFirstRequestRow().$(byText("Decline")).click();
        confirmDecline();
        return this;
    }

    private SelenideElement searchAndFindRow(ElementsCollection rows, String text) {
        searchField.search(text);
        return rows.find(text(text));
    }

    private SelenideElement findRequestRow(String username) {
        return requestsTableRows.find(text(username));
    }

    private SelenideElement findFirstRequestRow() {
        return requestsTableRows.first();
    }

    private void confirmDecline() {
        popUp.$(byText("Decline")).click();
    }
}