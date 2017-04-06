package uk.org.fyodor.generators.time;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler.Sample;
import uk.org.fyodor.generators.Generator;

import java.time.*;
import java.util.Set;

import static java.time.Clock.fixed;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.RDG.*;
import static uk.org.fyodor.range.Range.fixed;

public final class ZonedDateTimeGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void zoneIsAlwaysTheCurrentTimekeeperZone() {
        final ZoneId currentZone = zoneId().next();
        final LocalDateTime currentDateTime = localDateTime().next();
        Timekeeper.from(clockOf(currentDateTime.atZone(currentZone)));

        final Sample<ZonedDateTime> sample = takeFrom(zonedDateTime());

        assertThat(uniqueZonesFrom(sample)).containsOnly(currentZone);
    }

    @Test
    public void fixedZonedDateTime() {
        final ZoneId currentZone = zoneId().next();
        final LocalDateTime currentDateTime = localDateTime().next();
        Timekeeper.from(clockOf(currentDateTime.atZone(currentZone)));

        final LocalDate date = LocalDate.of(1999, 12, 31);
        final LocalTime time = LocalTime.of(23, 59, 59);
        final Sample<ZonedDateTime> sample = takeFrom(zonedDateTime(fixed(date), fixed(time)));

        assertThat(sample.unique()).containsOnly(ZonedDateTime.of(date, time, currentZone));
    }

    @Test
    public void dateRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("date range cannot be null");

        zonedDateTime(null, LocalTimeRange.all());
    }

    @Test
    public void timeRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("time range cannot be null");

        zonedDateTime(LocalDateRange.all(), null);
    }

    private static Clock clockOf(final ZonedDateTime zonedDateTime) {
        return fixed(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    private static Sample<ZonedDateTime> takeFrom(final Generator<ZonedDateTime> generatorOfT) {
        return from(generatorOfT).sample(1000);
    }

    private static Set<ZoneId> uniqueZonesFrom(final Sample<ZonedDateTime> sample) {
        return sample.unique().stream().map(ZonedDateTime::getZone).collect(toSet());
    }
}
