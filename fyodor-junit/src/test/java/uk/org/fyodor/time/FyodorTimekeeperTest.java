package uk.org.fyodor.time;

import org.junit.Test;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.createTestDescription;

public class FyodorTimekeeperTest {

    @Test
    public void startingWithCurrentDateGenerator() {
        final LocalDate today = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();
        Timekeeper.from(clock(today.atTime(currentTime)));

        FyodorTimekeeper.withCurrentDate(() -> LocalDate.of(1999, 12, 31))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant()).isEqualTo(utcInstantOf(1999, 12, 31, currentTime));
    }

    @Test
    public void startingWithCurrentDate() {
        final LocalDate today = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();
        Timekeeper.from(clock(today.atTime(currentTime)));

        FyodorTimekeeper.withCurrentDate(LocalDate.of(1999, 12, 31))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant()).isEqualTo(utcInstantOf(1999, 12, 31, currentTime));
    }

    @Test
    public void startingWithCurrentTimeGenerator() {
        final LocalDate today = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();
        Timekeeper.from(clock(today.atTime(currentTime)));

        FyodorTimekeeper.withCurrentTime(() -> LocalTime.of(3, 4, 5))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant()).isEqualTo(utcInstantOf(today, 3, 4, 5));
    }

    @Test
    public void startingWithCurrentTime() {
        final LocalDate today = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();
        Timekeeper.from(clock(today.atTime(currentTime)));

        FyodorTimekeeper.withCurrentTime(LocalTime.of(3, 4, 5))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant()).isEqualTo(utcInstantOf(today, 3, 4, 5));
    }

    @Test
    public void startingWithCurrentDateAndTimeGenerator() {
        final LocalDate today = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();
        Timekeeper.from(clock(today.atTime(currentTime)));

        FyodorTimekeeper.withCurrentDateAndTime(() -> LocalDateTime.of(1999, 12, 31, 23, 59, 59))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant())
                .isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));
    }

    @Test
    public void startingWithCurrentDateAndTime() {
        final LocalDate today = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();
        Timekeeper.from(clock(today.atTime(currentTime)));

        FyodorTimekeeper.withCurrentDateAndTime(LocalDateTime.of(1999, 12, 31, 23, 59, 59))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant())
                .isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));
    }

    @Test
    public void startingFromClock() {
        Timekeeper.from(clock(RDG.localDateTime().next()));

        FyodorTimekeeper.from(clock(LocalDateTime.of(1999, 12, 31, 23, 59, 59)))
                .starting(createTestDescription(FyodorTimekeeperTest.class, "test"));

        assertThat(Timekeeper.currentInstant())
                .isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));
    }

    private Instant utcInstantOf(final LocalDate date, final int hour, final int minute, final int second) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute, second)).toInstant(UTC);
    }

    private Instant utcInstantOf(final int year, final int month, final int dayOfMonth, final int hour, final int minute, final int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).toInstant(UTC);
    }

    private Instant utcInstantOf(final int year, final int month, final int dayOfMonth, final LocalTime time) {
        return LocalDateTime.of(LocalDate.of(year, month, dayOfMonth), time).toInstant(UTC);
    }

    private static Clock clock(final LocalDateTime localDateTime) {
        return Clock.fixed(localDateTime.toInstant(UTC), UTC);
    }
}