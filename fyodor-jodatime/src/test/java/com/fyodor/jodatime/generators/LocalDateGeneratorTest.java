package com.fyodor.jodatime.generators;

import com.fyodor.generators.Generator;
import org.joda.time.LocalDate;
import org.junit.Test;

import static com.fyodor.jodatime.generators.JodaTimeRanges.*;
import static com.fyodor.jodatime.generators.RDG.localDate;
import static com.fyodor.jodatime.generators.Sampler.*;
import static com.fyodor.range.Range.closed;
import static com.fyodor.range.Range.fixed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.LocalDate.now;

public final class LocalDateGeneratorTest {

    private static final LocalDate MIN = new LocalDate(0, 1, 1);
    private static final LocalDate MAX = new LocalDate(2999, 12, 31);

    private final int sampleSize = 1000;

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateLocalDateForNullRange() {
        localDate(null);
    }

    @Test
    public void neverReturnsNull() {
        final Generator<LocalDate> generator = localDate(closed(MIN, MAX));
        assertThat(from(generator).sample(10000))
                .doesNotContainNull();
    }

    @Test
    public void returnsFixedLocalDate() {
        final LocalDate fixedDate = now().minusDays(55);
        assertThat(from(localDate(fixed(fixedDate))).sample(10).unique())
                .containsOnly(fixedDate);
    }

    @Test
    public void returnsDateAgedInDays() {
        assertThat(from(localDate(aged(days(18, 19)))).sample(10).unique())
                .containsExactly(now().minusDays(18), now().minusDays(19));
    }

    @Test
    public void returnsDateAgedInMonths() {
        final Sampler.Sample<LocalDate> sample = from(localDate(aged(months(18, 19)))).sample(100);
        final LocalDate smallest = smallest(sample);
        final LocalDate largest = largest(sample);
        assertThat(smallest).isEqualTo(now().minusMonths(19));
        assertThat(largest).isEqualTo(now().minusMonths(18));
    }

    @Test
    public void returnsDateAgedInYears() {
        final Sampler.Sample<LocalDate> sample = from(localDate(aged(years(18, 19)))).sample(10000);
        final LocalDate smallest = smallest(sample);
        final LocalDate largest = largest(sample);
        assertThat(smallest).isEqualTo(now().minusYears(19));
        assertThat(largest).isEqualTo(now().minusYears(18));
    }

    @Test
    public void returnsFutureDateOnly() {
        final LocalDate smallest = smallest(from(localDate(futureDate())).sample(10000));
        assertThat(smallest.compareTo(now()) > 0).isTrue();
    }

    @Test
    public void returnsPastDateOnly() {
        final LocalDate smallest = smallest(from(localDate(pastDate())).sample(10000));
        assertThat(smallest.compareTo(now()) < 0).isTrue();
    }

    @Test
    public void neverReturnsNullForAllDates() {
        assertThat(from(localDate(allDates())).sample(10000)).doesNotContainNull();
    }

    @Test
    public void closedRangeWhereLowerAndUpperBoundsAreBothTheSameShouldReturnTheSameDateEveryTime() {
        assertThat(from(localDate(closed(now(), now()))).sample(sampleSize).unique())
                .containsOnly(now());

        assertThat(from(localDate(closed(MIN, MIN))).sample(sampleSize).unique())
                .containsOnly(MIN);

        assertThat(from(localDate(closed(MAX, MAX))).sample(sampleSize).unique())
                .containsOnly(MAX);
    }

    @Test
    public void closedRangeWithOneSingleDayBetweenLowerBoundAndUpperBoundShouldReturnBothBounds() {
        assertThat(from(localDate(closed(now(), now().plusDays(1)))).sample(sampleSize).unique())
                .containsOnly(now(), now().plusDays(1));

        assertThat(from(localDate(closed(MIN, MIN.plusDays(1)))).sample(sampleSize).unique())
                .containsOnly(MIN, MIN.plusDays(1));

        assertThat(from(localDate(closed(MAX.minusDays(1), MAX))).sample(sampleSize).unique())
                .containsOnly(MAX.minusDays(1), MAX);
    }

    @Test
    public void rangeExceedingUpperBoundLimitReturnsAtMostTheUpperBound() {
        final Generator<LocalDate> generator = localDate(closed(MAX, MAX.plusDays(1)));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MAX);
    }

    @Test
    public void rangePrecedingLowerBoundReturnsAtLeastTheLowerBound() {
        final Generator<LocalDate> generator = localDate(closed(MIN.minusDays(1), MIN));
        assertThat(from(generator).sample(sampleSize).unique())
                .containsOnly(MIN);
    }
}
