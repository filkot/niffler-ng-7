package guru.qa.niffler.model;

import lombok.Getter;

@Getter
public enum CurrencyValues {
    RUB("₽"),
    USD("$"),
    EUR("€"),
    KZT("₸");

    private final String icon;

    CurrencyValues(String icon) {
        this.icon = icon;
    }

}
