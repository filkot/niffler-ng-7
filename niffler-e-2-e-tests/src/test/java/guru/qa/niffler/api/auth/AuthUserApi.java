package guru.qa.niffler.api.auth;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthUserApi {

    @GET("register")
    Call<ResponseBody> getCsrfToken();

    @POST("register")
    @FormUrlEncoded
    Call<Void> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf);
}
