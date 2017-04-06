package uk.org.fyodor.generators.time;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler;
import uk.org.fyodor.Sampler.*;
import uk.org.fyodor.range.Range;

import java.time.*;

import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.*;
import static uk.org.fyodor.generators.RDG.localTime;
import static uk.org.fyodor.generators.time.LocalTimeRange.*;

public final class LocalTimeGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @After
    public void resetTimekeeper() {
        Timekeeper.from(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
    }

    @Test
    public void smallestLocalTimeIsStartOfDayIgnoringNanosInTheSecond() {
        final Sample<LocalTime> sample = from(localTime()).sample(500_000);

        final LocalTime startOfDay = LocalTime.of(0, 0);
        assertThat(smallest(sample).with(NANO_OF_SECOND, 0)).isEqualTo(startOfDay);
    }

    @Test
    public void greatestLocalTimeIsEndOfDayIgnoringNanosInTheSecond() {
        final Sample<LocalTime> sample = from(localTime()).sample(500_000);

        final LocalTime endOfDay = LocalTime.of(23, 59, 59, 0);
        assertThat(largest(sample).with(NANO_OF_SECOND, 0)).isEqualTo(endOfDay);
    }

    @Test
    public void generatesLargeSampleOfUniqueTimesIgnoringNanosInTheSecond() {
        final Sample<LocalTime> sample = from(localTime()).sample(10_000);

        assertThat(sample.asList().stream().map(t -> t.with(NANO_OF_SECOND, 0)).collect(toSet()).size()).isGreaterThan(9300);
    }

    @Test
    public void generatesLargeSampleOfUniqueTimesIncludingNanos() {
        final Sample<LocalTime> sample = from(localTime()).sample(10_000);

        assertThat(sample.unique().stream().map(t -> t.getLong(NANO_OF_SECOND)).collect(toSet()).size()).isGreaterThan(9900);
    }

    @Test
    public void localTimeAfterSomeTimeUpToAndIncludingMaxLocalTime() {
        final Sample<LocalTime> sample = take(from(localTime(after(LocalTime.of(23, 59, 59, 999_999_996)))));

        assertThat(sample.unique()).containsOnly(
                LocalTime.of(23, 59, 59, 999_999_997),
                LocalTime.of(23, 59, 59, 999_999_998),
                LocalTime.of(23, 59, 59, 999_999_999));
    }

    @Test
    public void localTimeBeforeSomeTimeDownToAndIncludingMinLocalTime() {
        final Sample<LocalTime> sample = take(from(localTime(before(LocalTime.of(0, 0, 0, 3)))));

        assertThat(sample.unique()).containsOnly(
                LocalTime.of(0, 0, 0, 0),
                LocalTime.of(0, 0, 0, 1),
                LocalTime.of(0, 0, 0, 2));
    }

    @Test
    public void fixedLocalTime() {
        final LocalTime fixedTime = LocalTime.of(12, 54);

        assertThat(take(from(localTime(fixed(fixedTime)))).unique())
                .containsExactly(fixedTime);
    }

    @Test
    public void now() {
        final LocalTime now = LocalTime.of(12, 54);
        Timekeeper.from(Clock.fixed(now.atDate(LocalDate.now()).toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

        assertThat(take(from(localTime(LocalTimeRange.now()))).unique())
                .containsExactly(now);
    }

    @Test
    public void closedRangeOfTimesIsInclusiveOfMinAndMax() {
        final LocalTime min = LocalTime.of(12, 54, 0, 0);
        final LocalTime max = LocalTime.of(12, 54, 0, 1);

        assertThat(take(from(localTime(closed(min, max)))).unique()).containsOnly(min, max);
    }

    @Test
    public void localTimeRangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("time range cannot be null");

        //noinspection RedundantCast
        localTime((LocalTimeRange) null);
    }

    @Test
    public void rangeCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("time range cannot be null");

        localTime((Range<LocalTime>) null);
    }

    private static <T> Sample<T> take(final Sampler<T> samplerOfT) {
        return samplerOfT.sample(100);
    }
}
