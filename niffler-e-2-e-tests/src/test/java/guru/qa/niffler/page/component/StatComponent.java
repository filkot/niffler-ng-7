package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement statImg = self.$("canvas[role='img']");

    public StatComponent() {
        super($("#stat"));
    }



    @Step("Check stat diagram")
    @Nonnull
    public StatComponent checkStatImage(BufferedImage expected) throws IOException {
        Selenide.sleep(2000);
        statImg.shouldBe(visible);
        BufferedImage actual = ImageIO.read(statImg.screenshot());
        assertFalse(new ScreenDiffResult(expected, actual));
        return this;
    }

    @Step("Check bubbles contains '{expectedCategories}'")
    @Nonnull
    public StatComponent checkBubbles(List<String> expectedBubbles) {
        bubbles.shouldHave(texts(expectedBubbles));
        return this;
    }

}
