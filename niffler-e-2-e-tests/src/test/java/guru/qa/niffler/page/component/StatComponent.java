package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");

    public StatComponent() {
        super($("#stat"));
    }

    @Step("Check bubbles contains '{expectedCategories}'")
    @Nonnull
    public StatComponent checkBubbles(List<String> expectedBubbles) {
        List<String> actualBubbles = new java.util.ArrayList<>(bubbles.stream()
                .map(SelenideElement::getText).toList());
        expectedBubbles.sort(null);
        actualBubbles.sort(null);

        assertEquals(expectedBubbles, actualBubbles);
        return this;
    }
}
