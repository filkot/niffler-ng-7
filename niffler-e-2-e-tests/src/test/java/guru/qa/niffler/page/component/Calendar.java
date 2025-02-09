package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.SetValueOptions.withDate;

public class Calendar extends BaseComponent<Calendar> {

    private static final String MUI_PICKERS_LAYOUT_ROOT = ".MuiPickersLayout-root";
    private static final String DATE_INPUT = "input[name='date']";

    public Calendar() {
        super($(MUI_PICKERS_LAYOUT_ROOT));
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
