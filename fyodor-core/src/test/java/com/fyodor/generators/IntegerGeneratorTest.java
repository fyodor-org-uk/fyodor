package com.fyodor.generators;

import org.junit.Test;

import static com.fyodor.Sampler.from;
import static com.fyodor.range.Range.closed;
import static com.fyodor.range.Range.fixed;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegerGeneratorTest {

    @Test
    public void neverReturnsNull() {
        final Generator<Integer> generator = RDG.integer(closed(Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertThat(from(generator).sample(10000)).doesNotContainNull();
    }

    @Test
    public void generatesIntegerAcrossZero() {
        assertThat(from(RDG.integer(closed(-1, 1))).sample(100).unique()).containsOnly(-1, 0, 1);
    }

    @Test
    public void returnsIntegerWithinOneOfMinimum() {
        final Generator<Integer> generator = RDG.integer(closed(Integer.MIN_VALUE, Integer.MIN_VALUE + 1));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Integer.MIN_VALUE, Integer.MIN_VALUE + 1);
    }

    @Test
    public void returnsFixedMinimumInteger() {
        final Generator<Integer> generator = RDG.integer(fixed(Integer.MIN_VALUE));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Integer.MIN_VALUE);
    }

    @Test
    public void returnsIntegerWithinOneOfMaximum() {
        final Generator<Integer> generator = RDG.integer(closed(Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
    }

    @Test
    public void returnsFixedMaximumInteger() {
        final Generator<Integer> generator = RDG.integer(fixed(Integer.MAX_VALUE));
        assertThat(from(generator).sample(100).unique())
                .containsOnly(Integer.MAX_VALUE);
    }

    @Test
    public void returnsMinimumOfZeroAndMaximumOfSomeInteger() {
        final Generator<Integer> generator = RDG.integer(1);
        assertThat(from(generator).sample(100).unique())
                .containsOnly(0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenMaximumIsNegative() {
        RDG.integer(-1);
    }

    @Test
    public void returnsFixedIntegerForZeroMaximum() {
        assertThat(from(RDG.integer(0)).sample(100)).containsOnly(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rangeCannotBeNull() {
        RDG.integer(null);
    }

    @Test
    public void neverReturnsNullForAllIntegers() {
        assertThat(from(RDG.integer()).sample(10000).unique()).doesNotContainNull();
    }

    @Test
    public void doesNotGenerateTheSameNumberEveryTime() {
        assertThat(from(RDG.integer()).sample(1000).unique().size()).isGreaterThan(900);
    }
}
