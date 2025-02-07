package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PeopleTable {

    private final SelenideElement self = $("#all");
    private final ElementsCollection sentInvites = $$(By.xpath("//tr[.//span[text()='Waiting...']]"));
    private final SearchField searchField = new SearchField();

    @Step("Проверяем, что инвайт отправлен пользователю {username}")
    public PeopleTable checkInvitationSentToUser(String username) {
        searchAndFindRow(username).shouldHave(text("Waiting..."));
        return this;
    }

    @Step("Проверяем, что количество отправленных инвайтов - {amount}")
    public PeopleTable checkAmountOfOutcomeInvitations(int amount) {
        sentInvites.shouldHave(size(amount));
        return this;
    }

    @Step("Отправляем инвайт пользователю {username}")
    public PeopleTable sendInvitationTo(String username) {
        searchAndFindRow(username).$(byText("Add friend")).click();
        return this;
    }

    private SelenideElement searchAndFindRow(String username) {
        searchField.search(username);
        return self.$$("tr").find(text(username));
    }
}