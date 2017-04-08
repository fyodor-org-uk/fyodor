package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;

public final class BooleanGeneratorTest extends BaseTest {

    @Test
    public void generatesBooleans() {
        assertThat(from(RDG.bool()).sample(100).unique())
                .containsOnly(true, false);
    }
}
