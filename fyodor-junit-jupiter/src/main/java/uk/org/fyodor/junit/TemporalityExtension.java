package uk.org.fyodor.junit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.TestExtensionContext;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.TimekeeperFyodorTestCallback;
import uk.org.fyodor.testapi.TimekeeperFyodorTestCallback.TimeController;

import java.time.*;

import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.FyodorTestAdapter.fyodorTestOf;

final class TemporalityExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback {

    private final TimekeeperFyodorTestCallback timekeeperCallback = new TimekeeperFyodorTestCallback(timeController());

    @Override
    public void beforeTestExecution(final TestExtensionContext testExtensionContext) throws Exception {
        timekeeperCallback.beforeTestExecution(fyodorTestOf(testExtensionContext));
    }

    @Override
    public void afterTestExecution(final TestExtensionContext testExtensionContext) throws Exception {
        timekeeperCallback.afterTestExecution(fyodorTestOf(testExtensionContext));
    }

    private static TimeController timeController() {
        return new TimeController() {
            @Override
            public LocalDate currentDate() {
                return current().date();
            }

            @Override
            public LocalTime currentTime() {
                return current().time();
            }

            @Override
            public ZoneId currentZone() {
                return current().zone();
            }

            @Override
            public void setDateTimeAndZone(final ZonedDateTime dateTimeAndZone) {
                Timekeeper.from(Clock.fixed(dateTimeAndZone.toInstant(), dateTimeAndZone.getZone()));
            }

            @Override
            public void revertToPreviousDateTimeAndZone() {
                Timekeeper.rollback();
            }
        };
    }
}
