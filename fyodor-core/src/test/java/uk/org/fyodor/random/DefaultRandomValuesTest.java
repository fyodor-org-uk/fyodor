package uk.org.fyodor.random;

import org.junit.Test;
import uk.org.fyodor.generators.Generator;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;

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
    public void returnsLongBetweenMinimumAndMaximum() {
        assertThat(from(randomLongs(randomValues, Long.MIN_VALUE, Long.MAX_VALUE)).sample(1000).unique().size())
                .isGreaterThan(900);
    }

    @Test
    public void upperAndLowerBoundsAreInclusiveOfTheReturnedLongValue() {
        assertThat(from(randomLongs(randomValues, 0, 1)).sample(100).unique())
                .containsOnly(0L, 1L);

        assertThat(from(randomLongs(randomValues, Long.MAX_VALUE - 1, Long.MAX_VALUE)).sample(100).unique())
                .containsOnly(Long.MAX_VALUE - 1, Long.MAX_VALUE);

        assertThat(from(randomLongs(randomValues, Long.MIN_VALUE, Long.MIN_VALUE + 1)).sample(100).unique())
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
        assertThat(from(randomIntegers(randomValues, 0, 1)).sample(100).unique())
                .containsOnly(0, 1);

        assertThat(from(randomIntegers(randomValues, Integer.MAX_VALUE - 1, Integer.MAX_VALUE)).sample(100).unique())
                .containsOnly(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);

        assertThat(from(randomIntegers(randomValues, Integer.MIN_VALUE, Integer.MIN_VALUE + 1)).sample(100).unique())
                .containsOnly(Integer.MIN_VALUE, Integer.MIN_VALUE + 1);

    }

    @Test
    public void returnsIntegerBetweenMinimumAndMaximum() {
        assertThat(from(randomIntegers(randomValues, Integer.MIN_VALUE, Integer.MAX_VALUE)).sample(1000).unique().size())
                .isGreaterThan(900);
    }

    @Test
    public void returnsIntegerUpToAndIncludingMaximum() {
        assertThat(from(randomIntegers(randomValues, 10)).sample(100).unique())
                .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnExceptionWhenUpperBoundIsLessThanTheLowerBoundForRandomInteger() {
        randomValues.randomInteger(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnExceptionWhenMaxIntegerIsNegative() {
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

    @Test
    public void returnsFixedDouble() {
        final double fixedValue = new Random().nextDouble();
        final double actualValue = randomValues.randomDouble(fixedValue, fixedValue);
        assertThat(actualValue).isEqualTo(fixedValue);
    }

    @Test
    public void returnsUpperBoundOfRandomDoubleWithScale() {
        final RandomValues randomValues = new DefaultRandomValues(new Random() {
            @Override
            public double nextDouble() {
                return 0.99999999999999;
            }
        });
        final double actualValue = randomValues.randomDouble(0.0, 100.0, 2);
        assertThat(actualValue)
                .isEqualTo(100.00);

    }

    @Test
    public void returnsRandomDoubleWithScale() {
        final double actualValue = randomValues.randomDouble(0.0, 100.0, 2);
        assertThat(BigDecimal.valueOf(actualValue).scale())
                .isEqualTo(2);
    }

    @Test
    public void returnsRandomDoubleWithZeroScale() {
        final double actualValue = randomValues.randomDouble(1.0, 1.0, 0);
        assertThat(actualValue).isEqualTo(1.0);
    }

    @Test
    public void doubleScaledToZeroDpIsRoundedDownAndDoesNotExceedTheUpperBound() {
        final double nextDouble = 0.99999999;
        final RandomValues randomValues = new DefaultRandomValues(new Random() {
            @Override
            public double nextDouble() {
                return nextDouble;
            }
        });

        final double actualValue = randomValues.randomDouble(0, 1.99, 0);

        assertThat(actualValue)
                .describedAs("When scaled the value 1.99 should be rounded down to 1.0 rather than up to 2.0 to avoid exceeding the upper bound")
                .isEqualTo(1.0);
    }

    @Test
    public void doubleScaledToZeroDpIsRoundedUpWhenItWillNotExceedTheUpperBound() {
        final double nextDouble = 0.5;
        final RandomValues randomValues = new DefaultRandomValues(new Random() {
            @Override
            public double nextDouble() {
                return nextDouble;
            }
        });

        final double actualValue = randomValues.randomDouble(0, 1.99, 0);

        assertThat(actualValue)
                .describedAs("When scaled the value 0.995 should be rounded up to 1 as it will not exceed the upper bound")
                .isEqualTo(1);
    }

    @Test
    public void randomDoubleIsInclusiveOfUpperBound() {
        final double upperBound = 10.0;
        final double lowerBound = upperBound - 0.000000000000001;
        assertThat(from(randomDoubles(randomValues, lowerBound, upperBound)).sample(100).unique())
                .contains(upperBound);
    }

    @Test
    public void randomDoubleIsInclusiveOfLowerBound() {
        final double lowerBound = 10.0;
        final double upperBound = lowerBound + 0.000000000000001;
        assertThat(from(randomDoubles(randomValues, lowerBound, upperBound)).sample(100).unique())
                .contains(lowerBound);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateDoubleWithNegativeScale() {
        randomValues.randomDouble(1.0, 1.0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateDoubleWhenLowerBoundIsGreaterThanUpperBound() {
        randomValues.randomDouble(1.0, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateDoubleWhenLowerBoundIsGreaterThanUpperBoundForAnyGivenScale() {
        final int anyScale = new Random().nextInt();
        randomValues.randomDouble(1.0, 0.0, anyScale);
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

    private Generator<Integer> randomIntegers(final RandomValues randomValues, final int maximum) {
        return new Generator<Integer>() {
            @Override
            public Integer next() {
                return randomValues.randomInteger(maximum);
            }
        };
    }

    private Generator<Double> randomDoubles(final RandomValues randomValues, final double lower, final double upper) {
        return new Generator<Double>() {
            @Override
            public Double next() {
                return randomValues.randomDouble(lower, upper);
            }
        };
    }
}