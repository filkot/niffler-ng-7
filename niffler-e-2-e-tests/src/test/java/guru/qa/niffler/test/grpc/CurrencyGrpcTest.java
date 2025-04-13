package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.model.CurrencyValues;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.grpc.CurrencyValues.*;
import static guru.qa.niffler.utils.SpendUtils.EXPECTED_CURRENCY_RATES;
import static guru.qa.niffler.utils.SpendUtils.roundToTwoDecimals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = CURRENCY_SERVICE_BLOCKING_STUB.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();

        assertEquals(EXPECTED_CURRENCY_RATES.size(), allCurrenciesList.size());

        allCurrenciesList.forEach(currency -> {
            CurrencyValues currencyValue = CurrencyValues.valueOf(currency.getCurrency().name());
            assertEquals(EXPECTED_CURRENCY_RATES.get(currencyValue), currency.getCurrencyRate());
        });
    }

    private static Stream<Arguments> currencyConversionProvider() {
        return Stream.of(
                Arguments.of(100.0, USD, RUB,
                        roundToTwoDecimals(
                                100.0 * (EXPECTED_CURRENCY_RATES.get(CurrencyValues.USD) /
                                        EXPECTED_CURRENCY_RATES.get(CurrencyValues.RUB)))),
                Arguments.of(50.0, EUR, KZT,
                        roundToTwoDecimals(
                                50.0 * (EXPECTED_CURRENCY_RATES.get(CurrencyValues.EUR) /
                                        EXPECTED_CURRENCY_RATES.get(CurrencyValues.KZT)))),
                Arguments.of(75.0, USD, USD, 75.0)
        );
    }

    @ParameterizedTest
    @MethodSource("currencyConversionProvider")
    void checkCalculateRates(double amount,
                             guru.qa.niffler.grpc.CurrencyValues spendCurrency,
                             guru.qa.niffler.grpc.CurrencyValues desiredCurrency,
                             double expectedAmount) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .setAmount(amount)
                .build();

        CalculateResponse response = CURRENCY_SERVICE_BLOCKING_STUB.calculateRate(request);
        assertEquals(expectedAmount, response.getCalculatedAmount(), 0.001);
    }

    @Test
    void checkCalculateRates_invalidCurrency() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(UNSPECIFIED)
                .setDesiredCurrency(USD)
                .setAmount(100.0)
                .build();

        assertThrows(StatusRuntimeException.class, () ->
                CURRENCY_SERVICE_BLOCKING_STUB.calculateRate(request)
        );
    }
}