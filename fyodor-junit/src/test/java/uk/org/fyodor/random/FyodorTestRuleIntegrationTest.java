package uk.org.fyodor.random;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

import static uk.org.fyodor.random.RandomValuesProvider.seed;
import static uk.org.fyodor.random.ReportAssert.assertThat;
import static uk.org.fyodor.random.TestFailureListener.testFailed;
import static uk.org.fyodor.random.TestFinishedListener.testFinished;
import static uk.org.fyodor.random.TestStartedListener.testStarted;

public final class FyodorTestRuleIntegrationTest {

    private static final Reporter reporter = new Reporter();

    private final TestRunner testRunner = new TestRunner(
            testStarted(reporter),
            testFailed(reporter),
            testFinished(reporter));

    @Test
    public void setsTheSeedBeforeEachTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();

        final Class<?> testClass = SeededTestClass.class;

        testRunner.scheduleTestWithInitialSeed(testClass, initialSeed).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed)
                .seedReportedInTestFailure(1334);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void resetsTheSeedToDefaultAfterEachTestMethod() {
        final long initialSeed = seed().current();
        final Class<?> testClass = SeededTestClass.class;

        testRunner.scheduleTest(testClass).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void setsTheSeedBeforeEachAnnotatedTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();
        final Class<?> testClass = TestClassWithSeededTestMethods.class;

        testRunner.scheduleTestWithInitialSeed(testClass, initialSeed).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(9876)
                .seedWhenTestHasFinished(initialSeed)
                .seedReportedInTestFailure(9876);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(9371)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void alwaysDescribesTheCurrentSeedWhenTheTestFails() {
        final long initialSeed = new Random().nextLong();
        final Class<?> testClass = NonSeededTestClass.class;

        testRunner.scheduleTestWithInitialSeed(testClass, initialSeed).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .seedReportedInTestFailure(initialSeed);
    }

    @Test
    public void seedAnnotationAtMethodLevelTakesPrecedenceOverSeedAnnotationAtClassLevel() {
        final long initialSeed = new Random().nextLong();

        final Class<?> testClass = SeededTestClassWithSeededTestMethods.class;

        testRunner.scheduleTestWithInitialSeed(testClass, initialSeed).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(3891)
                .seedWhenTestHasFinished(initialSeed)
                .seedReportedInTestFailure(3891);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .seedBeforeTestStarts(initialSeed)
                .seedDuringTest(1357)
                .seedWhenTestHasFinished(initialSeed);
    }

    @Test
    public void seedsForParallelTestsDoNotInterfereWithEachOther() {
        testRunner.scheduleTestWithInitialSeed(SeededTestClass.class, 0)
                .scheduleTestWithInitialSeed(SeededTestClassWithSeededTestMethods.class, 1)
                .scheduleTestWithInitialSeed(TestClassWithSeededTestMethods.class, 2)
                .runInParallel();

        assertThat(reporter.reportFor(SeededTestClass.class, "redTest"))
                .seedBeforeTestStarts(0)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(0)
                .seedReportedInTestFailure(1334);

        assertThat(reporter.reportFor(SeededTestClass.class, "greenTest"))
                .seedBeforeTestStarts(0)
                .seedDuringTest(1334)
                .seedWhenTestHasFinished(0);

        assertThat(reporter.reportFor(SeededTestClassWithSeededTestMethods.class, "redTest"))
                .seedBeforeTestStarts(1)
                .seedDuringTest(3891)
                .seedWhenTestHasFinished(1)
                .seedReportedInTestFailure(3891);

        assertThat(reporter.reportFor(SeededTestClassWithSeededTestMethods.class, "greenTest"))
                .seedBeforeTestStarts(1)
                .seedDuringTest(1357)
                .seedWhenTestHasFinished(1);

        assertThat(reporter.reportFor(TestClassWithSeededTestMethods.class, "redTest"))
                .seedBeforeTestStarts(2)
                .seedDuringTest(9876)
                .seedWhenTestHasFinished(2)
                .seedReportedInTestFailure(9876);

        assertThat(reporter.reportFor(TestClassWithSeededTestMethods.class, "greenTest"))
                .seedBeforeTestStarts(2)
                .seedDuringTest(9371)
                .seedWhenTestHasFinished(2);
    }

    @Seed(1334)
    public static final class SeededTestClass {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Test
        public void redTest() {
            reporter.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        public void greenTest() {
            reporter.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }

    @Seed(1984)
    public static final class SeededTestClassWithSeededTestMethods {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Test
        @Seed(3891)
        public void redTest() {
            reporter.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        @Seed(1357)
        public void greenTest() {
            reporter.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }

    public static final class TestClassWithSeededTestMethods {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Test
        @Seed(9876)
        public void redTest() {
            reporter.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        @Seed(9371)
        public void greenTest() {
            reporter.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }

    public static final class NonSeededTestClass {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Test
        public void redTest() {
            reporter.seedDuringTest(this.getClass(), "redTest", seed().current());
            Assert.assertTrue(false);
        }

        @Test
        public void greenTest() {
            reporter.seedDuringTest(this.getClass(), "greenTest", seed().current());
            Assert.assertTrue(true);
        }
    }
}
