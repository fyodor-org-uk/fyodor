package com.fyodor.generators;

import com.google.common.collect.Range;
import org.joda.time.LocalDate;
import org.junit.Test;

import static com.fyodor.generators.Sampler.from;
import static com.google.common.collect.Range.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.LocalDate.now;

public final class LocalDateGeneratorTest {

    private static final LocalDate MIN = new LocalDate(0, 1, 1);
    private static final LocalDate MAX = new LocalDate(2999, 12, 31);

    private final int sampleSize = 1000;

    @Test
    public void neverReturnsNull() {
        final Generator<LocalDate> generator = RDG.localDate(Range.<LocalDate>all());
        assertThat(from(generator).sample(10000))
                .doesNotContainNull();
    }

    @Test
    public void closedRangeWhereLowerAndUpperBoundsAreBothTheSameShouldReturnTheSameDateEveryTime() {
        assertThat(from(RDG.localDate(closed(now(), now()))).sample(sampleSize).unique())
                .containsOnly(now());

        assertThat(from(RDG.localDate(closed(MIN, MIN))).sample(sampleSize).unique())
                .containsOnly(MIN);

        assertThat(from(RDG.localDate(closed(MAX, MAX))).sample(sampleSize).unique())
                .containsOnly(MAX);
    }

    @Test
    public void closedRangeWithOneSingleDayBetweenLowerBoundAndUpperBoundShouldReturnBothBounds() {
        assertThat(from(RDG.localDate(closed(now(), now().plusDays(1)))).sample(sampleSize).unique())
                .containsOnly(now(), now().plusDays(1));

        assertThat(from(RDG.localDate(closed(MIN, MIN.plusDays(1)))).sample(sampleSize).unique())
                .containsOnly(MIN, MIN.plusDays(1));

        assertThat(from(RDG.localDate(closed(MAX.minusDays(1), MAX))).sample(sampleSize).unique())
                .containsOnly(MAX.minusDays(1), MAX);
    }

    @Test
    public void openRangeSpanningThreeDaysShouldReturnMiddleDateOnly() {
        final Generator<LocalDate> generator = RDG.localDate(open(now().minusDays(1), now().plusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique()).containsOnly(now());
    }

    @Test
    public void openClosedRangeSpanningThreeDaysShouldReturnMiddleAndLatestDateOnly() {
        final Generator<LocalDate> generator = RDG.localDate(openClosed(now().minusDays(1), now().plusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(now(), now().plusDays(1));
    }

    @Test
    public void closedOpenRangeSpanningThreeDaysShouldReturnEarliestAndMiddleDateOnly() {
        final Generator<LocalDate> generator = RDG.localDate(closedOpen(now().minusDays(1), now().plusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(now().minusDays(1), now());
    }

    @Test
    public void greaterThanRangeOneDayBeforeDefaultUpperBoundReturnsOnlyTheDefaultUpperBound() {
        final Generator<LocalDate> generator = RDG.localDate(greaterThan(MAX.minusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MAX);
    }

    @Test
    public void lessThanRangeOneDayAfterDefaultLowerBoundReturnsOnlyTheDefaultLowerBound() {
        final Generator<LocalDate> generator = RDG.localDate(lessThan(MIN.plusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MIN);
    }

    @Test
    public void atMostRangeForDefaultLowerBoundReturnsOnlyTheDefaultLowerBound() {
        final Generator<LocalDate> generator = RDG.localDate(atMost(MIN));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MIN);
    }

    @Test
    public void atLeastRangeForDefaultUpperBoundReturnsOnlyTheDefaultUpperBound() {
        final Generator<LocalDate> generator = RDG.localDate(atLeast(MAX));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MAX);
    }

    @Test
    public void rangeExceedingUpperBoundLimitReturnsAtMostTheUpperBound() {
        final Generator<LocalDate> generator = RDG.localDate(closed(MAX, MAX.plusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MAX);
    }

    @Test
    public void rangePrecedingLowerBoundReturnsAtLeastTheLowerBound() {
        final Generator<LocalDate> generator = RDG.localDate(closed(MIN.minusDays(1), MIN));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MIN);
    }
}
