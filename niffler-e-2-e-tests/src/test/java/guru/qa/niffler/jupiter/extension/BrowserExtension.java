package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    static {
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.timeout = 8000;
        Configuration.pageLoadStrategy = "eager";

        if ("docker".equals(System.getProperty("test.env"))) {
            Configuration.remote = "http://selenoid:4444/wd/hub";
            if ("chrome".equals(Configuration.browser)) {
                Configuration.browserVersion = "127.0";
                Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
            } else {
                Configuration.browserVersion = "latest";
                Configuration.browserCapabilities = new FirefoxOptions();
            }
        }
    }

    private final List<SelenideDriver> drivers = new ArrayList<>();

    public void addDriver(SelenideDriver driver) {
        drivers.add(driver);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        for (SelenideDriver driver : drivers) {
            if (driver.hasWebDriverStarted()) {
                driver.close();
            }
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private void doScreenshot() {
        for (SelenideDriver driver : drivers) {
            if (driver.hasWebDriverStarted()) {
                Allure.addAttachment(
                        "Screen on fail fro browser " + driver.getSessionId(),
                        new ByteArrayInputStream(
                                ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                        )
                );
            }
        }
    }
}
