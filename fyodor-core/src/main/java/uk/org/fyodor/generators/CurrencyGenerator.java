package uk.org.fyodor.generators;

import java.util.Currency;

public class CurrencyGenerator implements Generator<Currency> {

    private Generator<Currency> currencyGenerator = RDG.value(Currency.getAvailableCurrencies());

    public Currency next() {
        return currencyGenerator.next();
    }
}
