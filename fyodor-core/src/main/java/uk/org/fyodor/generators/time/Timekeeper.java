package uk.org.fyodor.generators.time;

import java.time.*;
import java.util.Stack;

import static java.lang.ThreadLocal.withInitial;
import static java.time.Clock.systemDefaultZone;

public final class Timekeeper {

    private static final ThreadLocal<Clocks> clocks = withInitial(() -> new Clocks(systemDefaultZone()));

    private Timekeeper() {
    }

    public static void from(final Clock clock) {
        clocks.get().next(clock);
    }

    public static void rollback() {
        clocks.get().previous();
    }

    public static LocalDate currentDate() {
        return currentInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }

    public static LocalTime currentTime() {
        return currentInstant().atZone(ZoneOffset.UTC).toLocalTime();
    }

    public static Instant currentInstant() {
        return currentClock().instant();
    }

    public static Clock currentClock() {
        return clocks.get().current();
    }

    private static final class Clocks {
        private final Stack<Clock> clockStack = new Stack<>();

        private Clocks(final Clock initialClock) {
            this.clockStack.push(initialClock);
        }

        private Clock current() {
            return clockStack.peek();
        }

        void previous() {
            clockStack.pop();
        }

        void next(final Clock nextClock) {
            clockStack.push(nextClock);
        }
    }
}
