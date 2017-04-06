package uk.org.fyodor.generators.time;

import java.time.*;
import java.util.Stack;

import static java.lang.ThreadLocal.withInitial;
import static java.time.Clock.systemDefaultZone;

public final class Timekeeper {

    private static final ThreadLocal<Temporalities> temporalities = withInitial(() -> new Temporalities(temporalityFrom(systemDefaultZone())));

    private Timekeeper() {
    }

    public static void from(final Clock clock) {
        temporalities.get().next(temporalityFrom(clock));
    }

    public static void rollback() {
        temporalities.get().previous();
    }

    public static Temporality current() {
        return temporalities.get().current();
    }

    private static Temporality temporalityFrom(final Clock clock) {
        return new Temporality() {
            @Override
            public LocalDate date() {
                return zonedDateTime().toLocalDate();
            }

            @Override
            public LocalTime time() {
                return zonedDateTime().toLocalTime();
            }

            @Override
            public LocalDateTime dateTime() {
                return zonedDateTime().toLocalDateTime();
            }

            @Override
            public Instant instant() {
                return clock().instant();
            }

            @Override
            public Clock clock() {
                return clock;
            }

            @Override
            public ZonedDateTime zonedDateTime() {
                return instant().atZone(zone());
            }

            @Override
            public ZoneId zone() {
                return clock().getZone();
            }
        };
    }

    private static final class Temporalities {
        private final Stack<Temporality> temporalityStack = new Stack<>();

        private Temporalities(final Temporality temporality) {
            this.temporalityStack.push(temporality);
        }

        private Temporality current() {
            return temporalityStack.peek();
        }

        void previous() {
            temporalityStack.pop();
        }

        void next(final Temporality nextTemporality) {
            temporalityStack.push(nextTemporality);
        }
    }
}
