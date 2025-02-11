package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EditSpendingPage extends BasePage<EditSpendingPage> {
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");
    private final ElementsCollection categoryList = $$(By.xpath("//li//span"));

    private final Calendar calendar = new Calendar();

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    public EditSpendingPage save() {
        saveBtn.click();
        return this;
    }

    public void shouldNotSeeArchivedCategoryInCategoryList(String category) {
        assertFalse(categoryList.stream().anyMatch(e -> e.text().equals(category)));
    }

    public void shouldSeeActiveCategoryInCategoryList(String category) {
        assertTrue(categoryList.stream().anyMatch(e -> e.text().equals(category)));
    }
}
