package com.fyodor.range;

import static java.lang.String.format;

public final class Range<T extends Comparable<? super T>> {

    private final T lowerBound;
    private final T upperBound;

    private Range(final T lowerBound, final T upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Range<T> upTo(final T upperBound) {
        if (upperBound == null) {
            throw new IllegalArgumentException("Cannot extend upper-bound to null");
        }

        if (this.upperBound.compareTo(upperBound) > 0) {
            throw new IllegalArgumentException("Cannot extend upper-bound downwards");
        }

        if (this.upperBound.equals(upperBound)) {
            return this;
        }

        return new Range<T>(lowerBound, upperBound);
    }

    public Range<T> limit(final Range<T> limit) {
        if (limit == null) {
            throw new IllegalArgumentException("cannot limit range to null");
        }

        if (this.equals(limit)) {
            return this;
        }

        final T lower = lowerBound.compareTo(limit.lowerBound()) < 0
                ? limit.lowerBound()
                : (lowerBound.compareTo(limit.upperBound()) > 0 ? limit.upperBound() : this.lowerBound());

        final T upper = upperBound.compareTo(limit.upperBound()) > 0
                ? limit.upperBound()
                : (upperBound.compareTo(limit.lowerBound()) < 0 ? limit.lowerBound() : upperBound());

        return new Range<T>(lower, upper);
    }

    public T lowerBound() {
        return lowerBound;
    }

    public T upperBound() {
        return upperBound;
    }

    @Override
    public boolean equals(final Object o) {
        if ((o == null) || !(o instanceof Range)) {
            return false;
        }

        final Range that = (Range) o;

        return this.lowerBound().equals(that.lowerBound()) &&
                this.upperBound().equals(that.upperBound());
    }

    @Override
    public int hashCode() {
        int result = lowerBound != null ? lowerBound.hashCode() : 0;
        result = 31 * result + (upperBound != null ? upperBound.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return format("Range %s .. %s", lowerBound, upperBound);
    }

    public static <T extends Comparable<? super T>> Range<T> closed(final T lowerBound, final T upperBound) {
        if (lowerBound == null) {
            throw new IllegalArgumentException("lower bound cannot be null");
        }
        if (upperBound == null) {
            throw new IllegalArgumentException("upper bound cannot be null");
        }
        if (lowerBound.compareTo(upperBound) > 0) {
            throw new IllegalArgumentException("lower bound should be less-than-or-equal-to the upper bound");
        }

        return new Range<T>(lowerBound, upperBound);
    }

    public static <T extends Comparable<? super T>> Range<T> fixed(final T fixed) {
        if (fixed == null) {
            throw new IllegalArgumentException("fixed bound cannot be null");
        }

        return closed(fixed, fixed);
    }

    public static Range<Integer> greaterThan(final int lowerBound) {
        if (lowerBound == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Lower-bound cannot be greater than Integer.MAX_VALUE");
        }

        return closed(lowerBound + 1, Integer.MAX_VALUE);
    }

    public static Range<Integer> atLeast(final int lowerBound) {
        return closed(lowerBound, Integer.MAX_VALUE);
    }

    public static Range<Integer> lessThan(final int upperBound) {
        if (upperBound == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Upper-bound cannot be less than Integer.MIN_VALUE");
        }

        return closed(Integer.MIN_VALUE, upperBound - 1);
    }

    public static Range<Integer> atMost(final int upperBound) {
        return closed(Integer.MIN_VALUE, upperBound);
    }

    public static Range<Integer> positive() {
        return closed(1, Integer.MAX_VALUE);
    }

    public static Range<Integer> negative() {
        return closed(Integer.MIN_VALUE, -1);
    }

    public static Range<Integer> all() {
        return closed(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static Range<Integer> zero() {
        return fixed(0);
    }
}
