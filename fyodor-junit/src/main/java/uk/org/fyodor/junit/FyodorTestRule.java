package uk.org.fyodor.junit;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static org.junit.rules.RuleChain.outerRule;

public final class FyodorTestRule implements TestRule {

    private final RuleChain delegate;

    public FyodorTestRule() {
        this(() -> Timekeeper.current().zonedDateTime());
    }

    private FyodorTestRule(final Generator<ZonedDateTime> currentDateTimeAndZone) {
        this.delegate = outerRule(new SeedRule())
                .around(new TimekeeperRule(currentDateTimeAndZone));
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
        return new FyodorTestRule(() -> clock.instant().atZone(clock.getZone()));
    }

    public static FyodorTestRule withCurrentDate(final LocalDate currentDate) {
        return withCurrentDate(() -> currentDate);
    }

    public static FyodorTestRule withCurrentDate(final Generator<LocalDate> currentDate) {
        return new FyodorTestRule(() -> {
            final LocalDate date = currentDate.next();
            return ZonedDateTime.of(date, Timekeeper.current().time(), Timekeeper.current().zone());
        });
    }

    public static FyodorTestRule withCurrentTime(final LocalTime currentTime) {
        return withCurrentTime(() -> currentTime);
    }

    public static FyodorTestRule withCurrentTime(final Generator<LocalTime> currentTime) {
        return new FyodorTestRule(() -> {
            final LocalTime time = currentTime.next();
            return ZonedDateTime.of(Timekeeper.current().date(), time, Timekeeper.current().zone());
        });
    }

    public static FyodorTestRule withCurrentDateAndTime(final LocalDateTime currentDateTime) {
        return withCurrentDateAndTime(() -> currentDateTime);
    }

    public static FyodorTestRule withCurrentDateAndTime(final Generator<LocalDateTime> currentDateTime) {
        return new FyodorTestRule(() -> {
            final LocalDateTime dateTime = currentDateTime.next();
            return ZonedDateTime.of(dateTime, Timekeeper.current().zone());
        });
    }

    public static FyodorTestRule withCurrentDateTimeAndZone(final ZonedDateTime currentDateTimeAndZone) {
        return withCurrentDateTimeAndZone(() -> currentDateTimeAndZone);
    }

    public static FyodorTestRule withCurrentDateTimeAndZone(final Generator<ZonedDateTime> currentDateTimeAndZone) {
        return new FyodorTestRule(currentDateTimeAndZone);
    }
}
