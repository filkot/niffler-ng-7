package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class CurrenciesGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void allCurrenciesShouldBeReturnedFromGateway(@Token String bearerToken){
        final ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final CurrenciesQuery.Data data = response.dataOrThrow();
        final List<CurrenciesQuery.Currency> currencies = data.currencies;

        final List<String> actList = currencies.stream().map(currency -> currency.currency.rawValue).sorted().toList();
        final List<String> expList = Arrays.stream(CurrencyValues.values()).map(CurrencyValues::name).sorted().toList();

        Assertions.assertEquals(
                expList,
                actList
        );
    }
}
