package uk.org.fyodor.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;

import java.time.*;

import static org.junit.rules.RuleChain.outerRule;

public final class FyodorTestRule implements TestRule {

    private final TestRule delegate;

    public FyodorTestRule() {
        this.delegate = outerRule(new FyodorSeedRule())
                .around(new FyodorTimekeeperRule(() -> Timekeeper.current().zonedDateTime()));
    }

    private FyodorTestRule(final Generator<ZonedDateTime> currentDateTime) {
        this.delegate = outerRule(new FyodorSeedRule())
                .around(new FyodorTimekeeperRule(currentDateTime));
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
            return Timekeeper.current().zonedDateTime()
                    .withYear(date.getYear())
                    .withMonth(date.getMonthValue())
                    .withDayOfMonth(date.getDayOfMonth());
        });
    }

    public static FyodorTestRule withCurrentTime(final LocalTime currentTime) {
        return withCurrentTime(() -> currentTime);
    }

    public static FyodorTestRule withCurrentTime(final Generator<LocalTime> currentTime) {
        return new FyodorTestRule(() -> {
            final LocalTime time = currentTime.next();
            return Timekeeper.current().zonedDateTime()
                    .withHour(time.getHour())
                    .withMinute(time.getMinute())
                    .withSecond(time.getSecond())
                    .withNano(time.getNano());
        });
    }

    public static FyodorTestRule withCurrentDateAndTime(final LocalDateTime currentDateTime) {
        return withCurrentDateAndTime(() -> currentDateTime);
    }

    public static FyodorTestRule withCurrentDateAndTime(final Generator<LocalDateTime> currentDateTime) {
        return new FyodorTestRule(() -> {
            final LocalDateTime dateTime = currentDateTime.next();
            return Timekeeper.current().zonedDateTime()
                    .withYear(dateTime.getYear())
                    .withMonth(dateTime.getMonthValue())
                    .withDayOfMonth(dateTime.getDayOfMonth())
                    .withHour(dateTime.getHour())
                    .withMinute(dateTime.getMinute())
                    .withSecond(dateTime.getSecond())
                    .withNano(dateTime.getNano());
        });
    }
}
