package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtDate;
import uk.org.fyodor.testapi.AtTime;
import uk.org.fyodor.testapi.AtZone;

import java.time.*;

import static java.time.Clock.fixed;
import static java.time.ZoneOffset.UTC;
import static uk.org.fyodor.generators.RDG.localDate;
import static uk.org.fyodor.generators.RDG.localTime;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.FyodorTestRule.from;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.junit.TimeFactory.Clocks.utcClockOf;
import static uk.org.fyodor.junit.TimeFactory.Instants.utcInstantOf;

public final class ZonedDateTimeTest {

    private static final Reporter<ZonedDateTime> reporter = reporter();

    private final TestRunner<ZonedDateTime> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().zonedDateTime()),
            testFailed(reporter, (failure) -> current().zonedDateTime()),
            testFinished(reporter, () -> current().zonedDateTime()));

    @Test
    public void noAnnotationsWithDefaultRule() {
        final ZoneId zone = ZoneId.systemDefault();
        final Clock initialClock = fixed(Instant.now(), zone);
        final ZonedDateTime initialZonedDateTime = initialClock.instant().atZone(zone);
        Timekeeper.from(initialClock);

        testRunner.scheduleTest(NoAnnotationsWithDefaultRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZonedDateTime)
                .duringTest(initialZonedDateTime)
                .whenTestHasFinished(initialZonedDateTime);

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialZonedDateTime)
                .duringTest(initialZonedDateTime)
                .whenTestHasFinished(initialZonedDateTime);
    }

    @Test
    public void dateTimeAndZoneConfiguredWithRule() {
        final Clock initialClock = utcClockOf(utcInstantOf(localDate().next().atTime(localTime().next())));
        final ZonedDateTime initialZonedDateTime = initialClock.instant().atZone(UTC);
        Timekeeper.from(initialClock);

        testRunner.scheduleTest(DateTimeAndZoneConfiguredWithRule.class).run();

        assertThat(reporter.reportFor(DateTimeAndZoneConfiguredWithRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZonedDateTime)
                .duringTest(ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneId.of("America/St_Johns")))
                .whenTestHasFinished(initialZonedDateTime);
    }

    @Test
    public void zoneConfiguredWithRuleAndDateTimeConfiguredWithAnnotations() {
        final Clock initialClock = utcClockOf(utcInstantOf(localDate().next().atTime(localTime().next())));
        final ZonedDateTime initialZonedDateTime = initialClock.instant().atZone(UTC);
        Timekeeper.from(initialClock);

        testRunner.scheduleTest(ZoneConfiguredWithRuleDateTimeConfiguredWithAnnotations.class).run();

        assertThat(reporter.reportFor(ZoneConfiguredWithRuleDateTimeConfiguredWithAnnotations.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZonedDateTime)
                .duringTest(ZonedDateTime.of(2017, 4, 3, 12, 31, 56, 0, ZoneId.of("Africa/Addis_Ababa")))
                .whenTestHasFinished(initialZonedDateTime);
    }

    @Test
    public void zoneConfiguredWithAnnotation() {
        final Clock initialClock = utcClockOf(utcInstantOf(localDate().next().atTime(localTime().next())));
        final ZonedDateTime initialZonedDateTime = initialClock.instant().atZone(UTC);
        Timekeeper.from(initialClock);

        testRunner.scheduleTest(DateTimeAndZoneConfiguredWithAnnotations.class).run();

        assertThat(reporter.reportFor(DateTimeAndZoneConfiguredWithAnnotations.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZonedDateTime)
                .duringTest(ZonedDateTime.of(2016, 3, 2, 11, 30, 55, 0, ZoneId.of("Pacific/Apia")))
                .whenTestHasFinished(initialZonedDateTime);
    }

    public static final class NoAnnotationsWithDefaultRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zonedDateTime());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zonedDateTime());
        }
    }

    public static final class DateTimeAndZoneConfiguredWithRule {

        @Rule
        public final FyodorTestRule rule = from(fixed(ZonedDateTime.of(
                LocalDate.of(1999, 12, 31),
                LocalTime.of(23, 59, 59),
                ZoneId.of("America/St_Johns")).toInstant(),
                ZoneId.of("America/St_Johns")));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zonedDateTime());
        }
    }

    public static final class ZoneConfiguredWithRuleDateTimeConfiguredWithAnnotations {

        @Rule
        public final FyodorTestRule rule = from(fixed(
                utcInstantOf(1999, 12, 31, 23, 59, 59),
                ZoneId.of("Africa/Addis_Ababa")));

        @Rule
        public final TestName testName = new TestName();

        @Test
        @AtDate("2017-04-03")
        @AtTime("12:31:56")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zonedDateTime());
        }
    }

    public static final class DateTimeAndZoneConfiguredWithAnnotations {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @AtDate("2016-03-02")
        @AtTime("11:30:55")
        @AtZone("Pacific/Apia")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zonedDateTime());
        }
    }
}
