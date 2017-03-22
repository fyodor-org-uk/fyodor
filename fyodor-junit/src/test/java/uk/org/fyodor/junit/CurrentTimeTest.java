package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static java.time.LocalTime.of;
import static org.junit.Assert.assertTrue;
import static uk.org.fyodor.generators.RDG.localTime;
import static uk.org.fyodor.generators.time.LocalTimeRange.now;
import static uk.org.fyodor.junit.FyodorTimekeeperRule.withCurrentTime;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.range.Range.fixed;

@SuppressWarnings("ConstantConditions")
public final class CurrentTimeTest {

    private static final Reporter<LocalTime> reporter = reporter();

    private final TestRunner<LocalTime> testRunner = new TestRunner<>(
            testStarted(reporter, Timekeeper::currentTime),
            testFailed(reporter, (failure) -> Timekeeper.currentTime()),
            testFinished(reporter, Timekeeper::currentTime));

    @Test
    public void usesCurrentTimeWhenTestIsNotAnnotated() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner.scheduleTest(TestClassWithNoAnnotations.class).run();

        assertThat(reporter.reportFor(TestClassWithNoAnnotations.class, "first"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(now)
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithNoAnnotations.class, "second"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(now)
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithNoAnnotations.class, "third"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(now)
                .whenTestHasFinished(now);
    }

    @Test
    public void ruleConfiguredWithCurrentTime() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner.scheduleTest(RuleConfiguredWithCurrentTime.class).run();

        assertThat(reporter.reportFor(RuleConfiguredWithCurrentTime.class, "first"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(10, 30, 45))
                .whenTestHasFinished(now);
    }

    @Test
    public void resetsTimeAfterEachTestMethod() {
        final LocalTime now = localTime().next();
        final LocalTime anHourAgo = now.minusHours(1);

        Timekeeper.from(clockTodayAt(anHourAgo));
        Timekeeper.from(clockTodayAt(now));

        testRunner.scheduleTest(TestClassWithTimeSpecificationOnClass.class).run();

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClass.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(23, 59, 59))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClass.class, "redTest"))
                .beforeTestStarts(now)
                .whenFailed(now)
                .whenTestHasFinished(now);
    }

    @Test
    public void timeMayBeConfiguredPerTestMethod() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner
                .scheduleTest(TestClassWithDatedMethods.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithDatedMethods.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(9, 0, 0))
                .whenTestHasFinished(now);
    }

    @Test
    public void timeMayBeConfiguredPerClass() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner
                .scheduleTest(TestClassWithTimeSpecificationOnClass.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClass.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(23, 59, 59))
                .whenTestHasFinished(now);
    }

    @Test
    public void methodLevelTimeAnnotationsTakePriorityOverClassLevelAnnotations() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner
                .scheduleTest(TestClassWithTimeSpecificationOnClassAndMethod.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClassAndMethod.class, "firstTestMethodUsingClassAnnotation"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(14, 0, 0))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClassAndMethod.class, "secondTestMethodUsingClassAnnotation"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(14, 0, 0))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClassAndMethod.class, "firstTestMethodUsingMethodAnnotation"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(15, 1, 0))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithTimeSpecificationOnClassAndMethod.class, "secondTestMethodUsingMethodAnnotation"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(of(16, 2, 0))
                .whenTestHasFinished(now);
    }

    @Test
    public void testFailsWhenTimeStringCannotBeParsed() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner.scheduleTest(TestWithBadTimeString.class).run();

        assertThat(reporter.reportFor(TestWithBadTimeString.class, "testWithBadTimeString"))
                .beforeTestStarts(now)
                .whenTestHasFinished(now)
                .whenFailed(now)
                .failedBecauseOf(DateTimeException.class);
    }

    @Test
    public void testCanUseCurrentTimeFromRule() {
        final LocalTime now = localTime().next();
        Timekeeper.from(clockTodayAt(now));

        testRunner.scheduleTest(TestClassUsingCurrentTimeFromRule.class).run();

        assertThat(reporter.reportFor(TestClassUsingCurrentTimeFromRule.class, "currentTimeFromRule"))
                .duringTest(of(13, 35, 21));
    }

    private static Clock clockTodayAt(final LocalTime time) {
        final Instant utcInstant = LocalDate.now().atTime(time).toInstant(ZoneOffset.UTC);
        return Clock.fixed(utcInstant, ZoneOffset.UTC);
    }

    public static final class TestClassUsingCurrentTimeFromRule {
        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentTime("13:35:21")
        public void currentTimeFromRule() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentTime());
        }
    }

    public static final class TestClassWithDatedMethods {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentTime("09:00:00")
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        @CurrentTime("10:00:00")
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
            assertTrue(false);
        }
    }

    @CurrentTime("23:59:59")
    public static final class TestClassWithTimeSpecificationOnClass {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
            assertTrue(false);
        }
    }

    @CurrentTime("14:00:00")
    public static final class TestClassWithTimeSpecificationOnClassAndMethod {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void firstTestMethodUsingClassAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        @CurrentTime("15:01:00")
        public void firstTestMethodUsingMethodAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        public void secondTestMethodUsingClassAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        @CurrentTime("16:02:00")
        public void secondTestMethodUsingMethodAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }

        @Test
        public void failingTestMethodUsingClassAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
            assertTrue(false);
        }

        @Test
        @CurrentTime("17:03:00")
        public void failingTestMethodUsingMethodAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
            assertTrue(false);
        }
    }

    public static final class TestWithBadTimeString {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Test
        @CurrentTime("this-is-not-a-time")
        public void testWithBadTimeString() {
        }
    }

    public static final class TestClassWithNoAnnotations {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(TestClassWithNoAnnotations.class, testName.getMethodName(), localTime(now()).next());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(TestClassWithNoAnnotations.class, testName.getMethodName(), localTime(now()).next());
        }

        @Test
        public void third() {
            reporter.objectDuringTest(TestClassWithNoAnnotations.class, testName.getMethodName(), localTime(now()).next());
        }
    }

    public static final class RuleConfiguredWithCurrentTime {

        @Rule
        public final FyodorTimekeeperRule rule = withCurrentTime(localTime(fixed(of(10, 30, 45))));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localTime(now()).next());
        }
    }
}
