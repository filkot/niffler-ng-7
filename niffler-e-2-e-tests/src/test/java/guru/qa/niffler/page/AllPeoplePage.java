package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
    private final SearchField searchField = new SearchField();
    private final ElementsCollection allPeopleTableRows = $$("#all tr");

    public void shouldSeeOutcomeInvitationInAllPeoplesTable(String invitationFriendName) {
        // Вводим имя друга в поле поиска
        searchField.search(invitationFriendName);

        // Проверяем, что строка с приглашением отображается в таблице
        SelenideElement invitationRow = allPeopleTableRows.find(text(invitationFriendName));
        invitationRow.shouldBe(visible);

        // Проверяем статус приглашения
        String expectedStatus = "Waiting...";
        invitationRow.find("td", 1).shouldHave(text(expectedStatus));
    }
}
