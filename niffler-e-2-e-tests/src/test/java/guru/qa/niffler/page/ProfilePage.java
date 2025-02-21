package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    public static final String URL = CFG.frontUrl() + "profile";

    protected final Header header = new Header();
    private final SelenideElement avatar = $("#image__input").parent().$("img");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");

    private final SelenideElement popup = $("div[role='dialog']");

    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");
    private final SelenideElement avatarImg = $(".MuiAvatar-img");

    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Step("Set name: '{name}'")
    @Nonnull
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload photo from classpath")
    @Nonnull
    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Set category: '{category}'")
    @Nonnull
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Step("Archive category: '{categoryName}'")
    @Nonnull
    public ProfilePage archiveCategory(String categoryName) {
        $(By.xpath("//div[span[text()='"+categoryName+"']]/following-sibling::div//button[@aria-label='Archive category']")).click();
        popup.$(byText("Archive")).click(usingJavaScript());
        return this;
    }

    @Step("Check category: '{category}'")
    @Nonnull
    public ProfilePage checkCategoryExists(String category) {
        bubbles.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Check archived category: '{category}'")
    @Nonnull
    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        bubblesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Check userName: '{username}'")
    @Nonnull
    public ProfilePage checkUsername(String username) {
        this.userName.should(value(username));
        return this;
    }

    @Step("Check name: '{name}'")
    @Nonnull
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Step("Check photo")
    @Nonnull
    public ProfilePage checkPhoto(String path) throws IOException {
        final byte[] photoContent;
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            photoContent = Base64.getEncoder().encode(is.readAllBytes());
        }
        avatar.should(attribute("src", new String(photoContent, StandardCharsets.UTF_8)));
        return this;
    }

    @Step("Check photo exist")
    @Nonnull
    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Check that category input is disabled")
    @Nonnull
    public ProfilePage checkThatCategoryInputDisabled() {
        categoryInput.should(disabled);
        return this;
    }

    @Step("Save profile")
    @Nonnull
    public ProfilePage submitProfile() {
        submitButton.click();
        return this;
    }

    @Override
    @Step("Check that page is loaded")
    @Nonnull
    public ProfilePage checkThatPageLoaded() {
        userName.should(visible);
        return this;
    }

    @Step("Check avatar image")
    @Nonnull
    public ProfilePage checkAvatarImage(BufferedImage expected) throws IOException {
        Selenide.sleep(2000);
        avatarImg.shouldBe(visible);
        BufferedImage actual = ImageIO.read(avatarImg.screenshot());
        assertFalse(new ScreenDiffResult(expected, actual));
        return this;
    }
}
