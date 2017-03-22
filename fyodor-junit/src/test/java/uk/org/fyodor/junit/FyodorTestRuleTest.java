package uk.org.fyodor.junit;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.generators.time.CurrentDate;
import uk.org.fyodor.generators.time.CurrentTime;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.random.RandomSourceProvider;
import uk.org.fyodor.random.Seed;

import java.lang.annotation.Annotation;
import java.time.*;
import java.util.function.Supplier;

import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.runner.Description.createTestDescription;
import static uk.org.fyodor.generators.time.Timekeeper.currentInstant;
import static uk.org.fyodor.junit.TimeFactory.Clocks.utcClockOf;
import static uk.org.fyodor.junit.TimeFactory.Instants.utcInstantOf;

public final class FyodorTestRuleTest {

    private final CapturingStatement<Instant> capturingTime = new CapturingStatement<>(() -> currentInstant());
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
    public void setsCauseOfFailingTest() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        try {
            new FyodorSeedRule()
                    .apply(failingTest(() -> new Exception("this is the top-level exception with no cause")), test())
                    .evaluate();

            fail("This test should throw an exception");
        } catch (final Exception causeOfFailingTest) {
            final FailedWithSeedException cause = (FailedWithSeedException) causeOfFailingTest.getCause();
            assertThat(cause.seed()).isEqualTo(initialSeed);
        }
    }

    @Test
    public void setsCauseOfFailingTestWhenFailureAlsoHasCause() throws Throwable {
        final long initialSeed = RandomSourceProvider.seed().current();

        try {
            new FyodorSeedRule()
                    .apply(failingTest(() -> new Exception("this is the top-level exception", new Exception("and this is the cause"))), test())
                    .evaluate();

            fail("This test should throw an exception");
        } catch (final Exception causeOfFailingTest) {
            final FailedWithSeedException cause = (FailedWithSeedException) causeOfFailingTest.getCause().getCause();
            assertThat(cause.seed()).isEqualTo(initialSeed);
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

        Timekeeper.from(utcClockOf(initialDateTime.minusDays(1)));
        Timekeeper.from(utcClockOf(initialDateTime));

        new FyodorTimekeeperRule(RDG.localDate(), () -> LocalTime.of(10, 11, 12))
                .apply(capturingTime, test(annotatedWith(currentDate(of(2000, 1, 1)))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2000, 1, 1, 10, 11, 12));

        assertThat(currentInstant())
                .isEqualTo(utcInstantOf(initialDateTime));
    }

    @Test
    public void doesNotRevertDateAndTimeWhenTestFailsToStartBecauseOfBadDate() throws Throwable {
        final LocalDateTime initialDateTime = LocalDateTime.now();

        Timekeeper.from(utcClockOf(initialDateTime.minusDays(1)));
        Timekeeper.from(utcClockOf(initialDateTime));

        try {
            new FyodorTimekeeperRule(RDG.localDate(), RDG.localTime())
                    .apply(capturingTime, test(annotatedWith(currentDate("this-is-not-a-valid-date-string"))))
                    .evaluate();

            fail("This test should throw an exception because the date string cannot be parsed");
        } catch (final DateTimeException ignored) {
        }

        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDateTime));
    }

    @Test
    public void doesNotRevertDateAndTimeWhenTestFailsToStartBecauseOfBadTime() throws Throwable {
        final LocalDateTime initialDateTime = LocalDateTime.now();

        Timekeeper.from(utcClockOf(initialDateTime.minusDays(1)));
        Timekeeper.from(utcClockOf(initialDateTime));

        try {
            new FyodorTimekeeperRule(RDG.localDate(), RDG.localTime())
                    .apply(capturingTime, test(annotatedWith(currentTime("this-is-not-a-valid-time-string"))))
                    .evaluate();

            fail("This test should throw an exception because the time string cannot be parsed");
        } catch (final DateTimeException ignored) {
        }

        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDateTime));
    }

    @Test
    public void classLevelCurrentDateAnnotationOverridesDateGeneratorConstructorArgument() throws Throwable {
        new FyodorTimekeeperRule(RDG.localDate(), () -> LocalTime.of(0, 1, 2))
                .apply(capturingTime, test(ClassLevelCurrentDateAnnotation.class))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2010, 1, 1, 0, 1, 2));
    }

    @Test
    public void methodLevelCurrentDateAnnotationOverridesClassLevelAnnotation() throws Throwable {
        new FyodorTimekeeperRule(RDG.localDate(), () -> LocalTime.of(23, 59, 59))
                .apply(capturingTime, test(ClassLevelCurrentDateAnnotation.class, annotatedWith(currentDate(of(2011, 2, 2)))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2011, 2, 2, 23, 59, 59));
    }

    @Test
    public void classLevelCurrentTimeAnnotationOverridesTimeGeneratorConstructorArgument() throws Throwable {
        new FyodorTimekeeperRule(() -> of(2010, 1, 1), RDG.localTime())
                .apply(capturingTime, test(ClassLevelCurrentTimeAnnotation.class))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2010, 1, 1, 10, 10, 10));
    }

    @Test
    public void methodLevelCurrentTimeAnnotationOverridesClassLevelAnnotation() throws Throwable {
        new FyodorTimekeeperRule(() -> of(1999, 12, 31), RDG.localTime())
                .apply(capturingTime, test(ClassLevelCurrentTimeAnnotation.class, annotatedWith(currentTime(LocalTime.of(15, 15, 15)))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(1999, 12, 31, 15, 15, 15));
    }

    @Test
    public void classLevelCurrentTimeAnnotationAndMethodLevelCurrentDateAnnotation() throws Throwable {
        new FyodorTimekeeperRule(RDG.localDate(), RDG.localTime())
                .apply(capturingTime, test(ClassLevelCurrentTimeAnnotation.class, annotatedWith(currentDate(LocalDate.of(2015, 6, 6)))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2015, 6, 6, 10, 10, 10));
    }

    @Test
    public void classLevelCurrentDateAnnotationAndMethodLevelCurrentTimeAnnotation() throws Throwable {
        new FyodorTimekeeperRule(RDG.localDate(), RDG.localTime())
                .apply(capturingTime, test(ClassLevelCurrentDateAnnotation.class, annotatedWith(currentTime(LocalTime.of(12, 12, 12)))))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(2010, 1, 1, 12, 12, 12));
    }

    @Test
    public void classLevelCurrentDateAndCurrentTimeAnnotations() throws Throwable {
        new FyodorTimekeeperRule(RDG.localDate(), RDG.localTime())
                .apply(capturingTime, test(ClassLevelCurrentDateAndCurrentTimeAnnotations.class))
                .evaluate();

        assertThat(capturingTime.captured())
                .isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));
    }

    @Test
    public void withCurrentDateGenerator() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDate(() -> of(2000, 1, 1))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(2000, 1, 1, initialTime));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentDate() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDate(of(2000, 1, 1))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(2000, 1, 1, initialTime));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentTimeGenerator() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentTime(() -> LocalTime.of(3, 4, 5))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(initialDate, 3, 4, 5));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentTime() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentTime(LocalTime.of(23, 59, 59))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(initialDate, 23, 59, 59));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentDateAndTimeGenerator() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDateAndTime(() -> LocalDateTime.of(1999, 12, 31, 23, 59, 59))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(1999, 12, 31, 23, 59, 59));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void withCurrentDateAndTime() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.withCurrentDateAndTime(LocalDateTime.of(2017, 3, 29, 12, 13, 14))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(2017, 3, 29, 12, 13, 14));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void fromClock() throws Throwable {
        final LocalDate initialDate = LocalDate.now();
        final LocalTime initialTime = LocalTime.now();
        Timekeeper.from(utcClockOf(initialDate.atTime(initialTime)));

        FyodorTestRule.from(utcClockOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0)))
                .apply(capturingTime, test())
                .evaluate();

        assertThat(capturingTime.captured()).isEqualTo(utcInstantOf(2000, 1, 1, 0, 0, 0));
        assertThat(currentInstant()).isEqualTo(utcInstantOf(initialDate.atTime(initialTime)));
    }

    @Test
    public void ruleProvidesConfiguredTimeDuringTest() throws Throwable {
        final LocalTime time = LocalTime.of(12, 34, 34);
        final FyodorTestRule rule = FyodorTestRule.withCurrentTime(time);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.currentTime());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(time);
    }

    @Test
    public void ruleProvidesConfiguredDateDuringTest() throws Throwable {
        final LocalDate date = LocalDate.of(1981, 12, 6);
        final FyodorTestRule rule = FyodorTestRule.withCurrentDate(date);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.currentDate());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(date);
    }

    @Test
    public void ruleProvidesConfiguredDateTimeDuringTest() throws Throwable {
        final LocalDateTime dateTime = LocalDateTime.of(1981, 12, 6, 12, 56, 34);
        final FyodorTestRule rule = FyodorTestRule.withCurrentDateAndTime(dateTime);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.currentDateTime());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(dateTime);
    }

    @Test
    public void ruleProvidesConfiguredInstantDuringTest() throws Throwable {
        final LocalDateTime dateTime = LocalDateTime.of(1981, 12, 6, 12, 56, 34);
        final FyodorTestRule rule = FyodorTestRule.withCurrentDateAndTime(dateTime);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.currentInstant());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(utcInstantOf(dateTime));
    }

    @Test
    public void ruleProvidesConfiguredClockDuringTest() throws Throwable {
        final Clock clock = utcClockOf(LocalDateTime.of(1981, 12, 6, 12, 56, 34));
        final FyodorTestRule rule = FyodorTestRule.from(clock);

        final CapturingStatement capturingCurrentTime = new CapturingStatement<>(() -> rule.currentClock());
        rule.apply(capturingCurrentTime, test()).evaluate();

        assertThat(capturingCurrentTime.captured()).isEqualTo(clock);
    }

    private static Annotation annotatedWith(final Annotation annotation) {
        return annotation;
    }

    private static CurrentDate currentDate(final LocalDate date) {
        return currentDate(date.toString());
    }

    private static CurrentDate currentDate(final String date) {
        return new CurrentDate() {
            @Override
            public String value() {
                return date;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return CurrentDate.class;
            }
        };
    }

    private static CurrentTime currentTime(final LocalTime time) {
        return currentTime(time.toString());
    }

    private static CurrentTime currentTime(final String time) {
        return new CurrentTime() {
            @Override
            public String value() {
                return time;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return CurrentTime.class;
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
    @CurrentTime("10:10:10")
    public static final class ClassLevelCurrentTimeAnnotation {
    }

    @SuppressWarnings("WeakerAccess")
    @CurrentDate("2010-01-01")
    public static final class ClassLevelCurrentDateAnnotation {
    }

    @SuppressWarnings("WeakerAccess")
    @CurrentDate("1999-12-31")
    @CurrentTime("23:59:59")
    public static final class ClassLevelCurrentDateAndCurrentTimeAnnotations {
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