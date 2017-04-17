package uk.org.fyodor.testapi;

import org.junit.Test;
import uk.org.fyodor.testapi.SeededFyodorTestCallback.SeedController;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static uk.org.fyodor.generators.RDG.longVal;
import static uk.org.fyodor.testapi.FyodorTestBuilder.fyodorTest;

public final class SeededFyodorTestCallbackTest {

    @Test
    public void setsAndRevertsSeedWhenTestMethodIsAnnotated() {
        final long initialSeed = longVal().next();
        final long nextSeed = longVal().next();
        final StubSeedController seedController = new StubSeedController(initialSeed);
        final SeededFyodorTestCallback underTest = new SeededFyodorTestCallback(seedController);

        final FyodorTest test = fyodorTest().withMethodAnnotations(seed(nextSeed)).build();

        underTest.beforeTestExecution(test);

        assertThat(seedController.seedWasSet).isTrue();
        assertThat(seedController.currentSeed()).isEqualTo(nextSeed);

        underTest.afterTestExecution(test);

        assertThat(seedController.seedWasReverted).isTrue();
        assertThat(seedController.currentSeed()).isEqualTo(initialSeed);
    }

    @Test
    public void setsAndRevertsSeedWhenTestClassIsAnnotated() {
        final long initialSeed = longVal().next();
        final long nextSeed = longVal().next();
        final StubSeedController seedController = new StubSeedController(initialSeed);
        final SeededFyodorTestCallback underTest = new SeededFyodorTestCallback(seedController);

        final FyodorTest test = fyodorTest().withClassAnnotations(seed(nextSeed)).build();

        underTest.beforeTestExecution(test);

        assertThat(seedController.seedWasSet).isTrue();
        assertThat(seedController.currentSeed()).isEqualTo(nextSeed);

        underTest.afterTestExecution(test);

        assertThat(seedController.seedWasReverted).isTrue();
        assertThat(seedController.currentSeed()).isEqualTo(initialSeed);
    }

    @Test
    public void methodSeedAnnotationHasPriorityOverClassAnnotation() {
        final long initialSeed = longVal().next();
        final long classSeed = longVal().next();
        final long methodSeed = longVal().next();

        final StubSeedController seedController = new StubSeedController(initialSeed);
        final SeededFyodorTestCallback underTest = new SeededFyodorTestCallback(seedController);

        final FyodorTest test = fyodorTest()
                .withClassAnnotations(seed(classSeed))
                .withMethodAnnotations(seed(methodSeed))
                .build();

        underTest.beforeTestExecution(test);

        assertThat(seedController.seedWasSet).isTrue();
        assertThat(seedController.currentSeed()).isEqualTo(methodSeed);

        underTest.afterTestExecution(test);

        assertThat(seedController.seedWasReverted).isTrue();
        assertThat(seedController.currentSeed()).isEqualTo(initialSeed);
    }

    @Test
    public void setsAndRevertSeedEvenWhenTestIsNotAnnotated() {
        final long initialSeed = longVal().next();
        final StubSeedController seedController = new StubSeedController(initialSeed);
        final SeededFyodorTestCallback underTest = new SeededFyodorTestCallback(seedController);

        final FyodorTest test = fyodorTest().build();

        underTest.beforeTestExecution(test);

        assertThat(seedController.currentSeed()).isEqualTo(initialSeed);
        assertThat(seedController.seedWasSet)
                .describedAs("the seed is always set, even if the test was not annotated")
                .isTrue();

        underTest.afterTestExecution(test);

        assertThat(seedController.currentSeed()).isEqualTo(initialSeed);
        assertThat(seedController.seedWasReverted)
                .describedAs("the seed should be reverted if it was set successfully")
                .isTrue();
    }

    @Test
    public void throwsFailedWithSeedWhenTestFails() throws Throwable {
        final long initialSeed = longVal().next();
        final StubSeedController seedController = new StubSeedController(initialSeed);
        final SeededFyodorTestCallback underTest = new SeededFyodorTestCallback(seedController);

        final AssertionError causeOfTestFailure = new AssertionError("Reason for test failure");
        final FailedWithSeed failedWithSeed = (FailedWithSeed) catchThrowable(() ->
                underTest.testFailed(fyodorTest().build(), causeOfTestFailure));

        assertThat(failedWithSeed.seed()).isEqualTo(initialSeed);
        assertThat(failedWithSeed).hasCause(causeOfTestFailure);
    }

    private static Seed seed(final long seed) {
        return new Seed() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Seed.class;
            }

            @Override
            public long value() {
                return seed;
            }
        };
    }

    private static final class StubSeedController implements SeedController {

        private final long initialSeed;
        private long currentSeed;
        private boolean seedWasSet;
        private boolean seedWasReverted;

        private StubSeedController(final long initialSeed) {
            this.initialSeed = initialSeed;
            this.currentSeed = initialSeed;
        }

        @Override
        public long currentSeed() {
            return currentSeed;
        }

        @Override
        public void setCurrentSeed(final long currentSeed) {
            if (seedWasSet) {
                throw new IllegalStateException("Seed has already been set");
            }

            this.currentSeed = currentSeed;
            this.seedWasSet = true;
        }

        @Override
        public void revertToPreviousSeed() {
            if (seedWasReverted) {
                throw new IllegalStateException("Seed has already been reverted");
            }

            this.currentSeed = initialSeed;
            this.seedWasReverted = true;
        }
    }

}