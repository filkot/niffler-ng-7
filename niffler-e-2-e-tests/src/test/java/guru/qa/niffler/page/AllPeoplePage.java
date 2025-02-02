package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
    private final ElementsCollection allPeopleTableRows = $$("#all tr");
    private final SelenideElement nextPageBtn = $("#page-next");
    private final SelenideElement prevPageBtn = $("#page-prev");

    public void shouldSeeOutcomeInvitationInAllPeoplesTable(String invitationFriendName) {
        boolean invitationFound = false;

        SelenideElement invitationRow = allPeopleTableRows.find(text(invitationFriendName));
        invitationRow.should(visible);


        // Поиск на текущей странице
        if (invitationRow.exists()) {
            String expected = "Waiting...";
            invitationRow.find("td", 1).shouldHave(text(expected));
            invitationFound = true;
        }

        // Если приглашение не найдено и кнопка "nextPageBtn" активна, переходим на следующую страницу
        while (!invitationFound && nextPageBtn.isEnabled()) {
            nextPageBtn.click(); // Переход на следующую страницу

            // Поиск на новой странице
            if (allPeopleTableRows.find(text(invitationFriendName)).exists()) {
                allPeopleTableRows.find(text(invitationFriendName)).should(visible);
                String expected = "Waiting...";
                allPeopleTableRows.find(text(invitationFriendName)).find("td", 1).shouldHave(text(expected));
                invitationFound = true;
            }
        }

        // Если приглашение так и не найдено, выбрасываем исключение
        if (!invitationFound) {
            throw new AssertionError("Outcome invitation for '" + invitationFriendName + "' not found in the all peoples table.");
        }
    }

}
