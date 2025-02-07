package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
    private final ElementsCollection allPeopleTableRows = $$("#all tr");
    private final SelenideElement searchInput = $("input[aria-label='search']");

    public void shouldSeeOutcomeInvitationInAllPeoplesTable(String invitationFriendName) {
        // Вводим имя друга в поле поиска
        searchInput.setValue(invitationFriendName);

        // Проверяем, что строка с приглашением отображается в таблице
        SelenideElement invitationRow = allPeopleTableRows.find(text(invitationFriendName));
        invitationRow.shouldBe(visible);

        // Проверяем статус приглашения
        String expectedStatus = "Waiting...";
        invitationRow.find("td", 1).shouldHave(text(expectedStatus));
    }
}
