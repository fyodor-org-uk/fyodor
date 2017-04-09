package uk.org.fyodor.generators.time;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class InstantRangeTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void atDateCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range at date cannot be null");

        //noinspection ConstantConditions
        InstantRange.atDate(null);
    }

    @Test
    public void cannotBeAfterNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range after instant cannot be null");

        InstantRange.after(null);
    }

    @Test
    public void cannotBeBeforeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range before instant cannot be null");

        InstantRange.before(null);
    }

    @Test
    public void cannotBeInTheFutureWhenTodayIsMax() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range cannot be in the future because today is the max instant");

        Timekeeper.from(Clock.fixed(Instant.MAX, ZoneId.systemDefault()));

        InstantRange.inTheFuture();
    }

    @Test
    public void cannotBeInThePastWhenTodayIsMin() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("range cannot be in the past because today is the min instant");

        Timekeeper.from(Clock.fixed(Instant.MIN, ZoneId.systemDefault()));

        InstantRange.inThePast();
    }

}