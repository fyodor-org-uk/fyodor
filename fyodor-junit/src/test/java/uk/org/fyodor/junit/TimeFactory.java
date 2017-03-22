package uk.org.fyodor.junit;

import java.time.*;

import static java.time.LocalDateTime.of;
import static java.time.ZoneOffset.UTC;

final class TimeFactory {
    static final class Instants {
        static Instant utcInstantOf(final LocalDate date, final int hour, final int minute, final int second) {
            return utcInstantOf(date.atTime(hour, minute, second));
        }

        static Instant utcInstantOf(final int year, final int month, final int dayOfMonth, final LocalTime time) {
            return utcInstantOf(LocalDate.of(year, month, dayOfMonth).atTime(time));
        }

        static Instant utcInstantOf(final int year, final int month, final int day) {
            return utcInstantOf(LocalDate.of(year, month, day).atStartOfDay());
        }

        static Instant utcInstantOf(final int year, final int month, final int day,
                                    final int hour, final int minute, final int second) {
            return utcInstantOf(LocalDate.of(year, month, day).atTime(hour, minute, second, 0));
        }

        static Instant utcInstantOf(final LocalDateTime dateTime) {
            return of(dateTime.toLocalDate(), dateTime.toLocalTime()).toInstant(UTC);
        }
    }

    static final class Clocks {
        static Clock utcClockOf(final LocalDate date) {
            return utcClockOf(date.atStartOfDay());
        }

        static Clock utcClockOf(final LocalTime time) {
            return utcClockOf(LocalDate.now().atTime(time));
        }

        static Clock utcClockOf(final LocalDateTime localDateTime) {
            return utcClockOf(localDateTime.toInstant(UTC));
        }

        static Clock utcClockOf(final Instant instant) {
            return Clock.fixed(instant, UTC);
        }
    }
}
