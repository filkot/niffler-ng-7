package guru.qa.niffler.model.allure.dockerService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SendResultsResponseDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    private Details details;


    @Data
    public static class Details {

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("files_uploaded")
        private List<FileUploaded> filesUploaded;
    }

    @Data
    public static class FileUploaded {

        @JsonProperty("file_name")
        private String fileName;

        @JsonProperty("status")
        private String status;
    }
}