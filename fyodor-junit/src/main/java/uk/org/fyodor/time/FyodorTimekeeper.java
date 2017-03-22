package uk.org.fyodor.time;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static java.time.ZoneOffset.UTC;

public final class FyodorTimekeeper extends TestWatcher {

    private final ThreadLocal<Boolean> timekeeperConfigured = ThreadLocal.withInitial(() -> false);
    private final Generator<LocalDate> currentDate;
    private final Generator<LocalTime> currentTime;

    private FyodorTimekeeper(final Generator<LocalDate> currentDate, final Generator<LocalTime> currentTime) {
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    @Override
    protected void starting(final Description description) {
        Timekeeper.from(clockOf(currentDateTime(description)));
        timekeeperConfigured.set(true);
    }

    @Override
    protected void finished(final Description description) {
        if (timekeeperConfigured.get()) {
            Timekeeper.rollback();
        }
    }

    public Instant currentInstant() {
        return Timekeeper.currentInstant();
    }

    public Clock currentClock() {
        return Timekeeper.currentClock();
    }

    public LocalDate currentDate() {
        return Timekeeper.currentDate();
    }

    public LocalTime currentTime() {
        return Timekeeper.currentTime();
    }

    private LocalDateTime currentDateTime(final Description description) {
        return currentDateFor(description).atTime(currentTimeFor(description));
    }

    private LocalDate currentDateFor(final Description description) {
        final CurrentDate methodAnnotation = description.getAnnotation(CurrentDate.class);
        if (methodAnnotation != null) {
            final String dateString = methodAnnotation.value();
            return LocalDate.parse(dateString);
        }

        final CurrentDate classAnnotation = description.getTestClass().getAnnotation(CurrentDate.class);
        if (classAnnotation != null) {
            final String dateString = classAnnotation.value();
            return LocalDate.parse(dateString);
        }

        return currentDate.next();
    }

    private LocalTime currentTimeFor(final Description description) {
        final CurrentTime methodAnnotation = description.getAnnotation(CurrentTime.class);
        if (methodAnnotation != null) {
            final String timeString = methodAnnotation.value();
            return LocalTime.parse(timeString);
        }

        final CurrentTime classAnnotation = description.getTestClass().getAnnotation(CurrentTime.class);
        if (classAnnotation != null) {
            final String timeString = classAnnotation.value();
            return LocalTime.parse(timeString);
        }

        return currentTime.next();
    }

    private static Clock clockOf(final LocalDateTime dateTime) {
        return Clock.fixed(dateTime.toInstant(UTC), UTC);
    }

    public static FyodorTimekeeper timekeeper() {
        return new FyodorTimekeeper(Timekeeper::currentDate, Timekeeper::currentTime);
    }

    public static FyodorTimekeeper from(final Clock clock) {
        return new FyodorTimekeeper(
                () -> clock.instant().atZone(UTC).toLocalDate(),
                () -> clock.instant().atZone(UTC).toLocalTime());
    }

    public static FyodorTimekeeper withCurrentDate(final LocalDate currentDate) {
        return withCurrentDate(() -> currentDate);
    }

    public static FyodorTimekeeper withCurrentDate(final Generator<LocalDate> currentDate) {
        return new FyodorTimekeeper(currentDate, Timekeeper::currentTime);
    }

    public static FyodorTimekeeper withCurrentTime(final LocalTime currentTime) {
        return withCurrentTime(() -> currentTime);
    }

    public static FyodorTimekeeper withCurrentTime(final Generator<LocalTime> currentTime) {
        return new FyodorTimekeeper(Timekeeper::currentDate, currentTime);
    }

    public static FyodorTimekeeper withCurrentDateAndTime(final LocalDateTime currentDateTime) {
        return withCurrentDateAndTime(() -> currentDateTime);
    }

    public static FyodorTimekeeper withCurrentDateAndTime(final Generator<LocalDateTime> currentDateTime) {
        return new FyodorTimekeeper(
                () -> currentDateTime.next().toLocalDate(),
                () -> currentDateTime.next().toLocalTime());
    }
}
