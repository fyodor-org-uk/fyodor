package uk.org.fyodor.random;

import org.junit.Test;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.util.Random;

import static uk.org.fyodor.random.RandomValuesProvider.seed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.EMPTY;
import static org.junit.runner.Description.createTestDescription;

public final class FyodorTestRuleTest {

    private final FyodorTestRule fyodorTestRule = new FyodorTestRule();

    @Test
    public void doesNotSetNextSeedWhenTheTestDoesNotHaveAnAnnotatedSeedValue() {
        final long initialSeed = seed().current();

        fyodorTestRule.starting(test(StandardTestClass.class));

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void setsNextSeedFromTestMethodAnnotation() {
        final long seedForTestMethod = new Random().nextLong();

        fyodorTestRule.starting(test(FyodorTestRuleTest.class, seedForTestMethod));

        assertThat(seed().current()).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsNextSeedFromTestClassAnnotation() {
        fyodorTestRule.starting(test(SeededTestClass.class));

        assertThat(seed().current()).isEqualTo(1234567890);
    }

    @Test
    public void seedFromTestMethodAnnotationTakesPrecedenceOverTestClassAnnotation() {
        final long seedForTestMethod = new Random().nextLong();

        fyodorTestRule.starting(test(StandardTestClass.class, seedForTestMethod));

        assertThat(seed().current()).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsCauseOfFailingTestToExceptionWithSeed() {
        final long currentSeed = seed().current();
        final Throwable causeOfFailingTest = new AssertionError();

        fyodorTestRule.failed(causeOfFailingTest, Description.EMPTY);

        final FailedWithSeedException cause = (FailedWithSeedException) causeOfFailingTest.getCause();
        assertThat(cause.seed()).isEqualTo(currentSeed);
    }

    @Test
    public void setsRootCauseOfFailingTestToExceptionWithSeed() {
        final long currentSeed = seed().current();

        final Throwable causeOfFailingTest = new AssertionError();
        final Throwable cause1 = new Throwable();
        causeOfFailingTest.initCause(cause1);
        final Throwable cause2 = new Throwable();
        cause1.initCause(cause2);

        fyodorTestRule.failed(causeOfFailingTest, Description.EMPTY);

        final FailedWithSeedException cause = (FailedWithSeedException) cause2.getCause();
        assertThat(cause.seed()).isEqualTo(currentSeed);
    }

    @Test
    public void revertsToPreviousSeedWhenTestFinishes() {
        final long initialSeed = seed().current();

        fyodorTestRule.starting(test(SeededTestClass.class, new Random().nextLong()));
        fyodorTestRule.finished(EMPTY);

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    private static Description test(final Class<?> testClass) {
        return createTestDescription(testClass, "test");
    }

    private static Description test(final Class<?> testClass, final long seedForTestMethod) {
        return createTestDescription(testClass, "test", seedAnnotation(seedForTestMethod));
    }

    private static Seed seedAnnotation(final long seed) {
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

    @Seed(1234567890)
    public static final class SeededTestClass {
    }

    public static final class StandardTestClass {
    }
}