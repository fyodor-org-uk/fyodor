package com.fyodor.random;

import org.junit.Test;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;
import java.util.Random;

import static com.fyodor.random.RandomValuesProvider.seed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.EMPTY;
import static org.junit.runner.Description.createTestDescription;

public final class SeedRuleTest {

    private final SeedRule seedRule = new SeedRule();

    @Test
    public void doesNotSetNextSeedWhenTheTestDoesNotHaveAnAnnotatedSeedValue() {
        final long initialSeed = seed().current();

        seedRule.starting(test(StandardTestClass.class));

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void setsNextSeedFromTestMethodAnnotation() {
        final long seedForTestMethod = new Random().nextLong();

        seedRule.starting(test(SeedRuleTest.class, seedForTestMethod));

        assertThat(seed().current()).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsNextSeedFromTestClassAnnotation() {
        seedRule.starting(test(SeededTestClass.class));

        assertThat(seed().current()).isEqualTo(1234567890);
    }

    @Test
    public void seedFromTestMethodAnnotationTakesPrecedenceOverTestClassAnnotation() {
        final long seedForTestMethod = new Random().nextLong();

        seedRule.starting(test(StandardTestClass.class, seedForTestMethod));

        assertThat(seed().current()).isEqualTo(seedForTestMethod);
    }

    @Test
    public void failingTestThrowsAnotherExceptionReportingTheCurrentSeed() {
        final long currentSeed = seed().current();

        long failingSeed = 0;

        try {
            seedRule.failed(new Throwable(), Description.EMPTY);
        } catch (final FailedWithSeedException fwse) {
            failingSeed = fwse.seed();
        }

        assertThat(failingSeed).isEqualTo(currentSeed);
    }

    @Test
    public void revertsToPreviousSeedWhenTestFinishes() {
        final long initialSeed = seed().current();

        seedRule.starting(test(SeededTestClass.class, new Random().nextLong()));
        seedRule.finished(EMPTY);

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