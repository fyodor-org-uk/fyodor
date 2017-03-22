package uk.org.fyodor.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static java.time.ZoneOffset.UTC;
import static org.junit.rules.RuleChain.outerRule;

public final class FyodorTestRule implements TestRule {

    private final TestRule delegate;

    /**
     * @deprecated Use static factory methods instead
     */
    @Deprecated
    public FyodorTestRule() {
        this.delegate = outerRule(new FyodorSeedRule())
                .around(new FyodorTimekeeperRule(() -> Timekeeper.currentDate(), () -> Timekeeper.currentTime()));
    }

    private FyodorTestRule(final Generator<LocalDate> currentDate,
                           final Generator<LocalTime> currentTime) {
        this.delegate = outerRule(new FyodorSeedRule())
                .around(new FyodorTimekeeperRule(currentDate, currentTime));
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return delegate.apply(base, description);
    }

    public Instant currentInstant() {
        return Timekeeper.currentInstant();
    }

    public Clock currentClock() {
        return Timekeeper.currentClock();
    }

    public LocalDate currentDate() {
        return Timekeeper.currentDate();
    }

    public LocalTime currentTime() {
        return Timekeeper.currentTime();
    }

    public LocalDateTime currentDateTime() {
        return Timekeeper.currentDateTime();
    }

    public static FyodorTestRule fyodorTestRule() {
        return new FyodorTestRule();
    }

    public static FyodorTestRule from(final Clock clock) {
        return new FyodorTestRule(
                () -> clock.instant().atZone(UTC).toLocalDate(),
                () -> clock.instant().atZone(UTC).toLocalTime());
    }

    public static FyodorTestRule withCurrentDate(final LocalDate currentDate) {
        return withCurrentDate(() -> currentDate);
    }

    public static FyodorTestRule withCurrentDate(final Generator<LocalDate> currentDate) {
        return new FyodorTestRule(currentDate, () -> Timekeeper.currentTime());
    }

    public static FyodorTestRule withCurrentTime(final LocalTime currentTime) {
        return withCurrentTime(() -> currentTime);
    }

    public static FyodorTestRule withCurrentTime(final Generator<LocalTime> currentTime) {
        return new FyodorTestRule(() -> Timekeeper.currentDate(), currentTime);
    }

    public static FyodorTestRule withCurrentDateAndTime(final LocalDateTime currentDateTime) {
        return withCurrentDateAndTime(() -> currentDateTime);
    }

    public static FyodorTestRule withCurrentDateAndTime(final Generator<LocalDateTime> currentDateTime) {
        return new FyodorTestRule(
                () -> currentDateTime.next().toLocalDate(),
                () -> currentDateTime.next().toLocalTime());
    }

}
