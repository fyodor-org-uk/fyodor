package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.CurrentZone;
import uk.org.fyodor.generators.time.Timekeeper;

import java.lang.annotation.Annotation;
import java.time.*;
import java.util.Optional;

import static java.time.Clock.fixed;
import static java.util.Optional.empty;

final class FyodorTimekeeperRule extends TestWatcher {

    private final ThreadLocal<Boolean> timekeeperConfigured = ThreadLocal.withInitial(() -> false);

    private final Generator<ZonedDateTime> currentDateTime;

    FyodorTimekeeperRule(final Generator<ZonedDateTime> currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    @Override
    public void starting(final Description description) {
        final FyodorTest fyodorTest = new FyodorTest(description);

        final ZonedDateTime defaultDateTimeAndZone = currentDateTime.next();

        final LocalDate currentDate = fyodorTest.currentDateAnnotation()
                .map(date -> LocalDate.parse(date.value()))
                .orElseGet(defaultDateTimeAndZone::toLocalDate);

        final LocalTime currentTime = fyodorTest.currentTimeAnnotation()
                .map(time -> LocalTime.parse(time.value()))
                .orElseGet(defaultDateTimeAndZone::toLocalTime);

        final ZoneId currentZone = fyodorTest.currentZoneAnnotation()
                .map(zone -> ZoneId.of(zone.value()))
                .orElseGet(defaultDateTimeAndZone::getZone);

        final Clock currentClock = fixedClockOf(currentDate.atTime(currentTime).atZone(currentZone));

        configureTimekeeper(currentClock);
    }

    @Override
    public void finished(final Description description) {
        if (timekeeperConfigured.get()) {
            Timekeeper.rollback();
        }
    }

    private void configureTimekeeper(final Clock currentClock) {
        Timekeeper.from(currentClock);
        timekeeperConfigured.set(true);
    }

    private static Clock fixedClockOf(final ZonedDateTime currentDateTimeAndZone) {
        return fixed(currentDateTimeAndZone.toInstant(), currentDateTimeAndZone.getZone());
    }

    private static final class FyodorTest {

        private final Description description;

        private FyodorTest(final Description description) {
            this.description = description;
        }

        Optional<CurrentDate> currentDateAnnotation() {
            return findAnnotation(CurrentDate.class);
        }

        Optional<CurrentTime> currentTimeAnnotation() {
            return findAnnotation(CurrentTime.class);
        }

        Optional<CurrentZone> currentZoneAnnotation() {
            return findAnnotation(CurrentZone.class);
        }

        private <A extends Annotation> Optional<A> findAnnotation(final Class<A> annotation) {
            final A methodAnnotation = description.getAnnotation(annotation);
            if (methodAnnotation != null) {
                return Optional.of(methodAnnotation);
            }

            final A classAnnotation = description.getTestClass().getAnnotation(annotation);
            if (classAnnotation != null) {
                return Optional.of(classAnnotation);
            }

            return empty();
        }

    }
}
