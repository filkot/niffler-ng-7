package guru.qa.niffler.api;


import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GhApiClient {

    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Config.getInstance().ghUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final GhApi ghApi = retrofit.create(GhApi.class);

    public @Nonnull String issueState(@Nonnull String issueNumber) {
        final Response<JsonNode> response;
        try {
            response = ghApi.issue(
                            "BearerToken " + System.getenv(GH_TOKEN_ENV),
                            issueNumber
                    )
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return Objects.requireNonNull(response.body()).get("state").asText();
    }
}
