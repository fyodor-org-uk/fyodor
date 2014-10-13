package uk.org.fyodor.jodatime.generators;

import org.joda.time.LocalDate;
import org.junit.Test;
import uk.org.fyodor.generators.Generator;

import static org.joda.time.Days.days;
import static org.joda.time.LocalDate.now;
import static org.joda.time.Months.months;
import static org.joda.time.Years.years;
import static uk.org.fyodor.jodatime.generators.JodaTimeAssertions.assertThat;
import static uk.org.fyodor.jodatime.generators.RDG.localDate;
import static uk.org.fyodor.jodatime.generators.Sampler.*;
import static uk.org.fyodor.jodatime.range.LocalDateRange.*;

public final class LocalDateGeneratorTest {

    private static final LocalDate MIN = new LocalDate(0, 1, 1);
    private static final LocalDate MAX = new LocalDate(2999, 12, 31);

    private final int sampleSize = 1000;

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateLocalDateForNullRange() {
        localDate(null);
    }

    @Test
    public void generatesFixedMinLocalDate() {
        assertThat(from(localDate(fixed(MIN))).sample(sampleSize).unique())
                .containsExactly(MIN);
    }

    @Test
    public void generatesFixedMaxLocalDate() {
        assertThat(from(localDate(fixed(MAX))).sample(sampleSize).unique())
                .containsExactly(MAX);
    }

    @Test
    public void generatesUniqueDateBetweenDefaultMinAndMax() {
        final Sample<LocalDate> sample = from(localDate()).sample(sampleSize);

        assertThat(sample.unique().size())
                .describedAs("There should be a significant number of unique values")
                .isGreaterThan(sampleSize - 10);

        assertThat(smallest(sample)).isGreaterThanOrEqualTo(MIN);
        assertThat(largest(sample)).isLessThanOrEqualTo(MAX);
    }

    @Test
    public void generatesFutureDatesAtMostTheMaximum() {
        final Sample<LocalDate> sample = from(localDate(inTheFuture())).sample(sampleSize);

        assertThat(sample.unique().size())
                .describedAs("There should be a significant number of unique values")
                .isGreaterThan(sampleSize - 10);

        assertThat(smallest(sample)).isGreaterThan(now());
        assertThat(largest(sample)).isLessThanOrEqualTo(MAX);
    }

    @Test
    public void generatesPastDatesAtLeastTheMinimum() {
        final Sample<LocalDate> sample = from(localDate(inThePast())).sample(sampleSize);

        assertThat(sample.unique().size())
                .describedAs("There should be a significant number of unique values")
                .isGreaterThan(sampleSize - 10);

        assertThat(smallest(sample)).isGreaterThanOrEqualTo(MIN);
        assertThat(largest(sample)).isLessThan(now());
    }

    @Test
    public void generatesLocalDateGreaterThanLowerBoundAndLessThanOrEqualToMax() {
        final LocalDate minimum = MAX.minusDays(1);

        final Sample<LocalDate> sample = from(localDate(greaterThan(minimum))).sample(10);

        assertThat(smallest(sample)).isGreaterThan(minimum);
        assertThat(largest(sample)).isEqualTo(MAX);
    }

    @Test
    public void generatesLocalDateLessThanUpperBoundAndGreaterThanOrEqualToMin() {
        final LocalDate maximum = MIN.plusDays(1);

        final Sample<LocalDate> sample = from(localDate(lessThan(maximum))).sample(10);

        assertThat(smallest(sample)).isEqualTo(MIN);
        assertThat(largest(sample)).isLessThan(maximum);
    }

    @Test
    public void generatesPastDateAgedInYears() {
        final Sample<LocalDate> sample = from(localDate(aged(closed(years(9), years(10))))).sample(2000);

        assertThat(smallest(sample)).isEqualTo(now().minusYears(10));
        assertThat(largest(sample)).isEqualTo(now().minusYears(9));
    }

    @Test
    public void generatesPastDateAgedInMonths() {
        final Sample<LocalDate> sample = from(localDate(aged(closed(months(9), months(10))))).sample(sampleSize);

        assertThat(smallest(sample)).isEqualTo(now().minusMonths(10));
        assertThat(largest(sample)).isEqualTo(now().minusMonths(9));
    }

    @Test
    public void generatesPastDateAgedInDays() {
        final Sample<LocalDate> sample = from(localDate(aged(closed(days(9), days(10))))).sample(10);

        assertThat(smallest(sample)).isEqualTo(now().minusDays(10));
        assertThat(largest(sample)).isEqualTo(now().minusDays(9));
    }

    @Test
    public void neverReturnsNullForLocalDateWithRange() {
        final Generator<LocalDate> generator = localDate(closed(MIN, MAX));
        assertThat(from(generator).sample(10000))
                .doesNotContainNull();
    }

    @Test
    public void closedRangeWhereLowerAndUpperBoundsAreBothTheSameShouldReturnTheSameDateEveryTime() {
        localDate(closed(now().plusDays(1), now().plusYears(100)));
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
