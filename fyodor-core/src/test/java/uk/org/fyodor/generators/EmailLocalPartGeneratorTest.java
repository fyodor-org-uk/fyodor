package uk.org.fyodor.generators;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailLocalPartGeneratorTest {

    EmailLocalPartGenerator generator = new EmailLocalPartGenerator(20);

    @Test
    public void noWrongDots(){
        for (int i = 0; i < 10000; i++) {
            String localPart = generator.next().trim();
            assertThat(localPart).doesNotContain("..");
            assertThat(localPart.startsWith(".")).isFalse();
            assertThat(localPart.endsWith(".")).isFalse();
        }
    }
}
