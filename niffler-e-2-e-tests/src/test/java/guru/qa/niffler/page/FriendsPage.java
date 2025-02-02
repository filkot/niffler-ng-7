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
    private final SelenideElement nextPageBtn = $("#page-next");
    private final SelenideElement prevPageBtn = $("#page-prev");

    public void shouldSeeEmptyTabPanelFriends() {
        emptyTabPanelText.should(visible);
        String expected = "There are no users yet";
        emptyTabPanelText.shouldHave(text(expected));
    }

    public void shouldSeeFriendInFriendsTable(String friendName) {
        boolean friendFound = false;

        SelenideElement invitationRow = friendsTableRows.find(text(friendName));
        invitationRow.should(visible);

        // Поиск на текущей странице
        if (invitationRow.exists()) {
            friendFound = true;
        }

        // Если друг не найден и кнопка "nextPageBtn" активна, переходим на следующую страницу
        while (!friendFound && nextPageBtn.isEnabled()) {
            nextPageBtn.click(); // Переход на следующую страницу

            // Поиск на новой странице
            if (friendsTableRows.find(text(friendName)).exists()) {
                friendsTableRows.find(text(friendName)).should(visible);
                friendFound = true;
            }
        }

        // Если друг так и не найден, выбрасываем исключение
        if (!friendFound) {
            throw new AssertionError("Friend '" + friendName + "' not found in the friends table.");
        }
    }

    public void shouldSeeFriendNameRequestInRequestsTable(String friendNameRequest) {
        boolean requestFound = false;

        SelenideElement invitationRow = requestsTableRows.find(text(friendNameRequest));
        invitationRow.should(visible);

        // Поиск на текущей странице
        if (invitationRow.exists()) {
            requestFound = true;
        }

        // Если запрос не найден и кнопка "nextPageBtn" активна, переходим на следующую страницу
        while (!requestFound && nextPageBtn.isEnabled()) {
            nextPageBtn.click(); // Переход на следующую страницу

            // Поиск на новой странице
            if (requestsTableRows.find(text(friendNameRequest)).exists()) {
                requestsTableRows.find(text(friendNameRequest)).should(visible);
                requestFound = true;
            }
        }

        // Если запрос так и не найден, выбрасываем исключение
        if (!requestFound) {
            throw new AssertionError("Friend request from '" + friendNameRequest + "' not found in the requests table.");
        }
    }
}