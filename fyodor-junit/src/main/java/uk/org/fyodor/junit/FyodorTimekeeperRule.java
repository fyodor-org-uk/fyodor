package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static java.time.ZoneOffset.UTC;

public final class FyodorTimekeeperRule extends TestWatcher {

    private final ThreadLocal<Boolean> timekeeperConfigured = ThreadLocal.withInitial(() -> false);
    private final Generator<LocalDate> currentDate;
    private final Generator<LocalTime> currentTime;

    private FyodorTimekeeperRule(final Generator<LocalDate> currentDate, final Generator<LocalTime> currentTime) {
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

    public static FyodorTimekeeperRule timekeeper() {
        return new FyodorTimekeeperRule(Timekeeper::currentDate, Timekeeper::currentTime);
    }

    public static FyodorTimekeeperRule from(final Clock clock) {
        return new FyodorTimekeeperRule(
                () -> clock.instant().atZone(UTC).toLocalDate(),
                () -> clock.instant().atZone(UTC).toLocalTime());
    }

    public static FyodorTimekeeperRule withCurrentDate(final LocalDate currentDate) {
        return withCurrentDate(() -> currentDate);
    }

    public static FyodorTimekeeperRule withCurrentDate(final Generator<LocalDate> currentDate) {
        return new FyodorTimekeeperRule(currentDate, Timekeeper::currentTime);
    }

    public static FyodorTimekeeperRule withCurrentTime(final LocalTime currentTime) {
        return withCurrentTime(() -> currentTime);
    }

    public static FyodorTimekeeperRule withCurrentTime(final Generator<LocalTime> currentTime) {
        return new FyodorTimekeeperRule(Timekeeper::currentDate, currentTime);
    }

    public static FyodorTimekeeperRule withCurrentDateAndTime(final LocalDateTime currentDateTime) {
        return withCurrentDateAndTime(() -> currentDateTime);
    }

    public static FyodorTimekeeperRule withCurrentDateAndTime(final Generator<LocalDateTime> currentDateTime) {
        return new FyodorTimekeeperRule(
                () -> currentDateTime.next().toLocalDate(),
                () -> currentDateTime.next().toLocalTime());
    }
}
