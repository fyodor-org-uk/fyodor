package uk.org.fyodor.junit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.AtDate;
import uk.org.fyodor.testapi.AtTime;
import uk.org.fyodor.testapi.AtZone;
import uk.org.fyodor.testapi.Current;

import java.time.*;

import static java.time.Clock.fixed;
import static java.time.ZonedDateTime.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.org.fyodor.generators.RDG.*;
import static uk.org.fyodor.generators.time.Timekeeper.current;

@SuppressWarnings("Duplicates")
@ExtendWithFyodor
final class TemporalityExtensionTest {

    private final LocalDate initialDate = localDate().next();
    private final LocalTime initialTime = localTime().next();
    private final ZoneId initialZone = zoneId().next();

    @BeforeEach
    void configureFyodor() {
        Timekeeper.from(fixed(of(initialDate, initialTime, initialZone).toInstant(), initialZone));
    }

    @AfterEach
    void revertFyodor() {
        Timekeeper.rollback();
    }

    @Nested
    final class NotAnnotated {
        @Test
        void usesInitialTimekeeperDate(@Current final LocalDate date,
                                       @Current final LocalTime time,
                                       @Current final ZoneId zone,
                                       @Current final LocalDateTime dateTime,
                                       @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = initialDate;
            final LocalTime expectedTime = initialTime;
            final ZoneId expectedZone = initialZone;
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }

        @Test
        void injectsTemporalityWithInitialDateTimeAndZone(final Temporality temporality) {
            final LocalDate expectedDate = initialDate;
            final LocalTime expectedTime = initialTime;
            final ZoneId expectedZone = initialZone;
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, temporality.date());
            assertEquals(expectedTime, temporality.time());
            assertEquals(expectedZone, temporality.zone());
            assertEquals(expectedDateTime, temporality.dateTime());
            assertEquals(expectedZonedDateTime, temporality.zonedDateTime());
        }
    }

    @Nested
    @AtDate("2010-06-15")
    final class AnnotatedWithAtDate {
        @Test
        @AtDate("1999-12-31")
        void usesDateFromTestMethodAnnotation(@Current final LocalDate date,
                                              @Current final LocalTime time,
                                              @Current final ZoneId zone,
                                              @Current final LocalDateTime dateTime,
                                              @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = LocalDate.of(1999, 12, 31);
            final LocalTime expectedTime = initialTime;
            final ZoneId expectedZone = initialZone;
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }

        @Test
        void usesDateFromTestClassAnnotation(@Current final LocalDate date,
                                             @Current final LocalTime time,
                                             @Current final ZoneId zone,
                                             @Current final LocalDateTime dateTime,
                                             @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = LocalDate.of(2010, 6, 15);
            final LocalTime expectedTime = initialTime;
            final ZoneId expectedZone = initialZone;
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }
    }

    @Nested
    @AtTime("23:59:59")
    final class AnnotatedWithAtTime {
        @Test
        @AtTime("11:12:13")
        void usesTimeFromTestMethodAnnotation(@Current final LocalDate date,
                                              @Current final LocalTime time,
                                              @Current final ZoneId zone,
                                              @Current final LocalDateTime dateTime,
                                              @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = initialDate;
            final LocalTime expectedTime = LocalTime.of(11, 12, 13);
            final ZoneId expectedZone = initialZone;
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }

        @Test
        void usesTimeFromTestClassAnnotation(@Current final LocalDate date,
                                             @Current final LocalTime time,
                                             @Current final ZoneId zone,
                                             @Current final LocalDateTime dateTime,
                                             @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = initialDate;
            final LocalTime expectedTime = LocalTime.of(23, 59, 59);
            final ZoneId expectedZone = initialZone;
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }
    }

    @Nested
    @AtZone("Asia/Dhaka")
    final class AnnotatedWithAtZone {
        @Test
        @AtZone("America/Chicago")
        void usesZoneFromTestMethodAnnotation(@Current final LocalDate date,
                                              @Current final LocalTime time,
                                              @Current final ZoneId zone,
                                              @Current final LocalDateTime dateTime,
                                              @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = initialDate;
            final LocalTime expectedTime = initialTime;
            final ZoneId expectedZone = ZoneId.of("America/Chicago");
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }

        @Test
        void usesZoneFromTestClassAnnotation(@Current final LocalDate date,
                                             @Current final LocalTime time,
                                             @Current final ZoneId zone,
                                             @Current final LocalDateTime dateTime,
                                             @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = initialDate;
            final LocalTime expectedTime = initialTime;
            final ZoneId expectedZone = ZoneId.of("Asia/Dhaka");
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }
    }

    @Nested
    @AtDate("1999-12-31")
    @AtTime("23:59:59")
    @AtZone("Europe/London")
    final class AnnotatedWithAtDateAndAtTimeAndAtZone {
        @Test
        @AtDate("2000-01-01")
        @AtTime("12:01:55")
        @AtZone("Australia/Darwin")
        void usesTestMethodAnnotationsOverTestClassAnnotations(@Current final LocalDate date,
                                                               @Current final LocalTime time,
                                                               @Current final ZoneId zone,
                                                               @Current final LocalDateTime dateTime,
                                                               @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = LocalDate.of(2000, 1, 1);
            final LocalTime expectedTime = LocalTime.of(12, 1, 55);
            final ZoneId expectedZone = ZoneId.of("Australia/Darwin");
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }

        @Test
        void usesTestClassAnnotationsWhenThereAreNoTestMethodAnnotations(@Current final LocalDate date,
                                                                         @Current final LocalTime time,
                                                                         @Current final ZoneId zone,
                                                                         @Current final LocalDateTime dateTime,
                                                                         @Current final ZonedDateTime zonedDateTime) {
            final LocalDate expectedDate = LocalDate.of(1999, 12, 31);
            final LocalTime expectedTime = LocalTime.of(23, 59, 59);
            final ZoneId expectedZone = ZoneId.of("Europe/London");
            final LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
            final ZonedDateTime expectedZonedDateTime = expectedDateTime.atZone(expectedZone);

            assertEquals(expectedDate, date);
            assertEquals(expectedDate, current().date());

            assertEquals(expectedTime, time);
            assertEquals(expectedTime, current().time());

            assertEquals(expectedZone, zone);
            assertEquals(expectedZone, current().zone());

            assertEquals(expectedDateTime, dateTime);
            assertEquals(expectedZonedDateTime, zonedDateTime);
        }
    }
}
