package uk.org.fyodor.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.CurrentZone;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.Clock.fixed;
import static uk.org.fyodor.generators.RDG.localDateTime;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.FyodorTestRule.from;
import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.junit.TimeFactory.Clocks.clockOf;

@SuppressWarnings("ConstantConditions")
public final class CurrentZoneTest {

    private static final Reporter<ZoneId> reporter = reporter();

    private final TestRunner<ZoneId> testRunner = new TestRunner<>(
            testStarted(reporter, () -> current().zone()),
            testFailed(reporter, (failure) -> current().zone()),
            testFinished(reporter, () -> current().zone()));

    @Test
    public void noAnnotationsWithDefaultRule() {
        final ZoneId initialZone = zoneId().next();
        Timekeeper.from(clockOf(localDateTime().next().atZone(initialZone)));

        testRunner.scheduleTest(NoAnnotationsWithDefaultRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZone)
                .duringTest(initialZone)
                .whenTestHasFinished(initialZone);

        assertThat(reporter.reportFor(NoAnnotationsWithDefaultRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialZone)
                .duringTest(initialZone)
                .whenTestHasFinished(initialZone);
    }

    @Test
    public void zoneConfiguredWithRule() {
        final ZoneId initialZone = zoneId().next();
        Timekeeper.from(clockOf(localDateTime().next().atZone(initialZone)));

        testRunner.scheduleTest(NoAnnotationsWithConfiguredRule.class).run();

        assertThat(reporter.reportFor(NoAnnotationsWithConfiguredRule.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZone)
                .duringTest(ZoneId.of("Australia/Darwin"))
                .whenTestHasFinished(initialZone);

        assertThat(reporter.reportFor(NoAnnotationsWithConfiguredRule.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialZone)
                .duringTest(ZoneId.of("Australia/Darwin"))
                .whenTestHasFinished(initialZone);
    }

    @Test
    public void annotatedTestMethods() {
        final ZoneId initialZone = zoneId().next();
        Timekeeper.from(clockOf(localDateTime().next().atZone(initialZone)));

        testRunner.scheduleTest(WithCurrentZoneMethodAnnotations.class).run();

        assertThat(reporter.reportFor(WithCurrentZoneMethodAnnotations.class, "first"))
                .didNotFail()
                .beforeTestStarts(initialZone)
                .duringTest(ZoneId.of("Asia/Shanghai"))
                .whenTestHasFinished(initialZone);

        assertThat(reporter.reportFor(WithCurrentZoneMethodAnnotations.class, "second"))
                .didNotFail()
                .beforeTestStarts(initialZone)
                .duringTest(ZoneId.of("Africa/Addis_Ababa"))
                .whenTestHasFinished(initialZone);
    }

    @Test
    public void testFailsWhenZoneIdIsNotValid() {
        final ZoneId initialZone = zoneId().next();
        Timekeeper.from(clockOf(localDateTime().next().atZone(initialZone)));

        testRunner.scheduleTest(BadZoneId.class).run();

        assertThat(reporter.reportFor(BadZoneId.class, "testWithBadZoneId"))
                .beforeTestStarts(initialZone)
                .whenTestHasFinished(initialZone)
                .whenFailed(initialZone)
                .failedBecauseOf(DateTimeException.class);
    }

    private static Generator<ZoneId> zoneId() {
        final Generator<String> zoneIdGenerator = RDG.value(ZoneId.getAvailableZoneIds());
        return () -> ZoneId.of(zoneIdGenerator.next());
    }

    public static final class WithCurrentZoneMethodAnnotations {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @CurrentZone("Asia/Shanghai")
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zone());
        }

        @Test
        @CurrentZone("Africa/Addis_Ababa")
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), rule.current().zone());
        }
    }

    public static final class BadZoneId {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Test
        @CurrentZone("this-is-not-a-valid-zone-id")
        public void testWithBadZoneId() {
        }
    }

    public static final class NoAnnotationsWithDefaultRule {

        @Rule
        public final FyodorTestRule rule = FyodorTestRule.fyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), Timekeeper.current().zone());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), Timekeeper.current().zone());
        }
    }

    public static final class NoAnnotationsWithConfiguredRule {

        @Rule
        public final FyodorTestRule rule = from(fixed(ZonedDateTime.now(ZoneId.of("Australia/Darwin")).toInstant(), ZoneId.of("Australia/Darwin")));

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void first() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), Timekeeper.current().zone());
        }

        @Test
        public void second() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), Timekeeper.current().zone());
        }
    }
}
