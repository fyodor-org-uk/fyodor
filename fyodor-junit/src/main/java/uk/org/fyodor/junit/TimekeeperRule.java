package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.FyodorTestCallback;
import uk.org.fyodor.testapi.TimekeeperFyodorTestCallback;
import uk.org.fyodor.testapi.TimekeeperFyodorTestCallback.TimeController;

import java.time.*;

import static uk.org.fyodor.junit.FyodorTestAdapter.fyodorTestOf;

final class TimekeeperRule extends TestWatcher {

    private final FyodorTestCallback callback;

    TimekeeperRule(final Generator<ZonedDateTime> currentDateTime) {
        callback = new TimekeeperFyodorTestCallback(new TimeController() {
            @Override
            public LocalDate currentDate() {
                return currentDateTime.next().toLocalDate();
            }

            @Override
            public LocalTime currentTime() {
                return currentDateTime.next().toLocalTime();
            }

            @Override
            public ZoneId currentZone() {
                return currentDateTime.next().getZone();
            }

            @Override
            public void setDateTimeAndZone(final ZonedDateTime dateTimeAndZone) {
                Timekeeper.from(Clock.fixed(dateTimeAndZone.toInstant(), dateTimeAndZone.getZone()));
            }

            @Override
            public void revertToPreviousDateTimeAndZone() {
                Timekeeper.rollback();
            }
        });
    }

    @Override
    public void starting(final Description description) {
        callback.beforeTestExecution(fyodorTestOf(description));
    }

    @Override
    public void finished(final Description description) {
        callback.afterTestExecution(fyodorTestOf(description));
    }

}
