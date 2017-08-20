package uk.org.fyodor.random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.runner.Description.createTestDescription;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

public final class FyodorTestRuleTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final FyodorTestRule underTest = new FyodorTestRule();

    @Test
    public void doesNotSetNextSeedWhenTheTestDoesNotHaveAnAnnotatedSeedValue() throws Throwable {
        final long initialSeed = seed().current();

        final CapturingStatement captured = new CapturingStatement();
        underTest.apply(captured, test(StandardTestClass.class)).evaluate();

        assertThat(captured.seed).isEqualTo(initialSeed);
    }

    @Test
    public void setsNextSeedFromTestMethodAnnotation() throws Throwable {
        final long seedForTestMethod = new Random().nextLong();

        final CapturingStatement captured = new CapturingStatement();
        underTest.apply(captured, test(StandardTestClass.class, seedForTestMethod)).evaluate();

        assertThat(captured.seed).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsNextSeedFromTestClassAnnotation() throws Throwable {
        final CapturingStatement captured = new CapturingStatement();
        underTest.apply(captured, test(SeededTestClass.class)).evaluate();

        assertThat(captured.seed).isEqualTo(1234567890);
    }

    @Test
    public void seedFromTestMethodAnnotationTakesPrecedenceOverTestClassAnnotation() throws Throwable {
        final long seedForTestMethod = new Random().nextLong();

        final CapturingStatement captured = new CapturingStatement();
        underTest.apply(captured, test(SeededTestClass.class, seedForTestMethod)).evaluate();

        assertThat(captured.seed).isEqualTo(seedForTestMethod);
    }

    @Test
    public void setsCauseOfFailingTestToExceptionWithSeed() throws Throwable {
        final long currentSeed = seed().current();
        final Throwable causeOfFailingTest = new AssertionError();

        thrown.expect(FailedWithSeed.class);
        thrown.expectCause(equalTo(causeOfFailingTest));
        thrown.expectMessage("Test failed with seed " + currentSeed);

        underTest.apply(testFailsWith(causeOfFailingTest), test(StandardTestClass.class)).evaluate();
    }

    @Test
    public void revertsToPreviousSeedWhenTestFinishes() throws Throwable {
        final long initialSeed = seed().current();

        underTest.apply(successfulTest(), test(SeededTestClass.class, new Random().nextLong())).evaluate();

        assertThat(seed().current()).isEqualTo(initialSeed);
    }

    private static Statement testFailsWith(final Throwable throwable) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw throwable;
            }
        };
    }

    private static Statement successfulTest() {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                //Do nothing and be successful!
            }
        };
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

    private static final class CapturingStatement extends Statement {

        private long seed;

        @Override
        public void evaluate() throws Throwable {
            this.seed = seed().current();
        }
    }

    @Seed(1234567890)
    static final class SeededTestClass {
    }

    static final class StandardTestClass {
    }
}