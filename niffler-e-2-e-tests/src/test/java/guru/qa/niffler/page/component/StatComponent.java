package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
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
import static guru.qa.niffler.condition.StatConditions.*;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = self.$("canvas[role='img']");

    public StatComponent() {
        super($("#stat"));
    }


    @Step("Check stat diagram")
    @Nonnull
    public StatComponent checkStatImage(BufferedImage expected) throws IOException {
        Selenide.sleep(2000);
        BufferedImage actual = chartScreenshot();
        assertFalse(new ScreenDiffResult(expected, actual), "Screen comparison failure");
        return this;
    }

    @Nonnull
    private BufferedImage chartScreenshot() throws IOException {
        chart.shouldBe(visible);
        return ImageIO.read(requireNonNull(chart.screenshot()));
    }

    @Step("Check bubbles contains '{expectedCategories}'")
    @Nonnull
    public StatComponent checkBubbles(List<String> expectedBubbles) {
        bubbles.shouldHave(texts(expectedBubbles));
        return this;
    }

    @Nonnull
    public StatComponent checkFirstBubbleColor(Color expectedColor) {
        bubbles.first().should(color(expectedColor));
        return this;
    }

    @Nonnull
    public StatComponent checkBubblesColors(Color... expectedColors) {
        bubbles.should(color(expectedColors));
        return this;
    }

    @Nonnull
    public StatComponent checkBubbles(Bubble... expectedBubbles) {
        bubbles.should(statBubbles(expectedBubbles));
        return this;
    }

    @Nonnull
    public StatComponent checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.should(statBubblesInAnyOrder(expectedBubbles));
        return this;
    }

    @Nonnull
    public StatComponent checkBubblesContains(Bubble... expectedBubbles) {
        bubbles.should(statBubblesContains(expectedBubbles));
        return this;
    }

}
