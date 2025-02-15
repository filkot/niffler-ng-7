package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");

    public StatComponent() {
        super($("#stat"));
    }
}
