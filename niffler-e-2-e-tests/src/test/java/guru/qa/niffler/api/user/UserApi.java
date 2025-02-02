package guru.qa.niffler.api.user;

import guru.qa.niffler.model.RegistrationModel;
import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface UserApi {

    @POST("/register")
    Call<String> registerUser(@Body RegistrationModel registrationModel);

    @GET("/internal/users/current")
    Call<UserJson> getUser(@Query("username") String username);

    @GET("/internal/users/all")
    Call<List<UserJson>> getUsers(@Query("username") String username,
                                  @Query("searchQuery") String searchQuery);

    @POST("internal/users/update")
    Call<UserJson> updateUser(@Body UserJson user);

    @POST("/internal/invitations/send")
    Call<UserJson> sendInvitation(@Query("username") String username,
                                  @Query("targetUsername") String targetUsername);

    @POST("/internal/invitations/accept")
    Call<UserJson> acceptInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);

    @POST("/internal/invitations/decline")
    Call<UserJson> declineInvitation(@Query("username") String username,
                                     @Query("targetUsername") String targetUsername);


}
