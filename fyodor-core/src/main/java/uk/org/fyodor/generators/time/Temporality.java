package uk.org.fyodor.generators.time;

import java.time.*;

public interface Temporality {
    LocalDate date();

    LocalTime time();

    LocalDateTime dateTime();

    Instant instant();

    Clock clock();
}
