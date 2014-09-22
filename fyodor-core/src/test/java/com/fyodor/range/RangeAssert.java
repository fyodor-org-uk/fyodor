package com.fyodor.range;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.internal.Failures;

final class RangeAssert<T extends Comparable<? super T>> extends AbstractAssert<RangeAssert<T>, Range<T>> {

    RangeAssert<T> hasLowerBound(final T expectedLowerBound) {
        isNotNull();

        final T actualLowerBound = actual.lowerBound();
        if (!actualLowerBound.equals(expectedLowerBound)) {
            throw Failures.instance().failure(info, new BasicErrorMessageFactory("Expecting lower-bound %s to be equal to %s", actualLowerBound, expectedLowerBound));
        }

        return this;
    }

    public RangeAssert<T> hasUpperBound(final T expectedUpperBound) {
        isNotNull();

        final T actualUpperBound = actual.upperBound();
        if (!actualUpperBound.equals(expectedUpperBound)) {
            throw Failures.instance().failure(info, new BasicErrorMessageFactory("Expecting upper-bound %s to be equal to %s", actualUpperBound, expectedUpperBound));
        }

        return this;
    }

    static <T extends Comparable<? super T>> RangeAssert<T> assertThat(final Range<T> actual) {
        return new RangeAssert(actual);
    }

    private RangeAssert(final Range<T> actual) {
        super(actual, RangeAssert.class);
    }

    public RangeAssert<T> hasEqualLowerAndUpperBounds() {
        isNotNull();

        final T lowerBound = actual.lowerBound();
        final T upperBound = actual.upperBound();

        if (!upperBound.equals(lowerBound)) {
            throw Failures.instance().failure(info, new BasicErrorMessageFactory("Expecting upper-bound %s to be equal to the lower-bound %s", upperBound, lowerBound));
        }

        return this;
    }
}
