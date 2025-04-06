package guru.qa.niffler.test.gql;

import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.UpdateCategoryMutation;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.SpendUtils;
import guru.qa.type.CategoryInput;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static guru.qa.niffler.utils.SpendUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatGraphQlTest extends BaseGraphQlTest {


    private StatQuery.Stat getStatistic(String bearerToken) {
        return Rx2Apollo.single(apolloClient.query(StatQuery.builder().build())
                        .addHttpHeader("authorization", bearerToken))
                .blockingGet()
                .dataOrThrow()
                .stat;
    }

    private UpdateCategoryMutation.Category archiveCategory(String bearerToken, CategoryJson category) {
        return Rx2Apollo.single(apolloClient.mutation(UpdateCategoryMutation.builder()
                                .input(CategoryInput.builder()
                                        .id(category.id().toString())
                                        .name(category.name())
                                        .archived(true)
                                        .build())
                                .build())
                        .addHttpHeader("authorization", bearerToken))
                .blockingGet()
                .dataOrThrow()
                .category;
    }

    @Test
    @ApiLogin
    @User
    void allStatShouldBeReturnedFromGateway(@Token String bearerToken) {
        StatQuery.Stat result = getStatistic(bearerToken);
        assertEquals(0.0, result.total);
    }

    @Test
    @ApiLogin
    @User(spendings = {
            @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 1000,
                    currency = CurrencyValues.USD),
            @Spending(
                    category = "Магазины",
                    description = "Продукты",
                    amount = 100,
                    currency = CurrencyValues.EUR)
    })
    void checkStatWithDifferenceCurrencies(@Token String bearerToken, UserJson user) {
        StatQuery.Stat result = getStatistic(bearerToken);

        List<SpendJson> expectedSpends = user.testData().spends().stream()
                .map(SpendUtils::convertToRubWithNewRates)
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();

        List<SpendJson> actualSpends = result.statByCategories.stream()
                .map(stat -> mapStatToSpend(stat, user))
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();

        assertThat(actualSpends)
                .usingRecursiveComparison()
                .ignoringFields("category.id", "spendDate")
                .isEqualTo(expectedSpends);

        double expectedTotal =
                roundToTwoDecimals(user.testData().spends().stream()
                .mapToDouble(spend -> convertAmountToRub(spend.amount(), spend.currency()))
                .sum());

        assertEquals(expectedTotal, result.total, 0.001,
                "Total sum in RUB doesn't match expected value");
    }

    @Test
    @ApiLogin
    @User(spendings = {
            @Spending(
                    category = "Clothes",
                    description = "jeans",
                    amount = 200,
                    currency = CurrencyValues.USD),
            @Spending(
                    category = "Sports",
                    description = "bike",
                    amount = 2000,
                    currency = CurrencyValues.EUR)
    })
    void checkStatWithArchivedCategory(@Token String bearerToken, UserJson user) {
        CategoryJson categoryToArchive = user.testData().spends().getFirst().category();
        archiveCategory(bearerToken, categoryToArchive);

        StatQuery.Stat result = getStatistic(bearerToken);

        List<SpendJson> expectedSpends = user.testData().spends().stream()
                .map(spend -> convertToRubWithNewRates(spend))
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();

        List<SpendJson> actualSpends = result.statByCategories.stream()
                .map(stat -> mapStatToSpend(stat, user))
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();

        assertThat(actualSpends)
                .usingRecursiveComparison()
                .ignoringFields("category.id", "spendDate", "category.name")
                .isEqualTo(expectedSpends);

        assertEquals("Archived", actualSpends.getFirst().category().name(),
                "Category should be archived");

        double expectedTotal = user.testData().spends().stream()
                .mapToDouble(spend -> convertAmountToRub(spend.amount(), spend.currency()))
                .sum();

        assertEquals(roundToTwoDecimals(expectedTotal), result.total, 0.01,
                "Total sum in RUB doesn't match expected value");
    }

}