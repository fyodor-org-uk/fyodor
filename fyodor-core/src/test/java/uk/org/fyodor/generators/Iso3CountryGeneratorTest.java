package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import static org.assertj.core.api.Assertions.assertThat;

public class Iso3CountryGeneratorTest extends BaseTestWithRule {

    @Test
    public void getIso3CountryCode(){
        Generator<String> countryGenerator = RDG.iso3Country();
        for (int i = 0; i < 1000; i++) {
            String country = countryGenerator.next();
            print(country);
            assertThat(country).hasSize(3);
        }

    }
}
