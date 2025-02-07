package guru.qa.niffler.api.category;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class CategoryApiClient {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Config.getInstance().spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final CategoryApi categoryApi = retrofit.create(CategoryApi.class);

    public @Nullable CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = categoryApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public @Nullable CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = categoryApi.updateCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body();
    }

    public @Nonnull List<CategoryJson> getAllCategories(String username, boolean excludeArchived) {
        final Response<List<CategoryJson>> response;
        try {
            response = categoryApi.getCategories(username, excludeArchived)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }
}
