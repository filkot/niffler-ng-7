package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color") {

            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition color(Color... expectedColors) {

        return new WebElementsCondition() {
            private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

            private String message = "Collection check failed";

            @Override
            public String errorMessage() {
                return message;
            }

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (elements.size() != expectedColors.length) {
                    message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color expectedColor = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = expectedColor.rgb.equals(rgba);
                    }
                }
                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba);
                    return rejected(message, actualRgba);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    public static WebElementsCondition statBubbles(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private String message = "Collection check failed";

            @Override
            public String errorMessage() {
                return message;
            }

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (elements.size() != expectedBubbles.length) {
                    message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<String> actualDetails = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Bubble expectedBubble = expectedBubbles[i];
                    final String actualColor = elementToCheck.getCssValue("background-color");
                    final String actualText = elementToCheck.getText();
                    actualDetails.add(String.format("Bubble: %s, Text: %s", actualColor, actualText));

                    if (passed) {
                        passed = expectedBubble.color().rgb.equals(actualColor)
                                && expectedBubble.text().equals(actualText);
                    }
                }

                if (!passed) {
                    message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)",
                            Arrays.toString(expectedBubbles), actualDetails);
                    return rejected(message, actualDetails);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.toString(expectedBubbles);
            }
        };
    }

    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private String message = "Collection check failed";

            @Override
            public String errorMessage() {
                return message;
            }

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (elements.size() != expectedBubbles.length) {
                    message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                List<Bubble> expectedList = new ArrayList<>(Arrays.asList(expectedBubbles));
                List<String> actualDetails = new ArrayList<>();
                boolean passed = true;

                for (WebElement element : elements) {
                    final String actualColor = element.getCssValue("background-color");
                    final String actualText = element.getText();
                    actualDetails.add(String.format("Bubble: %s, Text: %s", actualColor, actualText));

                    Bubble actualBubble = new Bubble(Color.fromRgb(actualColor), actualText);
                    if (!expectedList.contains(actualBubble)) {
                        passed = false;
                        break;
                    }
                    expectedList.remove(actualBubble);
                }

                if (!passed) {
                    message = String.format(
                            "List mismatch in any order (expected: %s, actual: %s)",
                            Arrays.toString(expectedBubbles), actualDetails);
                    return rejected(message, actualDetails);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.toString(expectedBubbles);
            }
        };
    }

    public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private String message = "Collection check failed";

            @Override
            public String errorMessage() {
                return message;
            }

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                List<Bubble> expectedList = new ArrayList<>(Arrays.asList(expectedBubbles));
                List<String> actualDetails = new ArrayList<>();
                boolean passed = true;

                for (WebElement element : elements) {
                    final String actualColor = element.getCssValue("background-color");
                    final String actualText = element.getText();
                    actualDetails.add(String.format("Bubble: %s, Text: %s", actualColor, actualText));

                    Bubble actualBubble = new Bubble(Color.fromRgb(actualColor), actualText);
                    expectedList.remove(actualBubble);
                }

                if (!expectedList.isEmpty()) {
                    passed = false;
                }

                if (!passed) {
                    message = String.format(
                            "List does not contain all expected values (expected: %s, actual: %s)",
                            Arrays.toString(expectedBubbles), actualDetails);
                    return rejected(message, actualDetails);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.toString(expectedBubbles);
            }
        };
    }
}
