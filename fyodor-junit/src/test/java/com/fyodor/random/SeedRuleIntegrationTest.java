package com.fyodor.random;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

import static com.fyodor.random.RandomValuesProvider.seed;
import static com.fyodor.random.ReportAssert.assertThat;
import static com.fyodor.random.TestRunner.testRunner;

public final class SeedRuleIntegrationTest {

    private static final Reporter SEED_REPORTER = new Reporter();
    private static final SeededTestListener listener = new SeededTestListener(SEED_REPORTER);

    @Test
    public void setsTheSeedBeforeEachTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();

        final Class<?> testClass = SeededTestClass.class;

        testRunner(listener)
                .scheduleTestWithInitialSeed(testClass, initialSeed)
                .run();

        assertThat(SEED_REPORTER.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed)
                .seedReportedInTestFailure(1334);

        assertThat(SEED_REPORTER.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void resetsTheSeedToDefaultAfterEachTestMethod() {
        final long initialSeed = seed().current();
        final Class<?> testClass = SeededTestClass.class;

        testRunner(listener)
                .scheduleTest(testClass)
                .run();

        assertThat(SEED_REPORTER.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed);

        assertThat(SEED_REPORTER.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void setsTheSeedBeforeEachAnnotatedTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();
        final Class<?> testClass = TestClassWithSeededTestMethods.class;

        testRunner(listener)
                .scheduleTestWithInitialSeed(testClass, initialSeed)
                .run();

        assertThat(SEED_REPORTER.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(9876)
                .seedWhenTestHasFinished(initialSeed)
                .seedReportedInTestFailure(9876);

        assertThat(SEED_REPORTER.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(9371)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void alwaysDescribesTheCurrentSeedWhenTheTestFails() {
        final long initialSeed = new Random().nextLong();
        final Class<?> testClass = NonSeededTestClass.class;

        testRunner(listener)
                .scheduleTestWithInitialSeed(testClass, initialSeed)
                .run();

        assertThat(SEED_REPORTER.reportFor(testClass, "redTest"))
                .seedReportedInTestFailure(initialSeed);
    }

    @Test
    public void seedAnnotationAtMethodLevelTakesPrecedenceOverSeedAnnotationAtClassLevel() {
        final long initialSeed = new Random().nextLong();

        final Class<?> testClass = SeededTestClassWithSeededTestMethods.class;

        testRunner(listener)
                .scheduleTestWithInitialSeed(testClass, initialSeed)
                .run();

        assertThat(SEED_REPORTER.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(3891)
                .seedWhenTestHasFinished(initialSeed)
                .seedReportedInTestFailure(3891);

        assertThat(SEED_REPORTER.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1357)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void seedsForParallelTestsDoNotInterfereWithEachOther() {
        testRunner(listener)
                .scheduleTestWithInitialSeed(SeededTestClass.class, 0)
                .scheduleTestWithInitialSeed(SeededTestClassWithSeededTestMethods.class, 1)
                .scheduleTestWithInitialSeed(TestClassWithSeededTestMethods.class, 2)
                .runInParallel();

        assertThat(SEED_REPORTER.reportFor(SeededTestClass.class, "redTest"))
                .seedBeforeTestStarts(0)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(0)
                .seedReportedInTestFailure(1334);

        assertThat(SEED_REPORTER.reportFor(SeededTestClass.class, "greenTest"))
                .seedBeforeTestStarts(0)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(0);

        assertThat(SEED_REPORTER.reportFor(SeededTestClassWithSeededTestMethods.class, "redTest"))
                .seedBeforeTestStarts(1)
                .seedDuringTest(3891)
                .seedWhenTestHasFinished(1)
                .seedReportedInTestFailure(3891);

        assertThat(SEED_REPORTER.reportFor(SeededTestClassWithSeededTestMethods.class, "greenTest"))
                .seedBeforeTestStarts(1)
                .seedDuringTest(1357)
                .seedWhenTestHasFinished(1);

        assertThat(SEED_REPORTER.reportFor(TestClassWithSeededTestMethods.class, "redTest"))
                .seedBeforeTestStarts(2)
                .seedDuringTest(9876)
                .seedWhenTestHasFinished(2)
                .seedReportedInTestFailure(9876);

        assertThat(SEED_REPORTER.reportFor(TestClassWithSeededTestMethods.class, "greenTest"))
                .seedBeforeTestStarts(2)
                .seedDuringTest(9371)
                .seedWhenTestHasFinished(2);
    }

    @Seed(1334)
    public static final class SeededTestClass {

        @Rule
        public final TestRule rule = new SeedRule();

        @Test
        public void redTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        public void greenTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }

    @Seed(1984)
    public static final class SeededTestClassWithSeededTestMethods {

        @Rule
        public final TestRule rule = new SeedRule();

        @Test
        @Seed(3891)
        public void redTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        @Seed(1357)
        public void greenTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }

    public static final class TestClassWithSeededTestMethods {

        @Rule
        public final TestRule rule = new SeedRule();

        @Test
        @Seed(9876)
        public void redTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        @Seed(9371)
        public void greenTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }

    public static final class NonSeededTestClass {

        @Rule
        public final TestRule rule = new SeedRule();

        @Test
        public void redTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        public void greenTest() {
            SEED_REPORTER.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }
}
