package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.DateTimeException;
import java.time.LocalDate;

import static java.time.LocalDate.of;
import static uk.org.fyodor.generators.RDG.localDate;
import static uk.org.fyodor.generators.time.LocalDateRange.today;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.junit.TimeFactory.Clocks.utcClockOf;

@SuppressWarnings("ConstantConditions")
public final class CurrentDateTest {

    private static final Reporter<LocalDate> reporter = reporter();

    private final TestRunner<LocalDate> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().date()),
            testFailed(reporter, (failure) -> current().date()),
            testFinished(reporter, () -> current().date()));

    @Test
    public void noAnnotationsWithDefaultRule() {
        final LocalDate initialDate = localDate().next();

        Timekeeper.from(utcClockOf(initialDate));

        testRunner.scheduleTest(NoAnnotationsWithDefaultRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDate)
                .duringTest(initialDate)
                .whenTestHasFinished(initialDate);

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialDate)
                .duringTest(initialDate)
                .whenTestHasFinished(initialDate);
    }

    @Test
    public void dateConfiguredWithRule() {
        final LocalDate initialDate = localDate().next();

        Timekeeper.from(utcClockOf(initialDate));

        testRunner.scheduleTest(DateConfiguredWithRule.class).run();

        assertThat(reporter.reportFor(DateConfiguredWithRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDate)
                .duringTest(of(1999, 12, 31))
                .whenTestHasFinished(initialDate);
    }

    @Test
    public void resetsDateAfterEachTestMethod() {
        final LocalDate today = localDate().next();
        final LocalDate yesterday = today.minusDays(1);

        Timekeeper.from(utcClockOf(yesterday));
        Timekeeper.from(utcClockOf(today));

        testRunner.scheduleTest(WithAnnotations.class).run();

        assertThat(reporter.reportFor(WithAnnotations.class, "first"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2011, 4, 13))
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(WithAnnotations.class, "second"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2015, 9, 18))
                .whenTestHasFinished(today);
    }

    @Test
    public void testFailsWhenDateStringCannotBeParsed() {
        final LocalDate initialDate = localDate().next();

        Timekeeper.from(utcClockOf(initialDate));

        testRunner.scheduleTest(BadDateString.class).run();

        assertThat(reporter.reportFor(BadDateString.class, "testWithBadDateString"))
                .beforeTestStarts(initialDate)
                .whenTestHasFinished(initialDate)
                .whenFailed(initialDate)
                .failedBecauseOf(DateTimeException.class);
    }

    public static final class WithAnnotations {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2011-04-13")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        @CurrentDate("2015-09-18")
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }
    }

    public static final class BadDateString {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Test
        @CurrentDate("this-is-not-a-date")
        public void testWithBadDateString() {
        }
    }

    public static final class NoAnnotationsWithDefaultRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }
    }

    public static final class DateConfiguredWithRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.withCurrentDate(of(1999, 12, 31));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }
    }
}
