package uk.org.fyodor.junit;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runners.model.InitializationError;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.junit.TestRunner.TestRunDetails;
import uk.org.fyodor.junit.TestRunner.TestRunReport;

import java.time.DateTimeException;
import java.time.ZonedDateTime;

import static java.time.Clock.fixed;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.TestRunner.runnerFor;

final class TemporalityTest {

    @Test
    void dateTimeHasNotChangedAfterTest() {
        final ZonedDateTime originalDateTime = of(1999, 12, 31, 23, 59, 59, 0, UTC);
        Timekeeper.from(fixed(originalDateTime.toInstant(), UTC));

        runnerFor(StandardTestClass.class).run();

        assertThat(current().zonedDateTime()).isEqualTo(originalDateTime);
    }

    @Nested
    final class AtZone {
        @Test
        void testFailsWhenZoneStringCannotBeParsed() throws InitializationError {
            final TestRunReport report = runnerFor(BadZoneString.class).run();

            final TestRunDetails testDetails = report.detailsFor(BadZoneString.class, "testWithBadZoneString");

            assertThat(testDetails.failure().getException()).isInstanceOf(DateTimeException.class);
        }

        @Test
        void dateTimeHasNotChangedAfterTestFailsToStart() {
            final ZonedDateTime firstDateTime = of(1999, 12, 31, 23, 59, 59, 0, UTC);
            final ZonedDateTime secondDateTime = of(2000, 1, 1, 0, 0, 0, 1, UTC);
            Timekeeper.from(fixed(firstDateTime.toInstant(), UTC));
            Timekeeper.from(fixed(secondDateTime.toInstant(), UTC));

            runnerFor(BadZoneString.class).run();

            assertThat(current().zonedDateTime()).isEqualTo(secondDateTime);
        }
    }

    @Nested
    final class AtTime {
        @Test
        void testFailsWhenTimeStringCannotBeParsed() throws InitializationError {
            final TestRunReport report = runnerFor(BadTimeString.class).run();

            final TestRunDetails testDetails = report.detailsFor(BadTimeString.class, "testWithBadTimeString");

            assertThat(testDetails.failure().getException()).isInstanceOf(DateTimeException.class);
        }

        @Test
        void dateTimeHasNotChangedAfterTestFailsToStart() {
            final ZonedDateTime firstDateTime = of(1999, 12, 31, 23, 59, 59, 0, UTC);
            final ZonedDateTime secondDateTime = of(2000, 1, 1, 0, 0, 0, 1, UTC);
            Timekeeper.from(fixed(firstDateTime.toInstant(), UTC));
            Timekeeper.from(fixed(secondDateTime.toInstant(), UTC));

            runnerFor(BadTimeString.class).run();

            assertThat(current().zonedDateTime()).isEqualTo(secondDateTime);
        }
    }

    @Nested
    final class AtDate {
        @Test
        void testFailsWhenDateStringCannotBeParsed() throws InitializationError {
            final TestRunReport report = runnerFor(BadDateString.class).run();

            final TestRunDetails testDetails = report.detailsFor(BadDateString.class, "testWithBadDateString");

            assertThat(testDetails.failure().getException()).isInstanceOf(DateTimeException.class);
        }

        @Test
        void dateTimeHasNotChangedAfterTestFailsToStart() {
            final ZonedDateTime firstDateTime = of(1999, 12, 31, 23, 59, 59, 0, UTC);
            final ZonedDateTime secondDateTime = of(2000, 1, 1, 0, 0, 0, 1, UTC);
            Timekeeper.from(fixed(firstDateTime.toInstant(), UTC));
            Timekeeper.from(fixed(secondDateTime.toInstant(), UTC));

            runnerFor(BadDateString.class).run();

            assertThat(current().zonedDateTime()).isEqualTo(secondDateTime);
        }
    }

    @ExtendWithFyodor
    static final class StandardTestClass {
        @Test
        @uk.org.fyodor.testapi.AtZone("Europe/London")
        @uk.org.fyodor.testapi.AtTime("12:13:14")
        @uk.org.fyodor.testapi.AtDate("2005-06-16")
        void test() {
        }
    }

    @ExtendWithFyodor
    static final class BadZoneString {
        @Test
        @uk.org.fyodor.testapi.AtZone("this-is-not-a-zone")
        void testWithBadZoneString() {
        }
    }

    @ExtendWithFyodor
    static final class BadTimeString {
        @Test
        @uk.org.fyodor.testapi.AtTime("this-is-not-a-time")
        void testWithBadTimeString() {
        }
    }

    @ExtendWithFyodor
    static final class BadDateString {
        @Test
        @uk.org.fyodor.testapi.AtDate("this-is-not-a-date")
        void testWithBadDateString() {
        }
    }
}
