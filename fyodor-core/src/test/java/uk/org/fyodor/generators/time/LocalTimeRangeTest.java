package uk.org.fyodor.generators.time;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.generators.RDG;

import java.time.LocalTime;

import static java.time.LocalTime.*;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.time.LocalTimeRange.*;

public final class LocalTimeRangeTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void noon() {
        final LocalTimeRange noon = LocalTimeRange.noon();
        assertThat(noon.lowerBound()).isEqualTo(NOON);
        assertThat(noon.upperBound()).isEqualTo(NOON);
    }

    @Test
    public void afterNoon() {
        final LocalTimeRange noon = LocalTimeRange.afterNoon();
        assertThat(noon.lowerBound()).isEqualTo(NOON.plusNanos(1));
        assertThat(noon.upperBound()).isEqualTo(LocalTime.MAX);
    }

    @Test
    public void beforeNoon() {
        final LocalTimeRange noon = LocalTimeRange.beforeNoon();
        assertThat(noon.lowerBound()).isEqualTo(MIN);
        assertThat(noon.upperBound()).isEqualTo(NOON.minusNanos(1));
    }

    @Test
    public void beforeTime() {
        final LocalTime time = RDG.localTime().next();
        final LocalTimeRange before = LocalTimeRange.before(time);
        assertThat(before.lowerBound()).isEqualTo(MIN);
        assertThat(before.upperBound()).isEqualTo(time.minusNanos(1));
    }

    @Test
    public void afterTime() {
        final LocalTime time = RDG.localTime().next();
        final LocalTimeRange before = LocalTimeRange.after(time);
        assertThat(before.lowerBound()).isEqualTo(time.plusNanos(1));
        assertThat(before.upperBound()).isEqualTo(MAX);
    }

    @Test
    public void midnight() {
        final LocalTimeRange noon = LocalTimeRange.midnight();
        assertThat(noon.lowerBound()).isEqualTo(MIN);
        assertThat(noon.upperBound()).isEqualTo(MIN);
    }

    @Test
    public void startOfDay() {
        final LocalTimeRange noon = LocalTimeRange.startOfDay();
        assertThat(noon.lowerBound()).isEqualTo(MIN);
        assertThat(noon.upperBound()).isEqualTo(MIN);
    }

    @Test
    public void endOfDay() {
        final LocalTimeRange noon = LocalTimeRange.endOfDay();
        assertThat(noon.lowerBound()).isEqualTo(MAX);
        assertThat(noon.upperBound()).isEqualTo(MAX);
    }

    @Test
    public void betweenHoursOfTheDay() {
        final LocalTimeRange noon = between(3, 7);
        assertThat(noon.lowerBound()).isEqualTo(LocalTime.of(3, 0, 0));
        assertThat(noon.upperBound()).isEqualTo(LocalTime.of(7, 0, 0));
    }

    @Test
    public void betweenHoursUpperBoundShouldBeEqualToOrGreaterThanTheLowerBound() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("lower bound should be less-than-or-equal-to the upper bound");

        between(7, 3);
    }

    @Test
    public void cannotBeBeforeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range cannot be before null");

        before(null);
    }

    @Test
    public void cannotBeBeforeMinimumTime() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range cannot be before the minimum time");

        before(LocalTime.MIN);
    }

    @Test
    public void cannotBeAfterNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range cannot be after null");

        after(null);
    }

    @Test
    public void cannotBeAfterMaximumTime() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range cannot be after the maximum time");

        after(LocalTime.MAX);
    }
}