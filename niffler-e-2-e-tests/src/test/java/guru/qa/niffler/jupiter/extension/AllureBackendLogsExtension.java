package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {
    private static final String CASE_NAME = "Niffler backend logs";
    private static final String LOG_DIRECTORY = "./logs";
    private static final String LOG_FILE_NAME = "app.log";

    @Override
    public void afterSuite() {
        AllureLifecycle allureLifecycle = Allure.getLifecycle();
        String caseId = UUID.randomUUID().toString();

        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(CASE_NAME));
        allureLifecycle.startTestCase(caseId);

        try {
            addLogsFromServices(allureLifecycle);
        } catch (IOException e) {
            throw new RuntimeException("Failed to add logs to Allure report", e);
        } finally {
            allureLifecycle.stopTestCase(caseId);
            allureLifecycle.writeTestCase(caseId);
        }
    }

    private void addLogsFromServices(AllureLifecycle allureLifecycle) throws IOException {
        String[] services = {
                "niffler-auth",
                "niffler-currency",
                "niffler-gateway",
                "niffler-spend",
                "niffler-userdata"
        };
        for (String service : services) {
            String logPath = String.format("%s/%s/%s", LOG_DIRECTORY, service, LOG_FILE_NAME);
            addLogFromService(allureLifecycle, service, logPath);
        }
    }

    private void addLogFromService(AllureLifecycle allureLifecycle, String serviceName, String logPath) throws IOException {
        allureLifecycle.addAttachment(
                serviceName,
                "text/html",
                ".log",
                Files.newInputStream(Path.of(logPath))
        );
    }
}