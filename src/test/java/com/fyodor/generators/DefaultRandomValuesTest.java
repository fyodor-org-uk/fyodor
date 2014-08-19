package com.fyodor.generators;

import org.junit.Test;

import java.util.Random;

import static com.fyodor.generators.Sampler.from;
import static org.assertj.core.api.Assertions.assertThat;

public final class DefaultRandomValuesTest {

    private final RandomValues randomValues = new DefaultRandomValues(new Random());

    @Test
    public void returnsNextRandomLong() {
        final long expectedLong = new Random().nextLong();
        final Random stubRandom = new Random() {
            @Override
            public long nextLong() {
                return expectedLong;
            }
        };
        final long actualLong = new DefaultRandomValues(stubRandom).randomLong();
        assertThat(actualLong).isEqualTo(expectedLong);
    }

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
    public void throwsAnExceptionWhenUpperBoundIsLessThanTheLowerBoundForRandomLong() {
        randomValues.randomLong(1, 0);
    }

    @Test
    public void returnsSingleIntegerValueWhenLowerAndUpperBoundAreEqual() {
        assertThat(randomValues.randomInteger(1, 1))
                .isEqualTo(1);

        assertThat(randomValues.randomInteger(0, 0))
                .isEqualTo(0);

        assertThat(randomValues.randomInteger(Integer.MIN_VALUE, Integer.MIN_VALUE))
                .isEqualTo(Integer.MIN_VALUE);

        assertThat(randomValues.randomInteger(Integer.MAX_VALUE, Integer.MAX_VALUE))
                .isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void upperAndLowerBoundsAreInclusiveOfTheReturnedIntegerValue() {
        assertThat(from(randomIntegers(randomValues, 0, 1)).sample(10).unique())
                .containsOnly(0, 1);

        assertThat(from(randomIntegers(randomValues, Integer.MAX_VALUE - 1, Integer.MAX_VALUE)).sample(10).unique())
                .containsOnly(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);

        assertThat(from(randomIntegers(randomValues, Integer.MIN_VALUE, Integer.MIN_VALUE + 1)).sample(10).unique())
                .containsOnly(Integer.MIN_VALUE, Integer.MIN_VALUE + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnExceptionWhenUpperBoundIsLessThanTheLowerBoundForRandomInteger() {
        randomValues.randomInteger(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnExceptionWhenMaxIsNegative() {
        randomValues.randomInteger(-1);
    }

    @Test
    public void returnsNextRandomInteger() {
        final int expectedInteger = new Random().nextInt();
        final Random stubRandom = new Random() {
            @Override
            public int nextInt(final int n) {
                return expectedInteger;
            }
        };
        final int actualInteger = new DefaultRandomValues(stubRandom).randomInteger(Math.abs(new Random().nextInt()));
        assertThat(actualInteger).isEqualTo(expectedInteger);
    }

    @Test
    public void returnsNextRandomBoolean() {
        final boolean expectedBoolean = new Random().nextBoolean();
        final Random stubRandom = new Random() {
            @Override
            public boolean nextBoolean() {
                return expectedBoolean;
            }
        };
        final boolean actualBoolean = new DefaultRandomValues(stubRandom).randomBoolean();
        assertThat(actualBoolean).isEqualTo(expectedBoolean);
    }

    private static Generator<Long> randomLongs(final RandomValues randomValues, final long lower, final long upper) {
        return new Generator<Long>() {
            @Override
            public Long next() {
                return randomValues.randomLong(lower, upper);
            }
        };
    }

    private static Generator<Integer> randomIntegers(final RandomValues randomValues, final int lower, final int upper) {
        return new Generator<Integer>() {
            @Override
            public Integer next() {
                return randomValues.randomInteger(lower, upper);
            }
        };
    }

}