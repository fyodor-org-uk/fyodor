package uk.org.fyodor.time;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.junit.Reporter;
import uk.org.fyodor.junit.TestRunner;

import java.time.*;

import static java.time.LocalDate.of;
import static org.junit.Assert.assertTrue;
import static uk.org.fyodor.generators.RDG.localDate;
import static uk.org.fyodor.generators.time.LocalDateRange.today;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.range.Range.fixed;
import static uk.org.fyodor.time.FyodorTimekeeper.withCurrentDate;

@SuppressWarnings("ConstantConditions")
public final class CurrentDateTest {

    private static final Reporter<LocalDate> reporter = reporter();

    private final TestRunner<LocalDate> testRunner = new TestRunner<>(
            testStarted(reporter, Timekeeper::currentDate),
            testFailed(reporter, (failure) -> Timekeeper.currentDate()),
            testFinished(reporter, Timekeeper::currentDate));

    @Test
    public void usesCurrentDateWhenTestIsNotAnnotated() {
        final LocalDate today = localDate().next();
        Timekeeper.from(clockFrom(today));

        testRunner.scheduleTest(TestClassWithNoAnnotations.class).run();

        assertThat(reporter.reportFor(TestClassWithNoAnnotations.class, "first"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(today)
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(TestClassWithNoAnnotations.class, "second"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(today)
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(TestClassWithNoAnnotations.class, "third"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(today)
                .whenTestHasFinished(today);
    }

    @Test
    public void ruleConfiguredWithCurrentDate() {
        final LocalDate today = localDate().next();
        Timekeeper.from(clockFrom(today));

        testRunner.scheduleTest(RuleConfiguredWithCurrentDate.class).run();

        assertThat(reporter.reportFor(RuleConfiguredWithCurrentDate.class, "first"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(1999, 12, 31))
                .whenTestHasFinished(today);
    }

    @Test
    public void resetsDateAfterEachTestMethod() {
        final LocalDate today = localDate().next();
        final LocalDate yesterday = today.minusDays(1);

        Timekeeper.from(clockFrom(yesterday));
        Timekeeper.from(clockFrom(today));

        testRunner.scheduleTest(TestClassWithDateSpecificationOnClass.class).run();

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClass.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2011, 4, 13))
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClass.class, "redTest"))
                .beforeTestStarts(today)
                .whenFailed(today)
                .whenTestHasFinished(today);
    }

    @Test
    public void dateMayBeConfiguredPerTestMethod() {
        final LocalDate today = localDate().next();
        Timekeeper.from(clockFrom(today));

        testRunner
                .scheduleTest(TestClassWithDatedMethods.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithDatedMethods.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2016, 11, 25))
                .whenTestHasFinished(today);
    }

    @Test
    public void dateMayBeConfiguredPerClass() {
        final LocalDate today = LocalDate.parse("2017-06-21");
        Timekeeper.from(clockFrom(today));

        testRunner
                .scheduleTest(TestClassWithDateSpecificationOnClass.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClass.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2011, 4, 13))
                .whenTestHasFinished(today);
    }

    @Test
    public void methodLevelDateAnnotationsTakePriorityOverClassLevelAnnotations() {
        final LocalDate today = localDate().next();
        Timekeeper.from(clockFrom(today));

        testRunner
                .scheduleTest(TestClassWithDateSpecificationOnClassAndMethod.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClassAndMethod.class, "firstTestMethodUsingClassAnnotation"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2050, 1, 1))
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClassAndMethod.class, "secondTestMethodUsingClassAnnotation"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2050, 1, 1))
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClassAndMethod.class, "firstTestMethodUsingMethodAnnotation"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(2013, 7, 11))
                .whenTestHasFinished(today);

        assertThat(reporter.reportFor(TestClassWithDateSpecificationOnClassAndMethod.class, "secondTestMethodUsingMethodAnnotation"))
                .didNotFail()
                .beforeTestStarts(today)
                .duringTest(of(1999, 12, 31))
                .whenTestHasFinished(today);
    }

    @Test
    public void testFailsWhenDateStringCannotBeParsed() {
        final LocalDate today = localDate().next();
        Timekeeper.from(clockFrom(today));

        testRunner.scheduleTest(TestWithBadDateString.class).run();

        assertThat(reporter.reportFor(TestWithBadDateString.class, "testWithBadDateString"))
                .beforeTestStarts(today)
                .whenTestHasFinished(today)
                .whenFailed(today)
                .failedBecauseOf(DateTimeException.class);
    }

    @Test
    public void testCanUseCurrentDateFromRule() {
        final LocalDate today = localDate().next();
        Timekeeper.from(clockFrom(today));

        testRunner.scheduleTest(TestClassUsingCurrentDateFromRule.class).run();

        assertThat(reporter.reportFor(TestClassUsingCurrentDateFromRule.class, "currentDateFromRule"))
                .duringTest(of(2016, 11, 25));
    }

    private static Clock clockFrom(final LocalDate date) {
        final Instant utcInstant = date.atTime(12, 0, 0).toInstant(ZoneOffset.UTC);
        return Clock.fixed(utcInstant, ZoneOffset.UTC);
    }

    public static final class TestClassUsingCurrentDateFromRule {
        @Rule
        public final FyodorTimekeeper rule = FyodorTimekeeper.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2016-11-25")
        public void currentDateFromRule() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.currentDate());
        }
    }

    public static final class TestClassWithDatedMethods {

        @Rule
        public final FyodorTimekeeper rule = FyodorTimekeeper.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2016-11-25")
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        @CurrentDate("-128291386-06-21")
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
            assertTrue(false);
        }
    }

    @CurrentDate("2011-04-13")
    public static final class TestClassWithDateSpecificationOnClass {

        @Rule
        public final FyodorTimekeeper rule = FyodorTimekeeper.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
            assertTrue(false);
        }
    }

    @CurrentDate("2050-01-01")
    public static final class TestClassWithDateSpecificationOnClassAndMethod {

        @Rule
        public final FyodorTimekeeper rule = FyodorTimekeeper.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void firstTestMethodUsingClassAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        @CurrentDate("2013-07-11")
        public void firstTestMethodUsingMethodAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        public void secondTestMethodUsingClassAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        @CurrentDate("1999-12-31")
        public void secondTestMethodUsingMethodAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }

        @Test
        public void failingTestMethodUsingClassAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
            assertTrue(false);
        }

        @Test
        @CurrentDate("2016-11-25")
        public void failingTestMethodUsingMethodAnnotation() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
            assertTrue(false);
        }
    }

    public static final class TestWithBadDateString {

        @Rule
        public final FyodorTimekeeper rule = FyodorTimekeeper.timekeeper();

        @Test
        @CurrentDate("this-is-not-a-date")
        public void testWithBadDateString() {
        }
    }

    public static final class TestClassWithNoAnnotations {

        @Rule
        public final FyodorTimekeeper rule = FyodorTimekeeper.timekeeper();

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

        @Test
        public void third() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }
    }

    public static final class RuleConfiguredWithCurrentDate {

        @Rule
        public final FyodorTimekeeper rule = withCurrentDate(localDate(fixed(of(1999, 12, 31))));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), localDate(today()).next());
        }
    }
}
