package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    private final SelenideElement emptyTabPanelText = $("#simple-tabpanel-friends").$("p");
    private final ElementsCollection friendsTableRows = $$("#friends tr");
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final SelenideElement searchInput = $("input[aria-label='search']");

    public void shouldSeeEmptyTabPanelFriends() {
        emptyTabPanelText.should(visible);
        String expected = "There are no users yet";
        emptyTabPanelText.shouldHave(text(expected));
    }

    public void shouldSeeFriendInFriendsTable(String friendName) {
        // Вводим имя друга в поле поиска
        searchInput.setValue(friendName);

        // Проверяем, что строка с именем друга отображается в таблице
        SelenideElement friendRow = friendsTableRows.find(text(friendName));
        friendRow.shouldBe(visible);
    }


    public void shouldSeeFriendNameRequestInRequestsTable(String friendNameRequest) {
        // Вводим имя друга в поле поиска
        searchInput.setValue(friendNameRequest);

        // Проверяем, что строка с запросом от друга отображается в таблице
        SelenideElement requestRow = requestsTableRows.find(text(friendNameRequest));
        requestRow.shouldBe(visible);
    }
}