package guru.qa.niffler.utils;

import guru.qa.niffler.model.rest.UserJson;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertionUtils {


    public static void assertUserJsonEquals(UserJson expected, UserJson actual, String... fieldNamesToIgnore) {
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(fieldNamesToIgnore)
                .isEqualTo(expected);
    }
}
