package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.ZoneOffset.UTC;

final class FyodorTimekeeperRule extends TestWatcher {

    private final ThreadLocal<Boolean> timekeeperConfigured = ThreadLocal.withInitial(() -> false);

    private final CurrentDateProvider currentDateProvider;
    private final CurrentTimeProvider currentTimeProvider;

    FyodorTimekeeperRule(final Generator<LocalDate> currentDate, final Generator<LocalTime> currentTime) {
        this.currentDateProvider = new CurrentDateProvider(currentDate);
        this.currentTimeProvider = new CurrentTimeProvider(currentTime);
    }

    @Override
    public void starting(final Description description) {
        final LocalDate currentDate = currentDateProvider.currentDateFor(description);
        final LocalTime currentTime = currentTimeProvider.currentTimeFor(description);
        Timekeeper.from(utcClockOf(currentDate.atTime(currentTime)));
        timekeeperConfigured.set(true);
    }

    @Override
    public void finished(final Description description) {
        if (timekeeperConfigured.get()) {
            Timekeeper.rollback();
        }
    }

    private static Clock utcClockOf(final LocalDateTime dateTime) {
        return Clock.fixed(dateTime.toInstant(UTC), UTC);
    }

    private static final class CurrentDateProvider {

        private final Generator<LocalDate> currentDate;

        private CurrentDateProvider(final Generator<LocalDate> currentDate) {
            this.currentDate = currentDate;
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
    }

    private static final class CurrentTimeProvider {

        private final Generator<LocalTime> currentTime;

        private CurrentTimeProvider(final Generator<LocalTime> currentTime) {
            this.currentTime = currentTime;
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
    }
}
