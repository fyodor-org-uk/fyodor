package uk.org.fyodor.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.*;
import uk.org.fyodor.testapi.SeededFyodorTestCallback.SeedController;
import uk.org.fyodor.testapi.TimekeeperFyodorTestCallback.TimeController;

import java.lang.annotation.Annotation;
import java.time.*;
import java.util.Optional;

import static uk.org.fyodor.random.RandomSourceProvider.seed;

public final class FyodorTestRule implements TestRule {

    private final SeededFyodorTestCallback seedCallback;
    private final TimekeeperFyodorTestCallback timekeeperCallback;

    public FyodorTestRule() {
        this(() -> Timekeeper.current().zonedDateTime());
    }

    private FyodorTestRule(final Generator<ZonedDateTime> currentDateTimeAndZone) {
        this.seedCallback = new SeededFyodorTestCallback(seedController());
        this.timekeeperCallback = new TimekeeperFyodorTestCallback(timeController(currentDateTimeAndZone));
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final FyodorTest test = fyodorTestOf(description);

                try {
                    seedCallback.beforeTestExecution(test);
                    timekeeperCallback.beforeTestExecution(test);
                    base.evaluate();
                } catch (final Throwable t) {
                    seedCallback.testFailed(test, t);
                } finally {
                    seedCallback.afterTestExecution(test);
                    timekeeperCallback.afterTestExecution(test);
                }
            }
        };
    }

    public Temporality current() {
        return Timekeeper.current();
    }

    private static FyodorTest fyodorTestOf(final Description description) {
        return new FyodorTest() {
            @Override
            public Annotatable testMethod() {
                return new Annotatable() {
                    @Override
                    public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                        return Optional.ofNullable(description.getAnnotation(annotationClass));
                    }
                };
            }

            @Override
            public Annotatable testClass() {
                return new Annotatable() {
                    @Override
                    public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                        return Optional.ofNullable(description.getTestClass().getAnnotation(annotationClass));
                    }
                };
            }
        };
    }

    private static TimeController timeController(final Generator<ZonedDateTime> currentDateTime) {
        return new TimeController() {
            @Override
            public LocalDate currentDate() {
                return currentDateTime.next().toLocalDate();
            }

            @Override
            public LocalTime currentTime() {
                return currentDateTime.next().toLocalTime();
            }

            @Override
            public ZoneId currentZone() {
                return currentDateTime.next().getZone();
            }

            @Override
            public void setDateTimeAndZone(final ZonedDateTime dateTimeAndZone) {
                Timekeeper.from(Clock.fixed(dateTimeAndZone.toInstant(), dateTimeAndZone.getZone()));
            }

            @Override
            public void revertToPreviousDateTimeAndZone() {
                Timekeeper.rollback();
            }
        };
    }

    private static SeedController seedController() {
        return new SeedController() {
            @Override
            public long currentSeed() {
                return seed().current();
            }

            @Override
            public void setCurrentSeed(final long currentSeed) {
                seed().next(currentSeed);
            }

            @Override
            public void revertToPreviousSeed() {
                seed().previous();
            }
        };
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
