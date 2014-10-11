package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import java.util.Random;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.*;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public final class DoubleGeneratorTest extends BaseTestWithRule {

    @Test
    public void generatesFixedDouble() {
        final double fixedDouble = new Random().nextDouble();
        assertThat(from(RDG.doubleVal(fixed(fixedDouble))).sample(100))
                .containsOnly(fixedDouble);
    }

    @Test
    public void doesNotGenerateNullForAllDoubles() {
        assertThat(from(RDG.doubleVal()).sample(10000).unique())
                .doesNotContainNull();
    }

    @Test
    public void doublesDoNotExceedTheUpperBound() {
        final double upperBound = 10;
        final double lowerBound = upperBound - 0.00000000000001;
        assertThat((double) largest(from(RDG.doubleVal(closed(lowerBound, upperBound))).sample(100)))
                .isEqualTo(upperBound);
    }

    @Test
    public void doublesAreNoLessThanTheLowerBound() {
        final double lowerBound = 2.0;
        final double upperBound = lowerBound + 0.00000000000001;
        assertThat((double) smallest(from(RDG.doubleVal(closed(lowerBound, upperBound))).sample(100)))
                .isGreaterThanOrEqualTo(lowerBound)
                .isLessThan(upperBound);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rangeCannotBeNull() {
        RDG.doubleVal(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundMustBeFinite() {
        RDG.doubleVal(closed(NEGATIVE_INFINITY, 0d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void upperBoundMustBeFinite() {
        RDG.doubleVal(closed(0d, POSITIVE_INFINITY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fixedValueMustBeFinite() {
        final double illegalValue = new Random().nextBoolean() ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
        RDG.doubleVal(fixed(illegalValue));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lowerBoundCannotBeNaN() {
        RDG.doubleVal(closed(Double.NaN, 0d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void upperBoundCannotBeNaN() {
        RDG.doubleVal(closed(0d, Double.NaN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fixedRangeCannotBeNaN() {
        RDG.doubleVal(fixed(Double.NaN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fixedUpperBoundCannotBeNegative() {
        RDG.doubleVal(-1.0);
    }

    @Test
    public void fixedUpperBoundOfZeroOnlyReturnsZero() {
        assertThat(from(RDG.doubleVal(0.0)).sample(100).unique())
                .containsOnly(0.0);
    }
}
