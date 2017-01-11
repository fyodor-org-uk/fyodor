package uk.org.fyodor.generators.time;

import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.time.ChronoAmount.*;
import static uk.org.fyodor.generators.time.LocalDateRange.*;

public class LocalDateRangeTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void tomorrow() {
        final LocalDate today = LocalDate.now();
        final LocalDate tomorrow = today.plusDays(1);
        Timekeeper.from(Clock.fixed(today.atTime(2, 30, 45).toInstant(UTC), UTC));

        final LocalDateRange range = LocalDateRange.tomorrow();
        assertThat(range.lowerBound()).isEqualTo(tomorrow);
        assertThat(range.upperBound()).isEqualTo(tomorrow);
    }

    @Test
    public void daysCannotBeComparedWithYears() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Is.is("Days cannot be compared to Years"));

        days(5).compareTo(years(1));
    }

    @Test
    public void daysCannotBeComparedWithMonths() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Is.is("Days cannot be compared to Months"));

        days(500).compareTo(months(1));
    }

    @Test
    public void comparingDays() {
        assertThat(days(1)).isEqualByComparingTo(days(1));
        assertThat(days(1)).isLessThan(days(2));
        assertThat(days(1)).isGreaterThan(days(0));
    }

    @Test
    public void monthsCannotBeComparedWithDays() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Is.is("Months cannot be compared to Days"));

        months(1).compareTo(days(500));
    }

    @Test
    public void monthsCannotBeComparedWithYears() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Is.is("Months cannot be compared to Years"));

        months(5).compareTo(years(1));
    }

    @Test
    public void comparingMonths() {
        assertThat(months(1)).isEqualByComparingTo(months(1));
        assertThat(months(1)).isLessThan(months(2));
        assertThat(months(1)).isGreaterThan(months(0));
    }

    @Test
    public void yearsCannotBeComparedWithDays() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Is.is("Years cannot be compared to Days"));

        years(1).compareTo(days(500));
    }

    @Test
    public void yearsCannotBeComparedWithMonths() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Is.is("Years cannot be compared to Months"));

        years(1).compareTo(months(5));
    }

    @Test
    public void comparingYears() {
        assertThat(years(1)).isEqualByComparingTo(years(1));
        assertThat(years(1)).isLessThan(years(2));
        assertThat(years(1)).isGreaterThan(years(0));
    }

    @Test
    public void afterMaximumLocalDateIsIllegal() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be after the maximum date");

        after(LocalDate.MAX);
    }

    @Test
    public void inTheFutureIsIllegalWhenTodayIsMaximumLocalDate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be in the future because today is the maximum date");

        Timekeeper.from(fixedClock(LocalDate.MAX));

        inTheFuture();
    }

    @Test
    public void beforeMinimumLocalDateIsIllegal() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be before the minimum date");

        before(LocalDate.MIN);
    }

    @Test
    public void inThePastIsIllegalWhenTodayIsMinimumLocalDate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be in the past because today is the minimum date");

        Timekeeper.from(fixedClock(LocalDate.MIN));

        inThePast();
    }

    @Test
    public void agedRangeOfZeroDaysWhenTodayIsMinimumLocalDate() {
        Timekeeper.from(fixedClock(LocalDate.MIN));

        final LocalDateRange range = aged(fixed(days(0)));

        assertThat(range.lowerBound()).isEqualTo(LocalDate.MIN);
        assertThat(range.upperBound()).isEqualTo(LocalDate.MIN);
    }

    @Test
    public void agedRangeIsIllegalWhenTodayIsMinimumLocalDate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be earlier than the minimum date");

        Timekeeper.from(fixedClock(LocalDate.MIN));

        aged(fixed(days(1)));
    }

    @Test
    public void agedRangeIsIllegalWhenAgeWouldBeBeforeMinimumLocalDateForLowerBound() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be earlier than the minimum date");

        Timekeeper.from(fixedClock(LocalDate.MIN.plusDays(10)));

        aged(fixed(days(11)));
    }

    @Test
    public void agedRangeIsIllegalWhenAgeWouldBeBeforeMinimumLocalDateForUpperBound() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Date range cannot be earlier than the minimum date");

        Timekeeper.from(fixedClock(LocalDate.MIN.plusDays(10)));

        aged(closed(days(0), days(11)));
    }

    private static Clock fixedClock(final LocalDate today) {
        return Clock.fixed(today.atTime(12, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }
}