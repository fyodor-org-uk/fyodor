package com.fyodor.random;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.lang.annotation.Annotation;
import java.util.Random;

import static com.fyodor.random.RandomValuesProvider.seed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.EMPTY;
import static org.junit.runner.Description.createTestDescription;

public class FyodorRunListenerTest {

    private final FyodorRunListener fyodorRunListener = new FyodorRunListener();

    @Test
    public void doesNotSetNextSeedWhenTheTestDoesNotHaveAnAnnotatedSeedValue() throws Exception {
        final long initialSeed = seed().current();

        fyodorRunListener.testStarted(test(StandardTestClass.class));

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void setsNextSeedFromTestMethodAnnotation() throws Exception {
        final long seedForTestMethod = new Random().nextLong();

        fyodorRunListener.testStarted(test(FyodorTestRuleTest.class, seedForTestMethod));

        assertThat(seed().current()).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsNextSeedFromTestClassAnnotation() throws Exception {
        fyodorRunListener.testStarted(test(SeededTestClass.class));

        assertThat(seed().current()).isEqualTo(1234567890);
    }

    @Test
    public void seedFromTestMethodAnnotationTakesPrecedenceOverTestClassAnnotation() throws Exception {
        final long seedForTestMethod = new Random().nextLong();

        fyodorRunListener.testStarted(test(StandardTestClass.class, seedForTestMethod));

        assertThat(seed().current()).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsCauseOfFailingTestToExceptionWithSeed() throws Exception {
        final long currentSeed = seed().current();

        final Throwable causeOfFailingTest = new AssertionError();
        final Throwable cause1 = new Throwable();
        causeOfFailingTest.initCause(cause1);
        final Throwable cause2 = new Throwable();
        cause1.initCause(cause2);

        fyodorRunListener.testFailure(new Failure(EMPTY, causeOfFailingTest));

        final FailedWithSeedException cause = (FailedWithSeedException) cause2.getCause();
        assertThat(cause.seed()).isEqualTo(currentSeed);
    }

    @Test
    public void setsRootCauseOfFailingTestToExceptionWithSeed() throws Exception {
        final long currentSeed = seed().current();
        final RuntimeException exception = new RuntimeException();

        fyodorRunListener.testFailure(new Failure(EMPTY, exception));

        final FailedWithSeedException cause = (FailedWithSeedException) exception.getCause();
        assertThat(cause.seed()).isEqualTo(currentSeed);
    }

    @Test
    public void revertsToPreviousSeedWhenTestFinishes() throws Exception {
        final long initialSeed = seed().current();

        fyodorRunListener.testStarted(test(SeededTestClass.class, new Random().nextLong()));
        fyodorRunListener.testFinished(EMPTY);

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