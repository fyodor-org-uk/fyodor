package uk.org.fyodor.junit;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.random.RandomSourceProvider;
import uk.org.fyodor.testapi.*;

import java.lang.annotation.Annotation;
import java.time.*;
import java.util.function.Supplier;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.runner.Description.createTestDescription;
import static uk.org.fyodor.generators.RDG.*;
import static uk.org.fyodor.generators.time.Timekeeper.current;
import static uk.org.fyodor.junit.TimeFactory.Clocks.utcClockOf;
import static uk.org.fyodor.junit.TimeFactory.Instants.utcInstantOf;

public final class FyodorTestRuleTest {

    private final CapturingStatement<Instant> capturingTime = new CapturingStatement<>(() -> current().instant());
    private final CapturingStatement<ZoneId> capturingZone = new CapturingStatement<>(() -> current().zone());
    private final CapturingStatement<Long> capturingSeed = new CapturingStatement<>(() -> RandomSourceProvider.seed().current());

    @Test
    public void methodAnnotatedWithSeed() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        FyodorTestRule.fyodorTestRule()
                .apply(capturingSeed, test(annotatedWith(seed(56789L))))
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(56789L);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void classAnnotatedWithSeed() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        FyodorTestRule.fyodorTestRule()
                .apply(capturingSeed, test(SeededTestClass.class))
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(1234567890L);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void methodLevelSeedAnnotationOverridesClassLevelSeedAnnotation() throws Throwable {
        FyodorTestRule.fyodorTestRule()
                .apply(capturingSeed, test(SeededTestClass.class, annotatedWith(seed(123456L))))
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(123456L);
    }

    @Test
    public void doesNotSetNextSeedWhenTheTestDoesNotHaveAnAnnotatedSeedValue() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        new FyodorSeedRule()
                .apply(capturingSeed, test())
                .evaluate();

        assertThat(capturingSeed.captured()).isEqualTo(initialSeed);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void throwsFailedWithSeedExceptionWhenTestFailsWithException() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        final Exception originalException = new NullPointerException();
        final Exception exceptionCausingTestToFail = new IllegalArgumentException("this is the top-level exception", originalException);

