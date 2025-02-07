package guru.qa.niffler.api.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {

    @POST("internal/spends/add")
    Call<SpendJson> addSpend(@Body SpendJson spend);

    @PATCH("/internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spend);

    @GET("/internal/spends/{id}")
    Call<SpendJson> getSpend(@Path("id") String id,
                             @Query("username") String username);

    @GET("/internal/spends/all")
    Call<List<SpendJson>> getSpends(@Query("username") String username,
                                    @Query("username") CurrencyValues currency,
                                    @Query("username") String from,
                                    @Query("username") String to);


    @DELETE("/internal/spends/remove")
    Call<Void> deleteSpends(@Query("username") String username,
                            @Query("ids") List<String> ids);

}
