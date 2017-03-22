package uk.org.fyodor.generators.time;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler.*;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.RDG;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.*;
import static uk.org.fyodor.generators.RDG.localDateTime;
import static uk.org.fyodor.generators.time.LocalDateRange.*;
import static uk.org.fyodor.generators.time.LocalTimeRange.after;
import static uk.org.fyodor.generators.time.LocalTimeRange.*;
import static uk.org.fyodor.range.Range.fixed;

public final class LocalDateTimeGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void fixedDateAndTime() {
        final Sample<LocalDateTime> sample =
                takeFrom(localDateTime(fixed(LocalDate.of(1999, 12, 31)), fixed(LocalTime.of(23, 59, 59))));

        assertThat(sample.unique())
                .containsExactly(LocalDateTime.of(1999, 12, 31, 23, 59, 59));
    }

    @Test
    public void futureDateAndFixedTime() {
        final LocalDateTime now = LocalDate.MAX.minusDays(1).atTime(0, 0, 0);
        Timekeeper.from(Clock.fixed(now.toInstant(UTC), UTC));

        final Sample<LocalDateTime> sample = takeFrom(localDateTime(inTheFuture(), fixed(LocalTime.of(23, 59, 59))));

        assertThat(sample.unique())
                .containsExactly(LocalDate.MAX.atTime(23, 59, 59));
    }

    @Test
    public void pastDateAndFixedTime() {
        final LocalDateTime now = LocalDate.MIN.plusDays(1).atTime(12, 0, 0);
        Timekeeper.from(Clock.fixed(now.toInstant(UTC), UTC));

        final Sample<LocalDateTime> sample = takeFrom(localDateTime(inThePast(), fixed(LocalTime.of(23, 59, 59))));

        assertThat(sample.unique())
                .containsExactly(LocalDate.MIN.atTime(23, 59, 59));
    }

    @Test
    public void todayAfterMidday() {
        final LocalDate today = LocalDate.now();
        Timekeeper.from(Clock.fixed(today.atTime(2, 30, 45).toInstant(UTC), UTC));

        final Sample<LocalDateTime> sample = takeFrom(localDateTime(today(), after(LocalTime.of(12, 0, 0))));

        assertThat(smallest(sample).toLocalTime())
                .isAfterOrEqualTo(LocalTime.of(12, 0, 0));

        assertThat(largest(sample).toLocalTime())
                .isBeforeOrEqualTo(LocalTime.of(23, 59, 59));

        assertThat(sample.asList().stream()
                .map(LocalDateTime::toLocalDate)
                .collect(toSet()))
                .containsExactly(today);
    }

    @Test
    public void tomorrowBetweenNineAndFive() {
        final LocalDate today = LocalDate.now();
        Timekeeper.from(Clock.fixed(today.atTime(2, 30, 45).toInstant(UTC), UTC));

        final Sample<LocalDateTime> sample = takeFrom(localDateTime(tomorrow(), between(9, 17)));

        assertThat(smallest(sample).toLocalTime())
                .isAfterOrEqualTo(LocalTime.of(9, 0, 0));

        assertThat(largest(sample).toLocalTime())
                .isBeforeOrEqualTo(LocalTime.of(17, 0, 0));

        assertThat(sample.asList().stream()
                .map(LocalDateTime::toLocalDate)
                .collect(toSet()))
                .containsExactly(today.plusDays(1));
    }

    @Test
    public void localDateTimeBetweenMinAndMax() {
        final Sample<LocalDateTime> sample = from(localDateTime()).sample(10000);

        final Set<LocalTime> times = sample.asList().stream()
                .map(LocalDateTime::toLocalTime)
                .map(lt -> lt.withNano(0))
                .collect(toSet());

        final Set<LocalDate> dates = sample.asList().stream()
                .map(LocalDateTime::toLocalDate)
                .collect(toSet());

        assertThat(times.size()).isGreaterThan(9000);
        assertThat(dates.size()).isGreaterThan(9000);
    }

    @Test
    public void dateRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("date range cannot be null");

        RDG.localDateTime(null, noon());
    }

    @Test
    public void timeRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("time range cannot be null");

        RDG.localDateTime(today(), null);
    }

    private static Sample<LocalDateTime> takeFrom(final Generator<LocalDateTime> generator) {
        return from(generator).sample(100);
    }

}
