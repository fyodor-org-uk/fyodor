package uk.org.fyodor.generators.time;

import java.time.*;

public final class Timekeeper {

    private static final ThreadLocal<Clock> clock = ThreadLocal.withInitial(Clock::systemDefaultZone);

    private Timekeeper() {
    }

    public static void from(final Clock clock) {
        Timekeeper.clock.set(clock);
    }

    public static Instant instant() {
        return clock.get().instant();
    }

    public static LocalDate currentDate() {
        return clock.get().instant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime currentTime() {
        return clock.get().instant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

}
