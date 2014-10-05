package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import static uk.org.fyodor.Sampler.from;
import static org.assertj.core.api.Assertions.assertThat;

public final class BooleanGeneratorTest extends BaseTestWithRule {

    @Test
    public void generatesBooleans() {
        assertThat(from(RDG.bool()).sample(100).unique())
                .containsOnly(true, false);
    }
}
