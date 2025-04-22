package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.allure.dockerService.AllureResultsDto;
import guru.qa.niffler.model.allure.dockerService.GenerateReportResponseDto;
import guru.qa.niffler.model.allure.dockerService.SendResultsResponseDto;
import guru.qa.niffler.service.impl.AllureDockerServiceApiClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AllureSendResultsExtension implements SuiteExtension {

    private static final Logger log = LoggerFactory.getLogger(AllureSendResultsExtension.class);
    private static final String ALLURE_RESULTS_DIR = "/niffler/niffler-e-2-e-tests/build/allure-results";
    private static final String PROJECT_ID = "niffler-tests";
    private static final String EXECUTION_NAME = "JUnit5 Tests Execution";
    private static final String EXECUTION_TYPE = "auto";
    private static final String ALLURE_DOCKER_PUBLIC_API_URL_ENV = "ALLURE_DOCKER_PUBLIC_API_URL";
    private static final String DEFAULT_ALLURE_SERVICE_URL = "http://localhost:5050";
    private static final int CONNECTION_TIMEOUT_SEC = 30;

    private final AllureDockerServiceApiClient client;

    public AllureSendResultsExtension() {
        String allureServiceUrl = System.getenv(ALLURE_DOCKER_PUBLIC_API_URL_ENV) != null
                ? System.getenv(ALLURE_DOCKER_PUBLIC_API_URL_ENV)
                : DEFAULT_ALLURE_SERVICE_URL;

        log.info("Initializing AllureDockerServiceApiClient with URL: {}", allureServiceUrl);
        this.client = new AllureDockerServiceApiClient(allureServiceUrl);
    }

    @Override
    public void afterSuite() {
        if (!isAllureServiceAvailable()) {
            log.warn("Allure Docker Service is not available. Skipping results upload.");
            return;
        }

        try {
            File resultsDir = new File(ALLURE_RESULTS_DIR);
            if (!resultsDir.exists() || !resultsDir.isDirectory()) {
                log.warn("Allure results directory not found: {}", ALLURE_RESULTS_DIR);
                return;
            }

            AllureResultsDto resultsDto = new AllureResultsDto();
            resultsDto.setResults(collectResults(resultsDir));

            if (resultsDto.getResults().isEmpty()) {
                log.warn("No Allure result files found in directory: {}", ALLURE_RESULTS_DIR);
                return;
            }

            log.info("Sending {} test results to Allure Docker Service", resultsDto.getResults().size());
            Response<SendResultsResponseDto> sendResultsResponse = client.sendResults(resultsDto).execute();

            if (!sendResultsResponse.isSuccessful()) {
                throw new RuntimeException("Failed to send results to Allure Docker Service. HTTP code: " +
                        sendResultsResponse.code() + ", message: " + sendResultsResponse.message());
            }

            log.info("Results sent successfully. Response: {}", sendResultsResponse.body());

            String executionFrom = java.time.Instant.now().toString();
            log.info("Generating Allure report for project: {}", PROJECT_ID);
            Response<GenerateReportResponseDto> generateReportResponse = client.generateReport(
                    PROJECT_ID,
                    EXECUTION_NAME,
                    executionFrom,
                    EXECUTION_TYPE
            ).execute();

            if (!generateReportResponse.isSuccessful()) {
                throw new RuntimeException("Failed to generate report. HTTP code: " +
                        generateReportResponse.code() + ", message: " + generateReportResponse.message());
            }

            log.info("Report generated successfully. Response: {}", generateReportResponse.body());

        } catch (Exception e) {
            log.error("Error while sending results or generating report", e);
            throw new RuntimeException("Error while sending results or generating report", e);
        }
    }

    private List<AllureResultsDto.ResultFile> collectResults(File resultsDir) throws IOException {
        List<AllureResultsDto.ResultFile> resultFiles = new ArrayList<>();
        File[] files = resultsDir.listFiles();

        if (files == null) {
            return resultFiles;
        }

        for (File file : files) {
            if (file.isFile()) {
                try {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String base64Content = Base64.getEncoder().encodeToString(fileContent);

                    AllureResultsDto.ResultFile resultFile = new AllureResultsDto.ResultFile();
                    resultFile.setFileName(file.getName());
                    resultFile.setContentBase64(base64Content);
                    resultFiles.add(resultFile);

                    log.debug("Collected result file: {}", file.getName());
                } catch (IOException e) {
                    log.error("Failed to read result file: {}", file.getName(), e);
                }
            }
        }
        return resultFiles;
    }

    private boolean isAllureServiceAvailable() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:5050/health")
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            boolean available = response.isSuccessful();
            log.info("Allure Docker Service health check: {}", available ? "OK" : "FAILED");
            return available;
        } catch (IOException e) {
            log.warn("Allure Docker Service health check failed: {}", e.getMessage());
            return false;
        }
    }
}