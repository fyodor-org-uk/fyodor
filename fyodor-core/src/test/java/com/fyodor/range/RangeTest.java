package com.fyodor.range;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class RangeTest {

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundCannotBeNull() {
        Range.closed(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void upperBoundCannotBeNull() {
        Range.closed(100, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundShouldNotBeMoreThanUpperBound() {
        Range.closed(5, 4);
    }

    @Test
    public void closedRangeMayHaveEqualLowerBoundAndToUpperBound() {
        final Range<Integer> range = Range.closed(10, 10);
        assertThat(range.lowerBound()).isEqualTo(range.upperBound());
    }

    @Test
    public void fixedBoundHasEqualLowerBoundAndUpperBound() {
        final Range<Integer> range = Range.fixed(10);
        assertThat(range.lowerBound()).isEqualTo(range.upperBound());
    }

    @Test
    public void shouldNotBeEqualToNonRange() {
        assertThat(Range.closed(1, 5).equals("not a range")).isFalse();
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(Range.closed(1, 5).equals(null)).isFalse();
    }

    @Test
    public void shouldBeReflexive() {
        final Range<Integer> range = Range.closed(10, 20);
        assertThat(range.equals(range)).isTrue();
    }

    @Test
    public void shouldBeSymmetric() {
        final Range<Integer> firstRange = Range.closed(10, 20);
        final Range<Integer> secondRange = Range.closed(10, 20);
        assertThat(firstRange.equals(secondRange)).isTrue();
        assertThat(secondRange.equals(firstRange)).isTrue();
    }

    @Test
    public void shouldBeTransitive() {
        final Range<Integer> firstRange = Range.closed(10, 20);
        final Range<Integer> secondRange = Range.closed(10, 20);
        final Range<Integer> thirdRange = Range.closed(10, 20);
        assertThat(firstRange.equals(secondRange)).isTrue();
        assertThat(secondRange.equals(thirdRange)).isTrue();
        assertThat(firstRange.equals(thirdRange)).isTrue();
    }

    @Test
    public void equalClosedRangesShouldHaveTheSameHashCode() {
        assertThat(Range.closed(10, 20).hashCode())
                .isEqualTo(Range.closed(10, 20).hashCode());
    }

    @Test
    public void differentRangesShouldLikelyHaveDifferentHashCodeValues() {
        final Range<Integer> firstRange = Range.closed(10, 20);
        final Range<Integer> secondRange = Range.closed(67, 200);

        assertThat(firstRange.hashCode())
                .isNotEqualTo(secondRange.hashCode());
    }

    @Test
    public void consistentlyHasTheSameHashCode() {
        final Range<Integer> range = Range.closed(67, 200);
        final int hashCode = range.hashCode();
        for (int i = 0; i < 1000; i++) {
            assertThat(range.hashCode()).isEqualTo(hashCode);
        }
    }
}