package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PeopleTable extends BaseComponent<PeopleTable> {

    private final SearchField searchField = new SearchField();
    private final ElementsCollection allRows = self.findAll("tr");


    public PeopleTable() {
        super($("#all"));
    }

    @Step("Проверяем, что приглашение отправлено пользователю {username}")
    @Nonnull
    public PeopleTable checkInvitationSentToUser(String username) {
        searchAndFindRow(username).shouldHave(text("Waiting..."));
        return this;
    }

    @Step("Проверяем, что количество отправленных приглашений - {amount}")
    @Nonnull
    public PeopleTable checkAmountOfOutcomeInvitations(int amount) {
        ElementsCollection invites = $$(By.xpath("//tr[.//span[text()='Waiting...']]"));
        invites.shouldHave(size(amount));
        return this;
    }

    @Step("Отправляем приглашение пользователю {username}")
    @Nonnull
    public PeopleTable sendInvitationTo(String username) {
        searchAndFindRow(username).$(byText("Add friend")).click();
        return this;
    }

    @Step("Поиск пользователя {username}")
    @Nonnull
    private SelenideElement searchAndFindRow(String username) {
        searchField.search(username);
        return self.findAll("tr").find(text(username));
    }

    @Step("Проверяем, что у пользователя есть приглашение от пользователя {username}")
    public void shouldSeeOutcomeInvitationInAllPeoplesTable(String invitationFriendName) {
        // Вводим имя друга в поле поиска
        searchField.search(invitationFriendName);

        // Проверяем, что строка с приглашением отображается в таблице
        SelenideElement invitationRow = allRows.find(text(invitationFriendName));
        invitationRow.shouldBe(visible);

        // Проверяем статус приглашения
        String expectedStatus = "Waiting...";
        invitationRow.find("td", 1).shouldHave(text(expectedStatus));
    }
}