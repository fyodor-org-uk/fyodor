package uk.org.fyodor.generators;

import org.junit.Test;

import java.util.Random;

import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;
import static org.assertj.core.api.Assertions.assertThat;

public final class LongGeneratorTest {

    @Test
    public void generatesLongFromClosedRangeWithinOneFromTheMinimum() {
        final Generator<Long> generator = RDG.longVal(closed(Long.MIN_VALUE, Long.MIN_VALUE + 1));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Long.MIN_VALUE, Long.MIN_VALUE + 1);
    }

    @Test
    public void generatesFixedMinimumLong() {
        final Generator<Long> generator = RDG.longVal(fixed(Long.MIN_VALUE));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Long.MIN_VALUE);
    }

    @Test
    public void generatesLongFromClosedRangeWithinOneFromTheMaximum() {
        final Generator<Long> generator = RDG.longVal(closed(Long.MAX_VALUE - 1, Long.MAX_VALUE));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Long.MAX_VALUE - 1, Long.MAX_VALUE);
    }


    @Test
    public void generatesFixedMaximumLong() {
        final Generator<Long> generator = RDG.longVal(fixed(Long.MAX_VALUE));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Long.MAX_VALUE);
    }

    @Test
    public void neverReturnsNullForRange() {
        final Generator<Long> generator = RDG.longVal(closed(Long.MIN_VALUE, Long.MAX_VALUE));
        assertThat(from(generator).sample(10000)).doesNotContainNull();
    }

    @Test
    public void neverReturnsNullForAllLongs() {
        final Generator<Long> generator = RDG.longVal();
        assertThat(from(generator).sample(10000)).doesNotContainNull();
    }

    @Test
    public void returnsAtLeastZeroAndAtMostMaximum() {
        final Generator<Long> generator = RDG.longVal(1);
        assertThat(from(generator).sample(100).unique())
                .containsOnly(0l, 1l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenMaximumIsLessThanZero() {
        RDG.longVal(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rangeCannotNeNull() {
        RDG.longVal(null);
    }

    @Test
    public void generatesFixedLong() {
        final long fixedLong = new Random().nextLong();

        final Generator<Long> generator = RDG.longVal(fixed(fixedLong));

        assertThat(from(generator).sample(100).unique())
                .containsOnly(fixedLong);
    }

    @Test
    public void generatesLongAcrossZero() {
        assertThat(from(RDG.longVal(closed(-1l, 1l))).sample(100).unique()).containsOnly(-1l, 0l, 1l);
    }
}
