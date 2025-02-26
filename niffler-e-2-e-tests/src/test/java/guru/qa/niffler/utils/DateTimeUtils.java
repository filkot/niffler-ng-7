package guru.qa.niffler.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static String getDateWithFormat(String date, String format) {
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, java.util.Locale.ENGLISH);
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        return parsedDate.format(targetFormatter);
    }
}
