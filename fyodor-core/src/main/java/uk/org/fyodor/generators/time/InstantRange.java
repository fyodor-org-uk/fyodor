package uk.org.fyodor.generators.time;

import uk.org.fyodor.range.Range;

import java.time.Instant;
import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;

public final class InstantRange extends Range<Instant> {

    private InstantRange(final Instant lowerBound, final Instant upperBound) {
        super(lowerBound, upperBound);
    }

    public static InstantRange all() {
        return new InstantRange(Instant.MIN, Instant.MAX);
    }

    public static InstantRange now() {
        final Instant now = Timekeeper.currentInstant();
        return new InstantRange(now, now);
    }

    public static InstantRange today() {
        return atDate(Timekeeper.currentDate());
    }

    public static InstantRange atDate(final LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("range at date cannot be null");
        }

        final Instant startOfDay = date.atTime(0, 0, 0, 0).toInstant(UTC);
        final Instant endOfDay = date.atTime(23, 59, 59, 0).toInstant(UTC);
        return new InstantRange(startOfDay, endOfDay);
    }

    public static InstantRange after(final Instant after) {
        if (after == null) {
            throw new IllegalArgumentException("range after instant cannot be null");
        }

        return new InstantRange(after.plusMillis(1), Instant.MAX);
    }

    public static InstantRange before(final Instant before) {
        if (before == null) {
            throw new IllegalArgumentException("range before instant cannot be null");
        }

        return new InstantRange(Instant.MIN, before.minusMillis(1));
    }

    public static InstantRange inTheFuture() {
        final Instant now = Timekeeper.currentInstant();

        if (now.equals(Instant.MAX)) {
            throw new IllegalArgumentException("range cannot be in the future because today is the max instant");
        }

        return new InstantRange(now.plusSeconds(1), Instant.MAX);
    }

    public static InstantRange inThePast() {
        final Instant now = Timekeeper.currentInstant();

        if (now.equals(Instant.MIN)) {
            throw new IllegalArgumentException("range cannot be in the past because today is the min instant");
        }

        return new InstantRange(Instant.MIN, now.minusSeconds(1));
    }
}
