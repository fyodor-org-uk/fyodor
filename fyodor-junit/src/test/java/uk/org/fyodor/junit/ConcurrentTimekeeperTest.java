package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.function.Consumer;

import static uk.org.fyodor.generators.RDG.instant;
import static uk.org.fyodor.generators.time.InstantRange.now;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.FyodorTestRule.fyodorTestRule;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.junit.TimeFactory.Instants.utcInstantOf;

public final class ConcurrentTimekeeperTest {

    private static final Reporter<Instant> reporter = reporter();

    private final TestRunner<Instant> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().instant()),
            testFailed(reporter, (failure) -> current().instant()),
            testFinished(reporter, () -> current().instant()));

    @Test
    public void testsRunInParallelCanHaveTheirOwnTimekeeperDateAndTimes() {
        final Instant first = utcInstantOf(2015, 1, 1);
        final Instant second = utcInstantOf(2016, 6, 15);
        final Instant third = utcInstantOf(2017, 12, 31);

        testRunner
                .scheduleTestWithObject(ClassLevelAnnotatedOnly.class, first, configureTimekeeperFromInstant())
                .scheduleTestWithObject(MethodLevelAnnotatedOnly.class, second, configureTimekeeperFromInstant())
                .scheduleTestWithObject(ClassLevelAndMethodLevelAnnotated.class, third, configureTimekeeperFromInstant())
                .runInParallel();

        assertThat(reporter.reportFor(ClassLevelAnnotatedOnly.class, "classLevelAnnotated"))
                .didNotFail()
                .beforeTestStarts(first)
                .duringTest(utcInstantOf(2050, 1, 1, 15, 35, 45))
                .whenTestHasFinished(first);

        assertThat(reporter.reportFor(MethodLevelAnnotatedOnly.class, "methodLevelAnnotated"))
                .didNotFail()
                .beforeTestStarts(second)
                .duringTest(utcInstantOf(2013, 7, 11, 12, 15, 25))
                .whenTestHasFinished(second);

        assertThat(reporter.reportFor(ClassLevelAndMethodLevelAnnotated.class, "classLevelAnnotated"))
                .didNotFail()
                .beforeTestStarts(third)
                .duringTest(utcInstantOf(2050, 1, 1, 15, 35, 45))
                .whenTestHasFinished(third);

        assertThat(reporter.reportFor(ClassLevelAndMethodLevelAnnotated.class, "methodLevelAnnotated"))
                .didNotFail()
                .beforeTestStarts(third)
                .duringTest(utcInstantOf(2013, 7, 11, 10, 55, 15))
                .whenTestHasFinished(third);
    }

    private static Consumer<Instant> configureTimekeeperFromInstant() {
        return instant -> Timekeeper.from(clockFrom(instant));
    }

    private static Clock clockFrom(final Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }

    @CurrentDate("2050-01-01")
    @CurrentTime("15:35:45")
    public static final class ClassLevelAnnotatedOnly {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void classLevelAnnotated() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().instant());
        }
    }

    public static final class MethodLevelAnnotatedOnly {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2013-07-11")
        @CurrentTime("12:15:25")
        public void methodLevelAnnotated() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().instant());
        }
    }

    @CurrentDate("2050-01-01")
    @CurrentTime("15:35:45")
    public static final class ClassLevelAndMethodLevelAnnotated {

        @Rule
        public final FyodorTestRule rule = fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void classLevelAnnotated() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentDate("2013-07-11")
        @CurrentTime("10:55:15")
        public void methodLevelAnnotated() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }
}
