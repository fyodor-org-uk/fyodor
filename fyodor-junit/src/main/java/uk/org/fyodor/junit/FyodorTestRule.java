package uk.org.fyodor.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.ZoneOffset.UTC;
import static org.junit.rules.RuleChain.outerRule;

public final class FyodorTestRule implements TestRule {

    private final TestRule delegate;

    public FyodorTestRule() {
        this.delegate = outerRule(new FyodorSeedRule())
                .around(new FyodorTimekeeperRule(() -> Timekeeper.current().date(), () -> Timekeeper.current().time()));
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

    public Temporality current() {
        return Timekeeper.current();
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
        return new FyodorTestRule(currentDate, () -> Timekeeper.current().time());
    }

    public static FyodorTestRule withCurrentTime(final LocalTime currentTime) {
        return withCurrentTime(() -> currentTime);
    }

    public static FyodorTestRule withCurrentTime(final Generator<LocalTime> currentTime) {
        return new FyodorTestRule(() -> Timekeeper.current().date(), currentTime);
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
