package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static com.codeborne.selenide.SetValueOptions.withDate;

public class Calendar {

    private static final String DATE_INPUT = "input[name='date']";
    private final SelenideElement self;

    public Calendar(SelenideElement self) {
        this.self = self;
    }

    public Calendar selectDate(Date date) {
        String dateFormat = Optional.ofNullable(
                self.$(DATE_INPUT).getAttribute("value")
        ).orElse("MM/DD/YYYY");
        LocalDate spendingDate = LocalDate.parse(date.toString(),
                DateTimeFormatter.ofPattern(dateFormat));
        self.$(DATE_INPUT).setValue(
                withDate(spendingDate)
        );
        return this;
    }
}
