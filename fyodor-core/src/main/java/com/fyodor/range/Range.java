package com.fyodor.range;

public final class Range<T extends Comparable> {

    private final T lowerBound;
    private final T upperBound;

    private Range(final T lowerBound, final T upperBound) {
        if (lowerBound == null) {
            throw new IllegalArgumentException("lower bound cannot be null");
        }
        if (upperBound == null) {
            throw new IllegalArgumentException("upper bound cannot be null");
        }
        if (lowerBound.compareTo(upperBound) > 0) {
            throw new IllegalArgumentException("lower bound should be less-than-or-equal-to the upper bound");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public static <T extends Comparable<?>> Range<T> closed(final T lowerBound, final T upperBound) {
        return new Range<T>(lowerBound, upperBound);
    }

    public static <T extends Comparable<?>> Range<T> fixed(final T fixed) {
        return closed(fixed, fixed);
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
}
