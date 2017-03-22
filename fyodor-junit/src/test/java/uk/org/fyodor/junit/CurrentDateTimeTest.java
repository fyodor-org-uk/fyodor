package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static java.time.LocalTime.of;
import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.assertTrue;
import static uk.org.fyodor.generators.RDG.*;
import static uk.org.fyodor.generators.time.InstantRange.now;
import static uk.org.fyodor.junit.FyodorTimekeeperRule.*;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.range.Range.fixed;

@SuppressWarnings("ConstantConditions")
public final class CurrentDateTimeTest {

    private static final Reporter<Instant> reporter = reporter();

    private final TestRunner<Instant> testRunner = new TestRunner<>(
            testStarted(reporter, Timekeeper::currentInstant),
            testFailed(reporter, (failure) -> Timekeeper.currentInstant()),
            testFinished(reporter, Timekeeper::currentInstant));

    @Test
    public void dateAndTimeMayBeConfiguredPerTestMethod() {
        final Instant initialDateTime = instant().next();
        Timekeeper.from(Clock.fixed(initialDateTime, UTC));

        testRunner
                .scheduleTest(TestClassWithDateTimeAnnotationAtMethodLevel.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithDateTimeAnnotationAtMethodLevel.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(utcInstantOf(2016, 11, 25, 16, 9, 50))
                .whenTestHasFinished(initialDateTime);

        assertThat(reporter.reportFor(TestClassWithDateTimeAnnotationAtMethodLevel.class, "redTest"))
                .beforeTestStarts(initialDateTime)
                .duringTest(utcInstantOf(2016, 11, 20, 11, 51, 34))
                .whenTestHasFinished(initialDateTime)
                .whenFailed(initialDateTime);
    }

    @Test
    public void dateAndTimeMayBeConfiguredPerClass() {
        final Instant initialDateTime = instant().next();
        Timekeeper.from(Clock.fixed(initialDateTime, UTC));

        testRunner
                .scheduleTest(TestClassWithDateTimeSpecificationOnClass.class)
                .run();

        assertThat(reporter.reportFor(TestClassWithDateTimeSpecificationOnClass.class, "greenTest"))
                .didNotFail()
                .beforeTestStarts(initialDateTime)
                .duringTest(utcInstantOf(2011, 4, 13, 16, 2, 31))
                .whenTestHasFinished(initialDateTime);

        assertThat(reporter.reportFor(TestClassWithDateTimeSpecificationOnClass.class, "redTest"))
                .beforeTestStarts(initialDateTime)
                .duringTest(utcInstantOf(2011, 4, 13, 16, 2, 31))
                .whenTestHasFinished(initialDateTime)
                .whenFailed(initialDateTime);
    }

    @Test
    public void classLevelDatesCanBeAppliedWithMethodLevelTimes() {
        final Instant now = instant().next();
        Timekeeper.from(Clock.fixed(now, UTC));


        testRunner.scheduleTest(TestClassWithClassLevelDateAndMethodLevelTime.class).run();

        assertThat(reporter.reportFor(TestClassWithClassLevelDateAndMethodLevelTime.class, "first"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(utcInstantOf(2000, 1, 1, 0, 0, 1))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithClassLevelDateAndMethodLevelTime.class, "second"))
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(utcInstantOf(2000, 1, 1, 23, 59, 59))
                .whenTestHasFinished(now)
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithClassLevelDateAndMethodLevelTime.class, "failing"))
                .beforeTestStarts(now)
                .duringTest(null)
                .whenTestHasFinished(now)
                .whenFailed(now)
                .failedBecauseOf(AssertionError.class);
    }

    @Test
    public void classLevelTimesCanBeAppliedWithMethodLevelDates() {
        final Instant now = instant().next();
        Timekeeper.from(Clock.fixed(now, UTC));


        testRunner.scheduleTest(TestClassWithClassLevelTimeAndMethodLevelDate.class).run();

        assertThat(reporter.reportFor(TestClassWithClassLevelTimeAndMethodLevelDate.class, "first"))
                .beforeTestStarts(now)
                .duringTest(utcInstantOf(2000, 1, 1, 23, 59, 59))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithClassLevelTimeAndMethodLevelDate.class, "second"))
                .beforeTestStarts(now)
                .duringTest(utcInstantOf(2000, 12, 31, 23, 59, 59))
                .whenTestHasFinished(now);

        assertThat(reporter.reportFor(TestClassWithClassLevelTimeAndMethodLevelDate.class, "failing"))
                .beforeTestStarts(now)
                .duringTest(null)
                .whenTestHasFinished(now)
                .whenFailed(now)
                .failedBecauseOf(AssertionError.class);
    }

    @Test
    public void timeConfiguredInRule() {
        final LocalDate today = RDG.localDate().next();
        final Instant now = today.atTime(localTime().next()).toInstant(UTC);
        Timekeeper.from(Clock.fixed(now, UTC));

        testRunner.scheduleTest(RuleConfiguredWithTime.class).run();

        final Report<Instant> report = reporter.reportFor(RuleConfiguredWithTime.class, "first");

        assertThat(report)
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(today.atTime(10, 30, 45).toInstant(UTC))
                .whenTestHasFinished(now);
    }

    @Test
    public void dateConfiguredInRule() {
        final LocalDate today = RDG.localDate().next();
        final LocalTime currentTime = RDG.localTime().next();
        final Instant now = today.atTime(currentTime).toInstant(UTC);
        Timekeeper.from(Clock.fixed(now, UTC));

        testRunner.scheduleTest(RuleConfiguredWithDate.class).run();

        final Report<Instant> report = reporter.reportFor(RuleConfiguredWithDate.class, "first");

        assertThat(report)
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(LocalDate.of(1999, 12, 31).atTime(currentTime).toInstant(UTC))
                .whenTestHasFinished(now);
    }

    @Test
    public void ruleConfiguredWithDateAndTime() {
        final Instant now = RDG.instant().next();
        Timekeeper.from(Clock.fixed(now, UTC));

        testRunner.scheduleTest(RuleConfiguredWithDateAndTime.class).run();

        final Report<Instant> report = reporter.reportFor(RuleConfiguredWithDateAndTime.class, "first");

        assertThat(report)
                .didNotFail()
                .beforeTestStarts(now)
                .duringTest(LocalDateTime.of(1999, 12, 31, 23, 59, 59).toInstant(UTC))
                .whenTestHasFinished(now);
    }

    @Test
    public void dateAndTimeAnnotationsOverrideDateAndTimeConfiguredInRule() {
        final Instant now = RDG.instant().next();
        Timekeeper.from(Clock.fixed(now, UTC));

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

    @Test
    public void resetsBackToPreviousDateAndTimeAfterEachTestMethod() {
        final Instant now = RDG.instant().next();
        final Instant anHourAgo = now.minus(Duration.ofHours(1));

        Timekeeper.from(Clock.fixed(anHourAgo, UTC));
        Timekeeper.from(Clock.fixed(now, UTC));

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

    private static Instant utcInstantOf(final int year, final int month, final int day,
                                        final int hour, final int minute, final int second) {
        return LocalDate.of(year, month, day).atTime(hour, minute, second, 0).toInstant(UTC);
    }

    public static final class TestClassWithDateTimeAnnotationAtMethodLevel {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2016-11-25")
        @CurrentTime("16:09:50")
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentDate("2016-11-20")
        @CurrentTime("11:51:34")
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
            assertTrue(false);
        }
    }

    @CurrentDate("2011-04-13")
    @CurrentTime("16:02:31")
    public static final class TestClassWithDateTimeSpecificationOnClass {

        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

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

    @CurrentDate("2000-01-01")
    public static final class TestClassWithClassLevelDateAndMethodLevelTime {
        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentTime("00:00:01")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentTime("23:59:59")
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentTime("12:00:00")
        public void failing() {
            assertTrue(false);
        }
    }

    @CurrentTime("23:59:59")
    public static final class TestClassWithClassLevelTimeAndMethodLevelDate {
        @Rule
        public final FyodorTimekeeperRule rule = FyodorTimekeeperRule.timekeeper();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentDate("2000-01-01")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentDate("2000-12-31")
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentDate("2000-6-15")
        public void failing() {
            assertTrue(false);
        }
    }

    public static final class RuleConfiguredWithTime {

        @Rule
        public final FyodorTimekeeperRule rule = withCurrentTime(localTime(fixed(of(10, 30, 45))));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    public static final class RuleConfiguredWithDate {

        @Rule
        public final FyodorTimekeeperRule rule = withCurrentDate(localDate(fixed(LocalDate.of(1999, 12, 31))));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    public static final class RuleConfiguredWithDateAndTime {

        @Rule
        public final FyodorTimekeeperRule rule = withCurrentDateAndTime(localDateTime(
                fixed(LocalDate.of(1999, 12, 31)), fixed(LocalTime.of(23, 59, 59))));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }

    @CurrentDate("2010-01-01")
    @CurrentTime("12:00:00")
    public static final class DateAndTimeConfiguredInAnnotationsAndRule {

        @Rule
        public final FyodorTimekeeperRule rule = withCurrentDateAndTime(localDateTime(
                fixed(LocalDate.of(1999, 12, 31)), fixed(LocalTime.of(23, 59, 59))));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void annotatedClass() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }

        @Test
        @CurrentDate("2015-06-15")
        @CurrentTime("17:01:30")
        public void annotatedMethod() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), instant(now()).next());
        }
    }
}
