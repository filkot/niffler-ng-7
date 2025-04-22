package guru.qa.niffler.api;

import guru.qa.niffler.model.allure.dockerService.AllureResultsDto;
import guru.qa.niffler.model.allure.dockerService.GenerateReportResponseDto;
import guru.qa.niffler.model.allure.dockerService.SendResultsResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AllureDockerServiceApi {

//    @POST("/allure-docker-service/projects")
//    Call<JsonNode> projects(@Body ProjectCreateRequestDto request);

    @POST("/allure-docker-service/send-results")
    Call<SendResultsResponseDto> sendResults(@Body AllureResultsDto results);

    @POST("/allure-docker-service/generate-report")
    Call<GenerateReportResponseDto> generateReport(@Query("project_id") String projectId,
                                                   @Query(value = "execution_name", encoded = true) String executionName,
                                                   @Query("execution_from") String executionFrom,
                                                   @Query("execution_type") String executionType);

}
