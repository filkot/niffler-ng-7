package guru.qa.niffler.model.allure.dockerService;

import lombok.Data;
import java.util.List;

@Data
public class AllureResultsDto {
    private List<ResultFile> results;

    @Data
    public static class ResultFile {
        private String fileName;
        private String contentBase64;
    }
}