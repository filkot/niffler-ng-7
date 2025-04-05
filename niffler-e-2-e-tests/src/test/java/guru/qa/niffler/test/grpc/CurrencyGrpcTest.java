package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.model.CurrencyValues;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.niffler.grpc.CurrencyValues.*;
import static guru.qa.niffler.utils.SpendUtils.EXPECTED_CURRENCY_RATES;
import static guru.qa.niffler.utils.SpendUtils.roundToTwoDecimals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyGrpcTest extends BaseGrpcTest {


    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());

        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();

        assertEquals(EXPECTED_CURRENCY_RATES.size(), allCurrenciesList.size());

        for (Currency currency : allCurrenciesList) {
            final CurrencyValues currencyValues = CurrencyValues.valueOf(currency.getCurrency().name());
            assertEquals(EXPECTED_CURRENCY_RATES.get(currencyValues), currency.getCurrencyRate());
        }
    }

    @Test
    void checkCalculateRates_USD_to_RUB() {
        double spendAmount = 100.0;
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(USD)
                .setDesiredCurrency(RUB)
                .setAmount(spendAmount)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);
        double expected = spendAmount * (EXPECTED_CURRENCY_RATES.get(CurrencyValues.USD) / EXPECTED_CURRENCY_RATES.get(CurrencyValues.RUB));

        assertEquals(roundToTwoDecimals(expected), response.getCalculatedAmount(), 0.001,
                "USD to RUB conversion failed");
    }

    @Test
    void checkCalculateRates_EUR_to_KZT() {
        double spendAmount = 50.0;
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(EUR)
                .setDesiredCurrency(KZT)
                .setAmount(spendAmount)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);
        double expected = spendAmount * (EXPECTED_CURRENCY_RATES.get(CurrencyValues.EUR) / EXPECTED_CURRENCY_RATES.get(CurrencyValues.KZT));

        assertEquals(roundToTwoDecimals(expected), response.getCalculatedAmount(), 0.001,
                "EUR to KZT conversion failed");
    }

    @Test
    void checkCalculateRates_sameCurrency() {
        double spendAmount = 75.0;
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(USD)
                .setDesiredCurrency(USD)
                .setAmount(spendAmount)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        assertEquals(spendAmount, response.getCalculatedAmount(), 0.001,
                "Same currency conversion should return original amount");
    }

    @Test
    void checkCalculateRates_invalidCurrency() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(guru.qa.niffler.grpc.CurrencyValues.UNSPECIFIED)
                .setDesiredCurrency(USD)
                .setAmount(100.0)
                .build();

        assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.calculateRate(request);
        }, "Should throw exception for unrecognized currency");
    }
}
