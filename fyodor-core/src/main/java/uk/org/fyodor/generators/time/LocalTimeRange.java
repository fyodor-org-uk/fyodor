package uk.org.fyodor.generators.time;

import uk.org.fyodor.range.Range;

import java.time.LocalTime;

import static java.time.LocalTime.*;

public final class LocalTimeRange extends Range<LocalTime> {

    private static final LocalTime MINIMUM = LocalTime.MIN;
    private static final LocalTime MAXIMUM = LocalTime.MAX;

    private LocalTimeRange(final LocalTime lowerBound, final LocalTime upperBound) {
        super(lowerBound, upperBound);
    }

    public static LocalTimeRange between(final int startHour, final int endHour) {
        return new LocalTimeRange(of(startHour, 0, 0), of(endHour, 0, 0));
    }

    public static LocalTimeRange midnight() {
        return new LocalTimeRange(MIDNIGHT, MIDNIGHT);
    }

    public static LocalTimeRange endOfDay() {
        return new LocalTimeRange(MAXIMUM, MAXIMUM);
    }

    public static LocalTimeRange startOfDay() {
        return new LocalTimeRange(MINIMUM, MINIMUM);
    }

    public static LocalTimeRange noon() {
        return new LocalTimeRange(NOON, NOON);
    }

    public static LocalTimeRange beforeNoon() {
        return new LocalTimeRange(MINIMUM, NOON.minusNanos(1));
    }

    public static LocalTimeRange afterNoon() {
        return new LocalTimeRange(NOON.plusNanos(1), MAXIMUM);
    }

    public static LocalTimeRange now() {
        final LocalTime now = Timekeeper.currentTime();
        return new LocalTimeRange(now, now);
    }

    public static LocalTimeRange all() {
        return new LocalTimeRange(MINIMUM, MAXIMUM);
    }

    public static LocalTimeRange after(final LocalTime after) {
        if (after == null) {
            throw new IllegalArgumentException("range cannot be after null");
        }
        if (after.equals(LocalTime.MAX)) {
            throw new IllegalArgumentException("range cannot be after the maximum time");
        }
        return new LocalTimeRange(after.plusNanos(1), MAXIMUM);
    }

    public static LocalTimeRange before(final LocalTime before) {
        if (before == null) {
            throw new IllegalArgumentException("range cannot be before null");
        }
        if (before.equals(LocalTime.MIN)) {
            throw new IllegalArgumentException("range cannot be before the minimum time");
        }
        return new LocalTimeRange(MINIMUM, before.minusNanos(1));
    }
}
