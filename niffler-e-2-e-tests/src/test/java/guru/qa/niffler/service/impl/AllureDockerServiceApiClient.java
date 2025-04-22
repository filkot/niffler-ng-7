package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AllureDockerServiceApi;
import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.allure.dockerService.AllureResultsDto;
import guru.qa.niffler.model.allure.dockerService.GenerateReportResponseDto;
import guru.qa.niffler.model.allure.dockerService.SendResultsResponseDto;
import retrofit2.Call;

import javax.annotation.ParametersAreNonnullByDefault;

import retrofit2.Call;
import retrofit2.Response;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AllureDockerServiceApiClient extends RestClient implements AllureDockerServiceApi {

    private final AllureDockerServiceApi allureDockerServiceApi;

    public AllureDockerServiceApiClient(String baseUrl) {
        super(baseUrl);
        this.allureDockerServiceApi = create(AllureDockerServiceApi.class);
    }

    @Override
    public Call<SendResultsResponseDto> sendResults(AllureResultsDto results) {
        return allureDockerServiceApi.sendResults(results);
    }

    @Override
    public Call<GenerateReportResponseDto> generateReport(String projectId, String executionName, String executionFrom, String executionType) {
        return allureDockerServiceApi.generateReport(projectId, executionName, executionFrom, executionType);
    }
}