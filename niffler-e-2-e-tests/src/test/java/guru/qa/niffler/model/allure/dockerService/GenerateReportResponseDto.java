package guru.qa.niffler.model.allure.dockerService;

import lombok.Data;

@Data
public class GenerateReportResponseDto {
    private boolean success;
    private String message;
}
