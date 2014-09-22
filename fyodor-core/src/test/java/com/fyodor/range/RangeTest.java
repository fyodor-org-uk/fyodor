package com.fyodor.range;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static com.fyodor.range.Range.*;
import static com.fyodor.range.RangeAssert.assertThat;

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
    public void greaterThanIntegerRangeStartsFromOneAfterLowerBoundUpToMaximum() {
        final int lowerBound = 55;
        assertThat(greaterThan(lowerBound))
                .hasLowerBound(lowerBound + 1)
                .hasUpperBound(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotBeGreaterThanMaximumInteger() {
        greaterThan(Integer.MAX_VALUE);
    }

    @Test
    public void lessThanIntegerRangeStartsFromMinimumUpToOneBeforeUpperBound() {
        final int upperBound = 456;
        assertThat(lessThan(upperBound))
                .hasLowerBound(Integer.MIN_VALUE)
                .hasUpperBound(upperBound - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotBeLessThanMinimumInteger() {
        lessThan(Integer.MIN_VALUE);
    }

    @Test
    public void atLeastIntegerRangeStartsFromLowerBoundUpToMaximum() {
        final int lowerBound = 999;
        assertThat(atLeast(lowerBound))
                .hasLowerBound(lowerBound)
                .hasUpperBound(Integer.MAX_VALUE);
    }

    @Test
    public void atMostIntegerRangeStartsFromMinimumUpToUpperBound() {
        final int upperBound = 1243;
        assertThat(atMost(upperBound))
                .hasLowerBound(Integer.MIN_VALUE)
                .hasUpperBound(upperBound);
    }

    @Test
    public void positiveIntegerRangeIsGreaterThanZero() {
        assertThat(positive())
                .hasLowerBound(1)
                .hasUpperBound(Integer.MAX_VALUE);
    }

    @Test
    public void negativeIntegerRangeIsLessThanZero() {
        assertThat(negative())
                .hasLowerBound(Integer.MIN_VALUE)
                .hasUpperBound(-1);
    }

    @Test
    public void allIntegerRangeIsFromMinimumToMaximum() {
        assertThat(all())
                .hasLowerBound(Integer.MIN_VALUE)
                .hasUpperBound(Integer.MAX_VALUE);
    }

    @Test
    public void zeroUpToUpperBoundIntegerRange() {
        final int upperBound = 78474;
        assertThat(zero().upTo(upperBound))
                .hasLowerBound(0)
                .hasUpperBound(upperBound);
    }

    @Test
    public void upToModifierExtendsUpperBoundIfMoreThanTheCurrentUpperBound() {
        assertThat(closed(0, 10).upTo(15))
                .hasLowerBound(0)
                .hasUpperBound(15);
    }

    @Test
    public void canApplyUpToModificationMultipleTimes() {
        assertThat(closed(0, 10).upTo(15).upTo(17).upTo(100).upTo(150).upTo(151))
                .hasLowerBound(0)
                .hasUpperBound(151);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotExtendUpperBoundDownwards() {
        assertThat(closed(0, 10).upTo(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotExtendUpperBoundToNull() {
        closed(0, 10).upTo(null);
    }

    @Test
    public void closedRangeMayHaveEqualLowerBoundAndUpperBound() {
        assertThat(closed(10, 10)).hasEqualLowerAndUpperBounds();
    }

    @Test
    public void fixedBoundHasEqualLowerBoundAndUpperBound() {
        assertThat(fixed(10)).hasEqualLowerAndUpperBounds();
    }

    @Test
    public void shouldLimitLowerBoundWhenItIsLessThanTheLowerLimit() {
        assertThat(closed(10, 20).limit(closed(15, 20)))
                .hasLowerBound(15)
                .hasUpperBound(20);
    }

    @Test
    public void shouldLimitUpperBoundWhenItExceedsTheUpperLimit() {
        assertThat(closed(10, 20).limit(closed(10, 15)))
                .hasLowerBound(10)
                .hasUpperBound(15);
    }

    @Test
    public void shouldNotLimitLowerBoundWhenItIsGreaterThanTheLowerLimit() {
        assertThat(closed(10, 20).limit(closed(0, 20)))
                .hasLowerBound(10)
                .hasUpperBound(20);
    }

    @Test
    public void shouldNotLimitBoundsWhenWhenTheyAreTheSameAsTheLimit() {
        assertThat(closed(10, 20).limit(closed(10, 20)))
                .hasLowerBound(10)
                .hasUpperBound(20);
    }

    @Test
    public void shouldNotLimitUpperBoundWhenItIsLessThanTheUpperLimit() {
        assertThat(closed(10, 20).limit(closed(10, 25)))
                .hasLowerBound(10)
                .hasUpperBound(20);
    }

    @Test
    public void shouldLimitUpperBoundToLowerLimitWhenItLessThanTheLowerLimit() {
        assertThat(closed(10, 20).limit(closed(30, 40)))
                .hasLowerBound(30)
                .hasUpperBound(30);
    }

    @Test
    public void shouldLimitLowerBoundToUpperLimitWhenItIsGreaterThanTheUpperLimit() {
        assertThat(closed(10, 20).limit(closed(1, 5)))
                .hasLowerBound(5)
                .hasUpperBound(5);
    }

    @Test
    public void shouldLimitBothBoundsToFixedLimit() {
        assertThat(closed(10, 20).limit(fixed(15)))
                .hasLowerBound(15)
                .hasUpperBound(15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotLimitRangeToNullRange() {
        closed(0, 10).limit(null);
    }

    @Test
    public void shouldNotBeEqualToNonRange() {
        //noinspection EqualsBetweenInconvertibleTypes
        Assertions.assertThat(closed(1, 5).equals("not a range")).isFalse();
    }

    @Test
    public void shouldNotBeEqualToNull() {
        //noinspection ObjectEqualsNull
        Assertions.assertThat(closed(1, 5).equals(null)).isFalse();
    }

    @Test
    public void shouldBeReflexive() {
        final Range<Integer> range = closed(10, 20);
        Assertions.assertThat(range.equals(range)).isTrue();
    }

    @Test
    public void shouldBeSymmetric() {
        final Range<Integer> firstRange = closed(10, 20);
        final Range<Integer> secondRange = closed(10, 20);
        Assertions.assertThat(firstRange.equals(secondRange)).isTrue();
        Assertions.assertThat(secondRange.equals(firstRange)).isTrue();
    }

    @Test
    public void shouldBeTransitive() {
        final Range<Integer> firstRange = closed(10, 20);
        final Range<Integer> secondRange = closed(10, 20);
        final Range<Integer> thirdRange = closed(10, 20);
        Assertions.assertThat(firstRange.equals(secondRange)).isTrue();
        Assertions.assertThat(secondRange.equals(thirdRange)).isTrue();
        Assertions.assertThat(firstRange.equals(thirdRange)).isTrue();
    }

    @Test
    public void equalClosedRangesShouldHaveTheSameHashCode() {
        Assertions.assertThat(closed(10, 20).hashCode())
                .isEqualTo(closed(10, 20).hashCode());
    }

    @Test
    public void differentRangesShouldLikelyHaveDifferentHashCodeValues() {
        final Range<Integer> firstRange = closed(10, 20);
        final Range<Integer> secondRange = closed(67, 200);

        Assertions.assertThat(firstRange.hashCode())
                .isNotEqualTo(secondRange.hashCode());
    }

    @Test
    public void consistentlyHasTheSameHashCode() {
        final Range<Integer> range = closed(67, 200);
        final int hashCode = range.hashCode();
        for (int i = 0; i < 1000; i++) {
            Assertions.assertThat(range.hashCode()).isEqualTo(hashCode);
        }
    }
}