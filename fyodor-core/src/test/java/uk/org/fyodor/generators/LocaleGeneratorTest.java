package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import java.util.Locale;

public class LocaleGeneratorTest extends BaseTestWithRule {

    @Test
    public void generateLocales(){

        Generator<Locale> generator = RDG.locales();
        for (int i = 0; i < 1000; i++) {
            Locale locale = generator.next();
            print(locale);
        }

    }
}
