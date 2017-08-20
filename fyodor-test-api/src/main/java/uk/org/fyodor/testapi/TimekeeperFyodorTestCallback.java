package uk.org.fyodor.testapi;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.lang.ThreadLocal.withInitial;

public final class TimekeeperFyodorTestCallback implements FyodorTestCallback {

    private final ThreadLocal<Boolean> successfullyConfigured = withInitial(() -> false);

    private final TimeController timeController;

    public TimekeeperFyodorTestCallback(final TimeController timeController) {
        this.timeController = timeController;
    }

    @Override
    public void beforeTestExecution(final FyodorTest test) {
        final LocalDate currentDate = test.getAnnotation(AtDate.class)
                .map(AtDate::value)
                .map(LocalDate::parse)
                .orElse(timeController.currentDate());

        final LocalTime currentTime = test.getAnnotation(AtTime.class)
                .map(AtTime::value)
                .map(LocalTime::parse)
                .orElse(timeController.currentTime());

        final ZoneId currentZone = test.getAnnotation(AtZone.class)
                .map(AtZone::value)
                .map(ZoneId::of)
                .orElse(timeController.currentZone());

        timeController.setDateTimeAndZone(ZonedDateTime.of(currentDate, currentTime, currentZone));
        successfullyConfigured.set(true);
    }

    @Override
    public void afterTestExecution(final FyodorTest test) {
        if (successfullyConfigured.get()) {
            timeController.revertToPreviousDateTimeAndZone();
        }
    }

    @Override
    public void testFailed(final FyodorTest test, final Throwable throwable) throws Throwable {
    }

    public interface TimeController {
        LocalDate currentDate();

        LocalTime currentTime();

        ZoneId currentZone();

        void setDateTimeAndZone(ZonedDateTime dateTimeAndZone);

        void revertToPreviousDateTimeAndZone();
    }
}
