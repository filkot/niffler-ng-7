package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage<ProfilePage> {

    public static final String URL = Config.getInstance().frontUrl() + "profile";

    private final SelenideElement avatar = $("#image__input").parent().$("img");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");

    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");
    private final SelenideElement editCategoryInput = $("input[placeholder='Edit category']");

    @Step("Устанавливаем имя пользователя {name}")
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return submitProfile();
    }

    @Step("Загружаем фото из classpath {path}")
    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Добавляем категорию {category}")
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Step("Редактируем имя категории {nameToUpdate} на {newName}")
    public ProfilePage editCategoryName(String nameToUpdate, String newName) {
        findCategoryBubble(nameToUpdate)
                .sibling(0)
                .$("button[aria-label='Edit category']")
                .click();
        editCategoryInput.setValue(newName).pressEnter();
        return this;
    }

    @Step("Проверяем наличие категории {category}")
    public ProfilePage checkCategoryExists(String category) {
        findCategoryBubble(category).shouldBe(visible);
        return this;
    }

    @Step("Проверяем наличие архивированной категории {category}")
    public ProfilePage checkArchivedCategoryExists(String category) {
        toggleArchivedSwitcher();
        bubblesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Проверяем имя пользователя {username}")
    public ProfilePage checkUsername(String username) {
        userName.shouldHave(value(username));
        return this;
    }

    @Step("Проверяем имя пользователя {name}")
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Step("Проверяем наличие фото")
    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Проверяем, что поле ввода категории отключено")
    public ProfilePage checkThatCategoryInputDisabled() {
        categoryInput.shouldBe(disabled);
        return this;
    }

    private ProfilePage submitProfile() {
        submitButton.click();
        return this;
    }

    private SelenideElement findCategoryBubble(String category) {
        return bubbles.find(text(category));
    }

    private void toggleArchivedSwitcher() {
        archivedSwitcher.click();
    }
}