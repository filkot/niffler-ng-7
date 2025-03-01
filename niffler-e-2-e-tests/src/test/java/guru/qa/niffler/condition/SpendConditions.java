package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.rest.SpendJson;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static guru.qa.niffler.utils.DateTimeUtils.getDateWithFormat;

@ParametersAreNonnullByDefault
public class SpendConditions {

    public static WebElementsCondition spends(SpendJson... expectedSpends) {
        return new WebElementsCondition() {
            private String message = "Collection check failed";

            @Override
            public String errorMessage() {
                return message;
            }

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }
                if (elements.size() != expectedSpends.length) {
                    message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedSpends.length, elements.size());
                    return rejected(message, elements);
                }

                List<String> actualDetails = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    List<WebElement> cells = elements.get(i).findElements(By.cssSelector("td"));
                    final String actualCategory = cells.get(1).getText();
                    final String actualAmount = cells.get(2).getText();
                    final String actualDescription = cells.get(3).getText();
                    final String actualDate = getDateWithFormat(cells.get(4).getText(), "MMM dd, yyyy");


                    final SpendJson expectedSpend = expectedSpends[i];
                    final String expectedCategory = expectedSpend.category().name();
                    final String icon = expectedSpend.currency().getIcon();
                    final String amount = expectedSpend.amount() % 1 == 0 ?
                            String.valueOf(expectedSpend.amount().intValue()) : expectedSpend.amount().toString();
                    final String expectedAmount = String.format("%s %s", amount, icon);
                    final String expectedDescription = expectedSpend.description();
                    final String expectedDate = getDateWithFormat(expectedSpend.spendDate().toString(), "yyyy-MM-dd");


                    actualDetails.add(String.format(
                            "Category: %s, Amount: %s, Description: %s, Date: %s",
                            actualCategory, actualAmount, actualDescription, actualDate));

                    // Проверка категории
                    if (!actualCategory.equals(expectedCategory)) {
                        message = String.format(
                                "Spend category mismatch (expected: %s, actual: %s)",
                                expectedCategory, actualCategory
                        );
                        return CheckResult.rejected(message, actualDetails);
                    }

                    // Проверка даты
                    if (!actualDate.equals(expectedDate)) {
                        message = String.format(
                                "Spend date mismatch (expected: %s, actual: %s)",
                                expectedDate, actualDate
                        );
                        return CheckResult.rejected(message, actualDetails);
                    }

                    // Проверка валюты и суммы
                    if (!actualAmount.equals(expectedAmount)) {
                        message = String.format(
                                "Spend amount mismatch (expected: %s, actual: %s)",
                                expectedAmount, actualAmount
                        );
                        return CheckResult.rejected(message, actualDetails);
                    }

                    // Проверка описания
                    if (!actualDescription.equals(expectedDescription)) {
                        message = String.format(
                                "Spend description mismatch (expected: %s, actual: %s)",
                                expectedDescription, actualDescription
                        );
                        return CheckResult.rejected(message, actualDetails);
                    }
                }

                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.stream(expectedSpends)
                        .map(s -> String.format(
                                "Category: %s, Amount: %s, Description: %s, Date: %s",
                                s.category().name(),
                                s.amount(),
                                s.description(),
                                s.spendDate()))
                        .toList().toString();
            }
        };
    }

}
