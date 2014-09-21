package com.fyodor.generators;

import org.junit.Test;

import static com.fyodor.Sampler.from;
import static org.assertj.core.api.Assertions.assertThat;

public final class BooleanGeneratorTest {

    @Test
    public void generatesBooleans() {
        assertThat(from(RDG.bool()).sample(100).unique())
                .containsOnly(true, false);
    }
}
