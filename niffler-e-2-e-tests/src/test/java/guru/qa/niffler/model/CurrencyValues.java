package guru.qa.niffler.model;

public enum CurrencyValues {
    RUB, USD, EUR, KZT;


    public static String getIcon(CurrencyValues value) {
        return switch (value) {
            case RUB -> "₽";
            case KZT -> "₸";
            case EUR -> "€";
            case USD -> "$";
        };
    }
}
