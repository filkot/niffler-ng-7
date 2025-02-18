package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {
    public static final String caseName = "Niffler backend logs";

    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        try {
            allureLifecycle.addAttachment(
                    "Niffler-auth-log",
                    "text/html",
                    ".log",
                    Files.newInputStream(Path.of("./logs/niffler-auth/app.log"))

            );
            allureLifecycle.addAttachment(
                    "Niffler-currency-log",
                    "text/html",
                    ".log",
                    Files.newInputStream(Path.of("./logs/niffler-currency/app.log"))

            );
            allureLifecycle.addAttachment(
                    "Niffler-gateway-log",
                    "text/html",
                    ".log",
                    Files.newInputStream(Path.of("./logs/niffler-gateway/app.log"))

            );
            allureLifecycle.addAttachment(
                    "Niffler-spend-log",
                    "text/html",
                    ".log",
                    Files.newInputStream(Path.of("./logs/niffler-spend/app.log"))

            );
            allureLifecycle.addAttachment(
                    "Niffler-userdata-log",
                    "text/html",
                    ".log",
                    Files.newInputStream(Path.of("./logs/niffler-userdata/app.log"))

            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }
}
