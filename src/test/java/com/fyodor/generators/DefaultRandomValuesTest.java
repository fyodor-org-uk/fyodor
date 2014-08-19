package com.fyodor.generators;

import org.junit.Test;

import java.util.Random;

import static com.fyodor.generators.Sampler.from;
import static org.assertj.core.api.Assertions.assertThat;

public final class DefaultRandomValuesTest {

    private final RandomValues randomValues = new DefaultRandomValues(new Random());

    @Test
    public void returnsSingleLongValueWhenLowerAndUpperBoundAreEqual() {
        assertThat(randomValues.randomLong(1, 1))
                .isEqualTo(1);

        assertThat(randomValues.randomLong(0, 0))
                .isEqualTo(0);

        assertThat(randomValues.randomLong(Long.MIN_VALUE, Long.MIN_VALUE))
                .isEqualTo(Long.MIN_VALUE);

        assertThat(randomValues.randomLong(Long.MAX_VALUE, Long.MAX_VALUE))
                .isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void upperAndLowerBoundsAreInclusiveOfTheReturnedLongValue() {
        assertThat(from(randomLongs(randomValues, 0, 1)).sample(10).unique())
                .containsOnly(0L, 1L);

        assertThat(from(randomLongs(randomValues, Long.MAX_VALUE - 1, Long.MAX_VALUE)).sample(10).unique())
                .containsOnly(Long.MAX_VALUE - 1, Long.MAX_VALUE);

        assertThat(from(randomLongs(randomValues, Long.MIN_VALUE, Long.MIN_VALUE + 1)).sample(10).unique())
                .containsOnly(Long.MIN_VALUE, Long.MIN_VALUE + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnExceptionWhenUpperBoundIsLessThanTheLowerBound() {
        randomValues.randomLong(1, 0);
    }

    private static Generator<Long> randomLongs(final RandomValues randomValues, final long lower, final long upper) {
        return new Generator<Long>() {
            @Override
            public Long next() {
                return randomValues.randomLong(lower, upper);
            }
        };
    }

}