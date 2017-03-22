package uk.org.fyodor.generators.time;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public final class Timekeeper {

    private static final ThreadLocal<Clock> clock = ThreadLocal.withInitial(Clock::systemDefaultZone);

    private Timekeeper() {
    }

    public static void from(final Clock clock) {
        Timekeeper.clock.set(clock);
    }

    public static LocalDate currentDate() {
        return clock.get().instant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime currentTime() {
        return clock.get().instant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

}
