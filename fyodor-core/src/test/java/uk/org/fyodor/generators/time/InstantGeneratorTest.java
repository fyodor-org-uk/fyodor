package uk.org.fyodor.generators.time;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler;
import uk.org.fyodor.Sampler.*;
import uk.org.fyodor.range.Range;

import java.time.*;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.*;
import static uk.org.fyodor.generators.RDG.instant;
import static uk.org.fyodor.generators.time.InstantRange.*;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public final class InstantGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @After
    public void resetTimekeeper() {
        Timekeeper.from(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
    }

    @Test
    public void generatesManyInstants() {
        final Sample<Instant> sample = from(instant()).sample(10_000);

        assertThat(sample.unique().size()).isGreaterThan(9_900);
    }

    @Test
    public void fixedInstants() {
        final Instant fixedInstant = Instant.now();
        final Sample<Instant> sample = take(from(instant(fixed(fixedInstant))));

        assertThat(sample.unique()).containsExactly(fixedInstant);
    }

    @Test
    public void closedRangeOfInstantsContainsLowerAndUpperBoundsToSecondPrecisionOnly() {
        final Instant min = Instant.now();
        final Instant max = min.plusSeconds(1);
        final Sample<Instant> sample = take(from(instant(closed(min, max))));

        assertThat(sample.unique()).containsOnly(
                min.with(ChronoField.NANO_OF_SECOND, 0),
                max.with(ChronoField.NANO_OF_SECOND, 0));
    }

    @Test
    public void afterInstantUpToAndIncludingMaxToSecondPrecisionOnly() {
        final Sample<Instant> sample = take(from(instant(after(Instant.MAX.minusSeconds(2)))));

        assertThat(sample.unique()).containsOnly(
                Instant.MAX.with(ChronoField.NANO_OF_SECOND, 0),
                Instant.MAX.minusSeconds(1).with(ChronoField.NANO_OF_SECOND, 0));
    }

    @Test
    public void beforeInstantDownToAndIncludingMinToSecondPrecisionOnly() {
        final Sample<Instant> sample = take(from(instant(before(Instant.MIN.plusSeconds(2)))));

        assertThat(sample.unique()).containsOnly(
                Instant.MIN.with(ChronoField.NANO_OF_SECOND, 0),
                Instant.MIN.plusSeconds(1).with(ChronoField.NANO_OF_SECOND, 0));
    }

    @Test
    public void futureInstantUpToAndIncludingMaxToSecondPrecisionOnly() {
        Timekeeper.from(Clock.fixed(Instant.MAX.minusSeconds(2), ZoneId.systemDefault()));

        final Sample<Instant> sample = take(from(instant(inTheFuture())));

        assertThat(sample.unique()).containsOnly(
                Instant.MAX.with(ChronoField.NANO_OF_SECOND, 0),
                Instant.MAX.minusSeconds(1).with(ChronoField.NANO_OF_SECOND, 0));
    }

    @Test
    public void pastInstantDownToAndIncludingMinToSecondPrecisionOnly() {
        Timekeeper.from(Clock.fixed(Instant.MIN.plusSeconds(2), ZoneId.systemDefault()));

        final Sample<Instant> sample = take(from(instant(inThePast())));

        assertThat(sample.unique()).containsOnly(
                Instant.MIN.with(ChronoField.NANO_OF_SECOND, 0),
                Instant.MIN.plusSeconds(1).with(ChronoField.NANO_OF_SECOND, 0));
    }

    @Test
    public void nowToNanoPrecision() {
        final Instant now = Instant.now();
        Timekeeper.from(Clock.fixed(now, ZoneId.systemDefault()));

        final Sample<Instant> sample = take(from(instant(now())));

        assertThat(sample.unique()).containsExactly(now);
    }

    @Test
    public void todayWithSecondPrecision() {
        final LocalDate today = LocalDate.now();
        final Instant startOfDay = today.atStartOfDay().toInstant(ZoneOffset.ofHours(0));
        final Instant endOfDay = today.atTime(23, 59, 59).toInstant(ZoneOffset.ofHours(0));

        Timekeeper.from(Clock.fixed(Instant.now(), ZoneId.systemDefault()));

        final Sample<Instant> sample = from(instant(today())).sample(1_000_000);

        assertThat(smallest(sample)).isEqualTo(startOfDay);
        assertThat(largest(sample)).isEqualTo(endOfDay);
        assertThat(sample.unique().size()).isGreaterThan(50_000);
    }

    @Test
    public void atDateWithSecondPrecision() {
        final LocalDate someDate = LocalDate.now().minusYears(36);
        final Instant startOfDay = someDate.atStartOfDay().toInstant(ZoneOffset.ofHours(0));
        final Instant endOfDay = someDate.atTime(23, 59, 59).toInstant(ZoneOffset.ofHours(0));

        final Sample<Instant> sample = from(instant(atDate(someDate))).sample(1_000_000);

        assertThat(smallest(sample)).isEqualTo(startOfDay);
        assertThat(largest(sample)).isEqualTo(endOfDay);
        assertThat(sample.unique().size()).isGreaterThan(50_000);
    }

    @Test
    public void instantRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("instant range cannot be null");

        //noinspection RedundantCast
        instant((InstantRange) null);
    }

    @Test
    public void rangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("instant range cannot be null");

        instant((Range<Instant>) null);
    }

    private static <T> Sample<T> take(final Sampler<T> samplerOfT) {
        return samplerOfT.sample(100);
    }
}
