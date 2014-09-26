package uk.org.fyodor.range;

import org.junit.Test;

import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;
import static org.assertj.core.api.Assertions.assertThat;

public final class RangeTest {

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundCannotBeNull() {
        closed(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void upperBoundCannotBeNull() {
        closed(100, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundShouldNotBeMoreThanUpperBound() {
        closed(5, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fixedBoundCannotBeNull() {
        //noinspection RedundantCast
        fixed((Comparable) null);
    }

    @Test
    public void closedRangeMayHaveEqualLowerBoundAndToUpperBound() {
        final Range<Integer> range = closed(10, 10);
        assertThat(range.lowerBound()).isEqualTo(range.upperBound());
    }

    @Test
    public void fixedBoundHasEqualLowerBoundAndUpperBound() {
        final Range<Integer> range = fixed(10);
        assertThat(range.lowerBound()).isEqualTo(range.upperBound());
    }

    @Test
    public void shouldLimitLowerBoundWhenItIsLessThanTheLowerLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(15, 20));
        assertThat(range.lowerBound()).isEqualTo(15);
    }

    @Test
    public void shouldLimitUpperBoundWhenItExceedsTheUpperLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(10, 15));
        assertThat(range.upperBound()).isEqualTo(15);
    }

    @Test
    public void shouldNotLimitLowerBoundWhenItIsGreaterThanTheLowerLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(0, 20));
        assertThat(range.lowerBound()).isEqualTo(10);
    }

    @Test
    public void shouldNotLimitLowerBoundWhenItIsTheSameAsTheLowerLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(10, 20));
        assertThat(range.lowerBound()).isEqualTo(10);
    }

    @Test
    public void shouldNotLimitUpperBoundWhenItIsLessThanTheUpperLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(10, 25));
        assertThat(range.upperBound()).isEqualTo(20);
    }

    @Test
    public void shouldNotLimitUpperBoundWhenItIsTheSameAsTheUpperLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(10, 20));
        assertThat(range.upperBound()).isEqualTo(20);
    }

    @Test
    public void shouldLimitUpperBoundToLowerLimitWhenItLessThanTheLowerLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(30, 40));
        assertThat(range.upperBound()).isEqualTo(30);
    }

    @Test
    public void shouldLimitLowerBoundToUpperLimitWhenItIsGreaterThanTheUpperLimit() {
        final Range<Integer> range = closed(10, 20).limit(closed(1, 5));
        assertThat(range.lowerBound()).isEqualTo(5);
    }

    @Test
    public void shouldLimitBothBoundsToLimit() {
        final Range<Integer> range = closed(10, 20).limit(fixed(15));
        assertThat(range.lowerBound()).isEqualTo(15);
        assertThat(range.upperBound()).isEqualTo(15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotLimitRangeToNullRange() {
        closed(0, 10).limit(null);
    }

    @Test
    public void shouldNotBeEqualToNonRange() {
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(closed(1, 5).equals("not a range")).isFalse();
    }

    @Test
    public void shouldNotBeEqualToNull() {
        //noinspection ObjectEqualsNull
        assertThat(closed(1, 5).equals(null)).isFalse();
    }

    @Test
    public void shouldBeReflexive() {
        final Range<Integer> range = closed(10, 20);
        assertThat(range.equals(range)).isTrue();
    }

    @Test
    public void shouldBeSymmetric() {
        final Range<Integer> firstRange = closed(10, 20);
        final Range<Integer> secondRange = closed(10, 20);
        assertThat(firstRange.equals(secondRange)).isTrue();
        assertThat(secondRange.equals(firstRange)).isTrue();
    }

    @Test
    public void shouldBeTransitive() {
        final Range<Integer> firstRange = closed(10, 20);
        final Range<Integer> secondRange = closed(10, 20);
        final Range<Integer> thirdRange = closed(10, 20);
        assertThat(firstRange.equals(secondRange)).isTrue();
        assertThat(secondRange.equals(thirdRange)).isTrue();
        assertThat(firstRange.equals(thirdRange)).isTrue();
    }

    @Test
    public void equalClosedRangesShouldHaveTheSameHashCode() {
        assertThat(closed(10, 20).hashCode())
                .isEqualTo(closed(10, 20).hashCode());
    }

    @Test
    public void differentRangesShouldLikelyHaveDifferentHashCodeValues() {
        final Range<Integer> firstRange = closed(10, 20);
        final Range<Integer> secondRange = closed(67, 200);

        assertThat(firstRange.hashCode())
                .isNotEqualTo(secondRange.hashCode());
    }

    @Test
    public void consistentlyHasTheSameHashCode() {
        final Range<Integer> range = closed(67, 200);
        final int hashCode = range.hashCode();
        for (int i = 0; i < 1000; i++) {
            assertThat(range.hashCode()).isEqualTo(hashCode);
        }
    }
}