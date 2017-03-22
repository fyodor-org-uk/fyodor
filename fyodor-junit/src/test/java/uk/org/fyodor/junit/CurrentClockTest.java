package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.Clock;
import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;
import static uk.org.fyodor.generators.RDG.*;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.junit.TimeFactory.Clocks.utcClockOf;
import static uk.org.fyodor.junit.TimeFactory.Instants.utcInstantOf;

public final class CurrentClockTest {

    private static final Reporter<Clock> reporter = reporter();

    private final TestRunner<Clock> testRunner = new TestRunner<>(
            testStarted(reporter, Timekeeper::currentClock),
            testFailed(reporter, (failure) -> Timekeeper.currentClock()),
            testFinished(reporter, Timekeeper::currentClock));

    @Test
    public void noAnnotationsWithDefaultRule() {
        final Clock initialClock = utcClockOf(instant().next());

        Timekeeper.from(initialClock);

        testRunner.scheduleTest(NoAnnotationsWithDefaultRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(initialClock)
                .whenTestHasFinished(initialClock);

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(initialClock)
                .whenTestHasFinished(initialClock);
    }

    @Test
    public void clockConfiguredWithRule() {
        final Clock initialClock = utcClockOf(localDate().next().atTime(localTime().next()).toInstant(UTC));

        Timekeeper.from(initialClock);

        testRunner.scheduleTest(RuleConfiguredWithClock.class).run();

        assertThat(reporter.reportFor(RuleConfiguredWithClock.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(utcClockOf(utcInstantOf(1999, 12, 31, 23, 59, 59)))
                .whenTestHasFinished(initialClock);
    }

    @Test
    public void annotationsOverrideRuleConfiguredWithClock() {
        final Clock initialClock = utcClockOf(localDate().next().atTime(localTime().next()).toInstant(UTC));

        Timekeeper.from(initialClock);

        testRunner.scheduleTest(AnnotatedAndRuleConfiguredWithClock.class).run();

        assertThat(reporter.reportFor(AnnotatedAndRuleConfiguredWithClock.class, "annotatedClass"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(utcClockOf(utcInstantOf(1999, 12, 31, 23, 59, 59)))
                .whenTestHasFinished(initialClock);

        assertThat(reporter.reportFor(AnnotatedAndRuleConfiguredWithClock.class, "annotatedMethod"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(utcClockOf(utcInstantOf(2010, 1, 1, 12, 0, 0)))
                .whenTestHasFinished(initialClock);
    }

    @Test
    public void clockConfiguredWithDateAndTimeAnnotations() {
        final Clock initialClock = utcClockOf(instant().next());

        Timekeeper.from(initialClock);

        testRunner.scheduleTest(TestClassWithAnnotations.class).run();

        assertThat(reporter.reportFor(TestClassWithAnnotations.class, "methodWithDate"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(utcClockOf(utcInstantOf(2015, 11, 24, 11, 59, 59)))
                .whenTestHasFinished(initialClock);

        assertThat(reporter.reportFor(TestClassWithAnnotations.class, "methodWithTime"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(utcClockOf(utcInstantOf(2014, 5, 1, 12, 0, 0)))
                .whenTestHasFinished(initialClock);

        assertThat(reporter.reportFor(TestClassWithAnnotations.class, "classAnnotationsOnly"))
                .didNotFail()
                .beforeTestStarts(initialClock)
                .duringTest(utcClockOf(utcInstantOf(2014, 5, 1, 11, 59, 59)))
                .whenTestHasFinished(initialClock);
    }

    public static final class NoAnnotationsWithDefaultRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentClock());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentClock());
        }
    }

    @CurrentDate("2014-05-01")
    @CurrentTime("11:59:59")
    public static final class TestClassWithAnnotations {
        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2015-11-24")
        public void methodWithDate() {
            reporter.objectDuringTest(TestClassWithAnnotations.class, testName.getMethodName(), rule.currentClock());
        }

        @Test
        @CurrentTime("12:00:00")
        public void methodWithTime() {
            reporter.objectDuringTest(TestClassWithAnnotations.class, testName.getMethodName(), rule.currentClock());
        }

        @Test
        public void classAnnotationsOnly() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentClock());
        }
    }

    public static final class RuleConfiguredWithClock {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.from(
                Clock.fixed(LocalDate.of(1999, 12, 31).atTime(23, 59, 59).toInstant(UTC), UTC));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentClock());
        }
    }

    @CurrentDate("1999-12-31")
    @CurrentTime("23:59:59")
    public static final class AnnotatedAndRuleConfiguredWithClock {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.from(
                Clock.fixed(LocalDate.of(2003, 6, 15).atTime(0, 0, 0).toInstant(UTC), UTC));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void annotatedClass() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentClock());
        }

        @Test
        @CurrentDate("2010-01-01")
        @CurrentTime("12:00:00")
        public void annotatedMethod() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentClock());
        }
    }
}
