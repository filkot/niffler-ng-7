package guru.qa.niffler.jupiter.convector;

import com.codeborne.selenide.SelenideConfig;

public enum Browser {
    CHROME,
    FIREFOX;

    public static SelenideConfig chromeConfig = new SelenideConfig()
            .browser("chrome")
            .pageLoadStrategy("eager")
            .timeout(5000L);


    public static SelenideConfig firefoxConfig = new SelenideConfig()
            .browser("firefox")
            .pageLoadStrategy("eager")
            .timeout(5000L);
}
