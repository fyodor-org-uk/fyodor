package uk.org.fyodor.range;

public class Range<T extends Comparable<? super T>> {

    private final T lowerBound;
    private final T upperBound;

    protected Range(final T lowerBound, final T upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
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

        return new Range<>(lowerBound, upperBound);
    }

    public static <T extends Comparable<? super T>> Range<T> fixed(final T fixed) {
        if (fixed == null) {
            throw new IllegalArgumentException("fixed bound cannot be null");
        }
        return closed(fixed, fixed);
    }

    public Range<T> limit(final Range<T> limit) {
        if (limit == null) {
            throw new IllegalArgumentException("cannot limit range to a null limit");
        }

        final T lower = lowerBound.compareTo(limit.lowerBound) < 0
                ? limit.lowerBound
                : (lowerBound.compareTo(limit.upperBound) > 0 ? limit.upperBound : lowerBound);

        final T upper = upperBound.compareTo(limit.upperBound) > 0
                ? limit.upperBound
                : (upperBound.compareTo(limit.lowerBound) < 0 ? limit.lowerBound : upperBound);

        return new Range<>(lower, upper);
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
