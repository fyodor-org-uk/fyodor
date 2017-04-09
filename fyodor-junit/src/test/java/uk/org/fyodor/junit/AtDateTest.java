package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtDate;

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
public final class AtDateTest {

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

        testRunner.scheduleTest(NoAnnotationsWithConfiguredRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsWithConfiguredRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialDate)
                .duringTest(of(1999, 12, 31))
                .whenTestHasFinished(initialDate);

        assertThat(reporter.reportFor(NoAnnotationsWithConfiguredRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialDate)
                .duringTest(of(1999, 12, 31))
                .whenTestHasFinished(initialDate);
    }

    @Test
    public void annotatedTestMethods() {
        final LocalDate today = localDate().next();
        Timekeeper.from(utcClockOf(today));

        testRunner.scheduleTest(AtDateMethodAnnotation.class).run();

        assertThat(reporter.reportFor(AtDateMethodAnnotation.class, "first"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2011, 4, 13))
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(AtDateMethodAnnotation.class, "second"))
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

    public static final class AtDateMethodAnnotation {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @AtDate("2011-04-13")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        @AtDate("2015-09-18")
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }
    }

    public static final class BadDateString {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Test
        @AtDate("this-is-not-a-date")
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

    public static final class NoAnnotationsWithConfiguredRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.withCurrentDate(of(1999, 12, 31));

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
}
