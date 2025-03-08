package guru.qa.niffler.jupiter.convector;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import static guru.qa.niffler.jupiter.convector.Browser.chromeConfig;
import static guru.qa.niffler.jupiter.convector.Browser.firefoxConfig;


public class BrowserConverter extends SimpleArgumentConverter {

    @Override
    protected SelenideDriver convert(Object source, Class<?> targetType) throws ArgumentConversionException {

        if (!(source instanceof Browser browser)) {
            throw new ArgumentConversionException("Source must be of type Browser");
        }

        return switch (browser) {
            case CHROME -> new SelenideDriver(chromeConfig);
            case FIREFOX -> new SelenideDriver(firefoxConfig);
        };
    }
}