package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtDate;
import uk.org.fyodor.testapi.AtTime;
import uk.org.fyodor.testapi.AtZone;

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

        final LocalDate currentDate = fyodorTest.atDateAnnotation()
                .map(date -> LocalDate.parse(date.value()))
                .orElseGet(defaultDateTimeAndZone::toLocalDate);

        final LocalTime currentTime = fyodorTest.atTimeAnnotation()
                .map(time -> LocalTime.parse(time.value()))
                .orElseGet(defaultDateTimeAndZone::toLocalTime);

        final ZoneId currentZone = fyodorTest.atZoneAnnotation()
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

    private void configureTimekeeper(final Clock clock) {
        Timekeeper.from(clock);
        timekeeperConfigured.set(true);
    }

    private static Clock fixedClockOf(final ZonedDateTime zonedDateTime) {
        return fixed(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    private static final class FyodorTest {

        private final Description description;

        private FyodorTest(final Description description) {
            this.description = description;
        }

        Optional<AtDate> atDateAnnotation() {
            return findAnnotation(AtDate.class);
        }

        Optional<AtTime> atTimeAnnotation() {
            return findAnnotation(AtTime.class);
        }

        Optional<AtZone> atZoneAnnotation() {
            return findAnnotation(AtZone.class);
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
