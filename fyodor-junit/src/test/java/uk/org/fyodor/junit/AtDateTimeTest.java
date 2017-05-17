package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtDate;
import uk.org.fyodor.testapi.AtTime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertTrue;
import static uk.org.fyodor.generators.RDG.instant;
import static uk.org.fyodor.generators.RDG.localTime;
import static uk.org.fyodor.generators.time.InstantRange.now;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.FyodorTestRule.*;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.junit.TimeFactory.Clocks.utcClockOf;
import static uk.org.fyodor.junit.TimeFactory.Instants.utcInstantOf;

@SuppressWarnings("ConstantConditions")
public final class AtDateTimeTest {

    private static final Reporter<Instant> reporter = reporter();

    private final TestRunner<Instant> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().instant()),
            testFailed(reporter, (failure) -> current().instant()),
            testFinished(reporter, () -> current().instant()));

    @Test
    public void noAnnotationsWithDefaultRule() {
        final Instant initialDateTime = instant().next();
        Timekeeper.from(utcClockOf(initialDateTime));

        testRunner
                .scheduleTest(NoAnnotationsWithDefaultRule.class)
                .run();

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(initialDateTime)
                .whenTestHasFinished(initialDateTime);

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(initialDateTime)
                .whenTestHasFinished(initialDateTime);
    }

    @Test
    public void timeConfiguredInRule() {
        final LocalDate initialDate = RDG.localDate().next();
        final Instant initialDateTime = utcInstantOf(initialDate.atTime(localTime().next()));
        Timekeeper.from(utcClockOf(initialDateTime));

        testRunner.scheduleTest(RuleConfiguredAtTime.class).run();

        final Report<Instant> report = reporter.reportFor(RuleConfiguredAtTime.class, "first");

        assertThat(report)
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(initialDate.atTime(10, 30, 45).toInstant(UTC))
                .whenTestHasFinished(initialDateTime);
    }

    @Test
    public void dateConfiguredInRule() {
        final LocalDate initialDate = RDG.localDate().next();
        final LocalTime initialTime = RDG.localTime().next();
        final Instant initialDateTime = utcInstantOf(initialDate.atTime(initialTime));
        Timekeeper.from(utcClockOf(initialDateTime));

        testRunner.scheduleTest(RuleConfiguredWithDate.class).run();

        final Report<Instant> report = reporter.reportFor(RuleConfiguredWithDate.class, "first");

        assertThat(report)
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(LocalDate.of(1999, 12, 31).atTime(initialTime).toInstant(UTC))
                .whenTestHasFinished(initialDateTime);
    }

    @Test
    public void dateAndTimeConfiguredInRule() {
        final Instant initialDateTime = RDG.instant().next();
        Timekeeper.from(utcClockOf(initialDateTime));

        testRunner.scheduleTest(RuleConfiguredWithDateAndTime.class).run();

        final Report<Instant> report = reporter.reportFor(RuleConfiguredWithDateAndTime.class, "first");

        assertThat(report)
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(LocalDateTime.of(1999, 12, 31, 23, 59, 59).toInstant(UTC))
                .whenTestHasFinished(initialDateTime);
    }


    @Test
    public void resetsBackToPreviousDateAndTimeAfterEachTestMethod() {
        final Instant now = RDG.instant().next();
        Timekeeper.from(utcClockOf(now));

        testRunner.scheduleTest(DateAndTimeConfiguredInAnnotationsAndRule.class).run();

        assertThat(reporter.reportFor(DateAndTimeConfiguredInAnnotationsAndRule.class, "annotatedClass"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(LocalDateTime.of(2010, 1, 1, 12, 0, 0).toInstant(UTC))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(DateAndTimeConfiguredInAnnotationsAndRule.class, "annotatedMethod"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(LocalDateTime.of(2015, 6, 15, 17, 1, 30).toInstant(UTC))
                .whenTestHasFinished(now);
    }

    public static final class NoAnnotationsWithDefaultRule {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    @AtDate("2011-04-13")
    @AtTime("16:02:31")
    public static final class TestClassWithDateTimeSpecificationOnClass {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
            assertTrue(false);
        }
    }


    public static final class RuleConfiguredAtTime {

        @Rule
        public final FyodorTestRule rule = withCurrentTime(LocalTime.of(10, 30, 45));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    public static final class RuleConfiguredWithDate {

        @Rule
        public final FyodorTestRule rule = withCurrentDate(LocalDate.of(1999, 12, 31));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    public static final class RuleConfiguredWithDateAndTime {

        @Rule
        public final FyodorTestRule rule = withCurrentDateAndTime(LocalDateTime.of(1999, 12, 31, 23, 59, 59));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    @AtDate("2010-01-01")
    @AtTime("12:00:00")
    public static final class DateAndTimeConfiguredInAnnotationsAndRule {

        @Rule
        public final FyodorTestRule rule = withCurrentDateAndTime(LocalDateTime.of(1999, 12, 31, 23, 59, 59));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void annotatedClass() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @AtDate("2015-06-15")
        @AtTime("17:01:30")
        public void annotatedMethod() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }
}
