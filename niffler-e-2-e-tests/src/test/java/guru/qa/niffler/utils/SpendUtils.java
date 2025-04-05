package guru.qa.niffler.utils;

import guru.qa.StatQuery;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;

import java.util.Map;

public class SpendUtils {


    public static final Map<CurrencyValues, Double> EXPECTED_CURRENCY_RATES = Map.of(
            CurrencyValues.USD, 1.0,
            CurrencyValues.EUR, 1.08,
            CurrencyValues.RUB, 0.015,
            CurrencyValues.KZT, 0.0021
    );

    public static double convertAmountToRub(double amount, CurrencyValues currency) {
        // Конвертируем в USD как базовую валюту, затем в RUB
        double amountInUsd = amount * EXPECTED_CURRENCY_RATES.get(currency);
        return amountInUsd / EXPECTED_CURRENCY_RATES.get(CurrencyValues.RUB);
    }

    public static SpendJson convertToRubWithNewRates(SpendJson spend) {
        return new SpendJson(
                null,
                spend.spendDate(),
                spend.category(),
                CurrencyValues.RUB,
                roundToTwoDecimals(convertAmountToRub(spend.amount(), spend.currency())),
                "",
                spend.username()
        );
    }

    public static SpendJson mapStatToSpend(StatQuery.StatByCategory stat, UserJson user) {
        return new SpendJson(
                null,
                stat.firstSpendDate,
                new CategoryJson(null, stat.categoryName, user.username(), false),
                CurrencyValues.valueOf(stat.currency.rawValue),
                roundToTwoDecimals(stat.sum),
                "",
                user.username()
        );
    }

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