        try {
            new FyodorSeedRule()
                    .apply(failingTest(() -> exceptionCausingTestToFail), test())
                    .evaluate();

            fail("This test should have thrown an exception");
        } catch (final FailedWithSeed exception) {
            assertThat(exception.seed()).isEqualTo(initialSeed);
            assertThat(exception).hasCause(exceptionCausingTestToFail);
        }
    }

    @Test
    public void revertsToPreviousSeedWhenTestFinishes() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        new FyodorSeedRule()
                .apply(capturingSeed, test(annotatedWith(seed(567123L))))
                .evaluate();

        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void doesNotRevertSeedIfSeedWasNotSetByTest() throws Throwable {
        final long firstSeed = 1L;
        final long secondSeed = 2L;

        RandomSourceProvider.seed().next(firstSeed);
        RandomSourceProvider.seed().next(secondSeed);

        new FyodorSeedRule()
                .apply(capturingSeed, test())
                .evaluate();

        assertThat(RandomSourceProvider.seed().current()).isEqualTo(secondSeed);
    }

    @Test
    public void setsDateAndTimeDuringTestAndRevertsWhenTestHasFinished() throws Throwable {
        final LocalDateTime initialDateTime = LocalDateTime.now();
        Timekeeper.from(utcClockOf(initialDateTime));

        new FyodorTimekeeperRule(() -> ZonedDateTime.of(localDate().next().atTime(LocalTime.of(10, 11, 12)), UTC))
                .apply(capturingTime, test(annotatedWith(currentDate(LocalDate.of(2000, 1, 1)))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2000, 1, 1, 10, 11, 12));

        assertThat(current().instant())
                .isEqualTo(utcInstantOf(initialDateTime));
    }

    @Test
    public void doesNotRevertDateAndTimeWhenTestFailsToStartBecauseOfBadDate() throws Throwable {
        final LocalDateTime initialDateTime = LocalDateTime.now();
        Timekeeper.from(utcClockOf(initialDateTime));

        try {
            new FyodorTimekeeperRule(() -> localDateTime().next().atZone(UTC))
                    .apply(capturingTime, test(annotatedWith(currentDate("this-is-not-a-valid-date-string"))))
                    .evaluate();

            fail("This test should throw an exception because the date string cannot be parsed");
        } catch (final DateTimeException ignored) {
        }

        assertThat(current().instant()).isEqualTo(utcInstantOf(initialDateTime));
    }

    @Test
    public void doesNotRevertDateAndTimeWhenTestFailsToStartBecauseOfBadTime() throws Throwable {
        final LocalDateTime initialDateTime = LocalDateTime.now();
        Timekeeper.from(utcClockOf(initialDateTime));

        try {
            new FyodorTimekeeperRule(() -> localDateTime().next().atZone(UTC))
                    .apply(capturingTime, test(annotatedWith(currentTime("this-is-not-a-valid-time-string"))))
                    .evaluate();

            fail("This test should throw an exception because the time string cannot be parsed");
        } catch (final DateTimeException ignored) {
        }

        assertThat(current().instant()).isEqualTo(utcInstantOf(initialDateTime));
    }

    @Test
    public void classLevelCurrentDateAnnotationOverridesDateGeneratorConstructorArgument() throws Throwable {
        final LocalTime initialTime = LocalTime.of(0, 1, 2);

        new FyodorTimekeeperRule(() -> localDate().next().atTime(initialTime).atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentDateAnnotation.class))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2010, 1, 1, initialTime));
    }

    @Test
    public void methodLevelCurrentDateAnnotationOverridesClassLevelAnnotation() throws Throwable {
        final LocalDate initialDate = localDate().next();
        final LocalTime initialTime = LocalTime.of(23, 59, 59);
        final LocalDate annotatedDate = LocalDate.of(2011, 2, 2);

        new FyodorTimekeeperRule(() -> initialDate.atTime(initialTime).atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentDateAnnotation.class, annotatedWith(currentDate(annotatedDate))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(annotatedDate, initialTime));
    }

    @Test
    public void classLevelCurrentTimeAnnotationOverridesTimeGeneratorConstructorArgument() throws Throwable {
        final LocalDate initialDate = LocalDate.of(2010, 1, 1);
        final LocalTime initialTime = localTime().next();

        new FyodorTimekeeperRule(() -> initialDate.atTime(initialTime).atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentTimeAnnotation.class))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(initialDate, 10, 10, 10));
    }

    @Test
    public void methodLevelCurrentTimeAnnotationOverridesClassLevelAnnotation() throws Throwable {
        final LocalDate initialDate = LocalDate.of(1999, 12, 31);
        final LocalTime initialTime = localTime().next();
        final LocalTime annotatedTime = LocalTime.of(15, 15, 15);

        new FyodorTimekeeperRule(() -> initialDate.atTime(initialTime).atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentTimeAnnotation.class, annotatedWith(currentTime(annotatedTime))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(initialDate, annotatedTime));
    }

    @Test
    public void classLevelCurrentTimeAnnotationAndMethodLevelCurrentDateAnnotation() throws Throwable {
        final LocalDate annotatedDate = LocalDate.of(2015, 6, 6);

        new FyodorTimekeeperRule(() -> localDateTime().next().atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentTimeAnnotation.class, annotatedWith(currentDate(annotatedDate))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(annotatedDate, 10, 10, 10));
    }

    @Test
    public void classLevelCurrentDateAnnotationAndMethodLevelCurrentTimeAnnotation() throws Throwable {
        final LocalTime annotatedTime = LocalTime.of(12, 12, 12);

        new FyodorTimekeeperRule(() -> localDateTime().next().atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentDateAnnotation.class, annotatedWith(currentTime(annotatedTime))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2010, 1, 1, annotatedTime));
    }

    @Test
    public void classLevelCurrentDateAndCurrentTimeAnnotations() throws Throwable {
        new FyodorTimekeeperRule(() -> localDateTime().next().atZone(UTC))
                .apply(capturingTime, test(ClassLevelCurrentDateAndCurrentTimeAnnotations.class))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));
    }

    @Test
    public void classLevelCurrentZoneAnnotationOverridesZoneInConstructorArgument() throws Throwable {
        final ZoneId zoneInRule = zoneId().next();
        new FyodorTimekeeperRule(() -> localDateTime().next().atZone(zoneInRule))
                .apply(capturingZone, test(ClassLevelCurrentZoneAnnotation.class))
                .evaluate();

        assertThat(capturingZone.captured())
                .isEqualTo(ZoneId.of("America/Los_Angeles"));
    }

    @Test
    public void methodLevelCurrentZoneAnnotationOverridesClassLevelAnnotation() throws Throwable {
        final ZoneId methodLevelZone = ZoneId.of("America/Chicago");

        new FyodorTimekeeperRule(() -> localDateTime().next().atZone(zoneId().next()))
                .apply(capturingZone, test(ClassLevelCurrentZoneAnnotation.class, annotatedWith(currentZone(methodLevelZone))))
                .evaluate();

        assertThat(capturingZone.captured())
                .isEqualTo(methodLevelZone);
    }

    @Test
    public void withCurrentDateGenerator() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDate(() -> LocalDate.of(2000, 1, 1))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(2000, 1, 1, initialTime));
        assertThat(current().instant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentDate() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDate(LocalDate.of(2000, 1, 1))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured())
                .describedAs("The date during rule evaluation should be the supplied current date")
                .isEqualTo(utcInstantOf(2000, 1, 1, initialTime));

        assertThat(current().instant())
                .describedAs("The date after rule evaluation should have reverted to the initial date")
                .isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentTimeGenerator() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentTime(() -> LocalTime.of(3, 4, 5))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured())
                .describedAs("The time during rule evaluation should be the supplied current time")
                .isEqualTo(utcInstantOf(initialDate, 3, 4, 5));

        assertThat(current().instant())
                .describedAs("The time after rule evaluation should have reverted to the initial time")
                .isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentTime() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentTime(LocalTime.of(23, 59, 59))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured())
                .describedAs("The time during rule evaluation should be the supplied current time")
                .isEqualTo(utcInstantOf(initialDate, 23, 59, 59));

        assertThat(current().instant())
                .describedAs("The time after rule evaluation should have reverted to the initial time")
                .isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentDateAndTimeGenerator() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDateAndTime(() -> LocalDateTime.of(1999, 12, 31, 23, 59, 59))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured())
                .describedAs("The date and time during rule evaluation should be the supplied current date and time")
                .isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));

        assertThat(current().instant())
                .describedAs("The date and time after rule evaluation should have reverted to the initial date and time")
                .isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentDateAndTime() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDateAndTime(LocalDateTime.of(1999, 12, 31, 12, 13, 14))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured())
                .describedAs("The date and time during rule evaluation should be the supplied current date and time")
                .isEqualTo(utcInstantOf(1999, 12, 31, 12, 13, 14));

        assertThat(current().instant())
                .describedAs("The date and time after rule evaluation should have reverted to the initial date and time")
                .isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void fromClock() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.from(utcClockOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0)))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured())
                .describedAs("The clock during rule evaluation should be the supplied clock")
                .isEqualTo(utcInstantOf(2000, 1, 1, 0, 0, 0));

        assertThat(current().instant())
                .describedAs("The clock after rule evaluation should be the initial clock")
                .isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void ruleProvidesConfiguredTimeDuringTest() throws Throwable {
        final LocalTime time = LocalTime.of(12, 34, 34);
        final FyodorTestRule rule = FyodorTestRule.withCurrentTime(time);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.current().time());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(time);
    }

    @Test
    public void ruleProvidesConfiguredDateDuringTest() throws Throwable {
        final LocalDate date = LocalDate.of(1981, 12, 6);
        final FyodorTestRule rule = FyodorTestRule.withCurrentDate(date);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.current().date());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(date);
    }

    @Test
    public void ruleProvidesConfiguredDateTimeDuringTest() throws Throwable {
        final LocalDateTime dateTime = LocalDateTime.of(1981, 12, 6, 12, 56, 34);
        final FyodorTestRule rule = FyodorTestRule.withCurrentDateAndTime(dateTime);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.current().dateTime());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(dateTime);
    }

    @Test
    public void ruleProvidesConfiguredInstantDuringTest() throws Throwable {
        final LocalDateTime dateTime = LocalDateTime.of(1981, 12, 6, 12, 56, 34);
        final FyodorTestRule rule = FyodorTestRule.withCurrentDateAndTime(dateTime);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.current().instant());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(utcInstantOf(dateTime));
    }

    @Test
    public void ruleProvidesConfiguredClockDuringTest() throws Throwable {
        final Clock clock = utcClockOf(LocalDateTime.of(1981, 12, 6, 12, 56, 34));
        final FyodorTestRule rule = FyodorTestRule.from(clock);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.current().clock());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(clock);
    }

    private static Generator<ZoneId> zoneId() {
        final Generator<String> zoneIdGenerator = RDG.value(ZoneId.getAvailableZoneIds());
        return () -> ZoneId.of(zoneIdGenerator.next());
    }

    private static Annotation annotatedWith(final Annotation annotation) {
        return annotation;
    }

    private static AtDate currentDate(final LocalDate date) {
        return currentDate(date.toString());
    }

    private static AtDate currentDate(final String date) {
        return new AtDate() {
            @Override
            public String value() {
                return date;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtDate.class;
            }
        };
    }

    private static AtTime currentTime(final LocalTime time) {
        return currentTime(time.toString());
    }

    private static AtTime currentTime(final String time) {
        return new AtTime() {
            @Override
            public String value() {
                return time;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtTime.class;
            }
        };
    }

    private static AtZone currentZone(final ZoneId zone) {
        return new AtZone() {
            @Override
            public String value() {
                return zone.getId();
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtZone.class;
            }
        };
    }

    private static Seed seed(final long seed) {
        return new Seed() {
            @Override
            public long value() {
                return seed;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Seed.class;
            }
        };
    }

    private static Description test() {
        return test(StandardTestClass.class);
    }

    private static Description test(final Class<?> testClass) {
        return createTestDescription(testClass, "test");
    }

    private static Description test(final Annotation annotation) {
        return test(StandardTestClass.class, annotation);
    }

    private static Description test(final Class<?> testClass, final Annotation annotation) {
        return createTestDescription(testClass, "test", annotation);
    }

    private static Statement failingTest(final Supplier<Throwable> reasonForFailure) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw reasonForFailure.get();
            }
        };
    }

    @SuppressWarnings("WeakerAccess")
    public static final class StandardTestClass {
    }

    @SuppressWarnings("WeakerAccess")
    @Seed(1234567890)
    public static final class SeededTestClass {
    }

    @SuppressWarnings("WeakerAccess")
    @AtTime("10:10:10")
    public static final class ClassLevelCurrentTimeAnnotation {
    }

    @SuppressWarnings("WeakerAccess")
    @AtDate("2010-01-01")
    public static final class ClassLevelCurrentDateAnnotation {
    }

    @SuppressWarnings("WeakerAccess")
    @AtDate("1999-12-31")
    @AtTime("23:59:59")
    public static final class ClassLevelCurrentDateAndCurrentTimeAnnotations {
    }

    @SuppressWarnings("WeakerAccess")
    @AtZone("America/Los_Angeles")
    public static final class ClassLevelCurrentZoneAnnotation {
    }

    static final class CapturingStatement<T> extends Statement {

        private final Supplier<T> supplierOfT;
        private T captured;

        CapturingStatement(final Supplier<T> supplierOfT) {
            this.supplierOfT = supplierOfT;
        }

        T captured() {
            return captured;
        }

        @Override
        public void evaluate() throws Throwable {
            this.captured = supplierOfT.get();
        }
    }
}