package uk.org.fyodor.generators.time;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler.Sample;
import uk.org.fyodor.range.Range;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.time.LocalDate.now;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.Sampler.smallest;
import static uk.org.fyodor.generators.RDG.localDate;
import static uk.org.fyodor.generators.time.ChronoAmount.*;
import static uk.org.fyodor.generators.time.LocalDateRange.*;
import static uk.org.fyodor.range.Range.closed;

public final class LocalDateGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @After
    public void resetTemporalDataAfterTests() {
        Timekeeper.from(Clock.systemDefaultZone());
    }

    @Test
    public void localDateBetweenDefaultMinimumAndDefaultMaximum() {
        final Sample<LocalDate> sample = from(localDate()).sample(100);

        assertThat(sample.unique())
                .satisfies(s -> assertThat(sizeOf(s)).isGreaterThanOrEqualTo(99));
    }

    @Test
    public void futureDatesStartFromTomorrow() {
        final LocalDate maxLocalDate = LocalDate.MAX;
        final LocalDate tomorrow = maxLocalDate.minusDays(1);
        final LocalDate today = tomorrow.minusDays(1);

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(inTheFuture())).sample(100);

        assertThat(smallest(sample)).isEqualTo(tomorrow);
    }

    @Test
    public void futureDateWhereMaxDateIsTomorrow() {
        final LocalDate tomorrow = LocalDate.MAX;
        final LocalDate today = tomorrow.minusDays(1);

        Timekeeper.from(fixedClock(today));

        final LocalDate futureDate = localDate(inTheFuture()).next();

        assertThat(futureDate)
                .describedAs("future date should be tomorrow [%s] when today is [%s] and the max is tomorrow", tomorrow, today)
                .isEqualTo(tomorrow);
    }

    @Test
    public void pastDatesEndYesterday() {
        final LocalDate minLocalDate = LocalDate.MIN;
        final LocalDate yesterday = minLocalDate.plusDays(1);
        final LocalDate today = yesterday.plusDays(1);

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(inThePast())).sample(100);

        assertThat(sample.unique()).containsOnly(minLocalDate, yesterday);
    }

    @Test
    public void pastDateWhereMinDateIsYesterday() {
        final LocalDate yesterday = LocalDate.MIN;
        final LocalDate today = yesterday.plusDays(1);

        Timekeeper.from(fixedClock(today));

        final LocalDate pastDate = localDate(inThePast()).next();

        assertThat(pastDate)
                .describedAs("past date should be yesterday [%s] when today is [%s] and the min is yesterday", yesterday, today)
                .isEqualTo(yesterday);
    }

    @Test
    public void fixedLocalDates() {
        final LocalDate fixedDate = LocalDate.now();
        final Sample<LocalDate> sample = from(localDate(fixed(fixedDate))).sample(100);

        assertThat(sample.unique()).containsExactly(fixedDate);
    }

    @Test
    public void todayIsCurrentDateOfSystem() {
        final LocalDate systemDate = LocalDate.now();
        final Sample<LocalDate> sample = from(localDate(today())).sample(100);

        assertThat(sample.unique()).containsExactly(systemDate);
    }

    @Test
    public void fixedDateOfToday() {
        final LocalDate today = randomLocalDate();
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(today())).sample(100);
        assertThat(sample.unique()).containsExactly(today);
    }

    @Test
    public void agedInYears() {
        final LocalDate today = now();
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(closed(years(1), years(2))))).sample(10000);

        final List<LocalDate> expectedDates = datesBetween(today.minusYears(2), today.minusYears(1));

        assertThat(sample.unique())
                .hasSize(expectedDates.size())
                .containsAll(expectedDates);
    }

    @Test
    public void agedInYearsFixed() {
        final LocalDate today = now();
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(fixed(years(7))))).sample(10000);

        assertThat(sample.unique()).containsExactly(today.minusYears(7));
    }

    @Test
    public void agedInMonths() {
        final LocalDate today = now();
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(closed(months(6), months(9))))).sample(10000);

        final List<LocalDate> expectedDates = datesBetween(today.minusMonths(9), today.minusMonths(6));

        assertThat(sample.unique())
                .hasSize(expectedDates.size())
                .containsAll(expectedDates);
    }

    @Test
    public void agedInMonthsFixed() {
        final LocalDate today = now();
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(fixed(months(6))))).sample(10000);

        assertThat(sample.unique()).containsExactly(today.minusMonths(6));
    }

    @Test
    public void agedInDays() {
        final LocalDate today = now();
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(closed(days(5), days(15))))).sample(10000);

        final List<LocalDate> expectedDates = datesBetween(today.minusDays(15), today.minusDays(5));

        assertThat(sample.unique())
                .hasSize(expectedDates.size())
                .containsAll(expectedDates);
    }

    @Test
    public void agedInDaysIncludingToday() {
        final LocalDate today = now();
        final LocalDate yesterday = today.minusDays(1);
        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(closed(days(0), days(1))))).sample(10000);

        assertThat(sample.unique()).containsOnly(yesterday, today);
    }

    @Test
    public void agedInDaysFixed() {
        final LocalDate today = now();

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(aged(fixed(days(100))))).sample(10000);

        assertThat(sample.unique()).containsExactly(today.minusDays(100));
    }

    @Test
    public void nonLeapYear() {
        final LocalDate today = LocalDate.of(2001, 2, 28);
        final LocalDate yesterday = LocalDate.of(2001, 2, 27);
        final LocalDate tomorrow = LocalDate.of(2001, 3, 1);

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(closed(yesterday, tomorrow))).sample(100);

        assertThat(sample.unique()).containsOnly(yesterday, today, tomorrow);
    }

    @Test
    public void leapYear() {
        final LocalDate today = LocalDate.of(2000, 2, 28);
        final LocalDate yesterday = LocalDate.of(2000, 2, 27);
        final LocalDate tomorrow = LocalDate.of(2000, 2, 29);

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(closed(yesterday, tomorrow))).sample(100);

        assertThat(sample.unique()).containsOnly(yesterday, today, tomorrow);
    }

    @Test
    public void afterIncludesMaxDate() {
        final LocalDate max = LocalDate.MAX;
        final LocalDate dayAfterTomorrow = max.minusDays(1);
        final LocalDate tomorrow = dayAfterTomorrow.minusDays(1);
        final LocalDate today = tomorrow.minusDays(1);

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> sample = from(localDate(after(tomorrow))).sample(100);

        assertThat(sample.unique()).containsOnly(dayAfterTomorrow, max);
    }

    @Test
    public void beforeIncludesMinDate() {
        final LocalDate min = LocalDate.MIN;
        final LocalDate dayBeforeYesterday = min.plusDays(1);
        final LocalDate yesterday = dayBeforeYesterday.plusDays(1);
        final LocalDate today = yesterday.plusDays(1);

        Timekeeper.from(fixedClock(today));

        final Sample<LocalDate> samples = from(localDate(before(yesterday))).sample(100);

        assertThat(samples.unique()).containsOnly(min, dayBeforeYesterday);
    }

    @Test
    public void closedRange() {
        final LocalDate today = now();
        final LocalDate tomorrow = now().plusDays(1);
        final LocalDate dayAfterTomorrow = tomorrow.plusDays(1);

        final Sample<LocalDate> sample = from(localDate(closed(today, dayAfterTomorrow))).sample(100);

        assertThat(sample.unique()).containsOnly(today, tomorrow, dayAfterTomorrow);
    }

    @Test
    public void fixedRange() {
        final LocalDate tomorrow = now().plusDays(1);
        final LocalDate fixedDate = tomorrow.plusDays(1);

        final Sample<LocalDate> sample = from(localDate(fixed(fixedDate))).sample(100);

        assertThat(sample.unique()).containsOnly(fixedDate);
    }

    @Test
    public void localTimeRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("date range cannot be null");

        //noinspection RedundantCast
        localDate((LocalDateRange) null);
    }

    @Test
    public void rangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("date range cannot be null");

        localDate((Range<LocalDate>) null);
    }

    private static Clock fixedClock(final LocalDate today) {
        return Clock.fixed(today.atTime(12, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }

    private static List<LocalDate> datesBetween(final LocalDate start, final LocalDate end) {
        LocalDate current = start;
        final List<LocalDate> dates = new ArrayList<>();
        while (!current.isAfter(end)) {
            dates.add(start);
            current = current.plusDays(1);
        }
        return dates;
    }

    private static long sizeOf(final Iterable<?> s) {
        return stream(s.spliterator(), true).count();
    }

    private static LocalDate randomLocalDate() {
        final Random random = new Random();
        return LocalDate.of(random.nextInt(9999) + 1, random.nextInt(12) + 1, random.nextInt(28) + 1);
    }
}
