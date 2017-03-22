package uk.org.fyodor.junit;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import uk.org.fyodor.random.Seed;

import java.util.Random;

import static uk.org.fyodor.junit.ReportAssert.assertThat;
import static uk.org.fyodor.junit.Reporter.reporter;
import static uk.org.fyodor.junit.TestFailureListener.testFailed;
import static uk.org.fyodor.junit.TestFinishedListener.testFinished;
import static uk.org.fyodor.junit.TestStartedListener.testStarted;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

@SuppressWarnings("ConstantConditions")
public final class FyodorTestRuleIntegrationTest {

    private static final Reporter<Long> reporter = reporter();

    private final TestRunner<Long> testRunner = new TestRunner<>(
            testStarted(reporter, () -> seed().current()),
            testFailed(reporter, (failure) -> ((FailedWithSeedException) failure.getException().getCause()).seed()),
            testFinished(reporter, () -> seed().current()));

    @Test
    public void setsTheSeedBeforeEachTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();

        final Class<?> testClass = SeededTestClass.class;

        testRunner.scheduleTestWithObject(testClass, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1334L)
                .whenTestHasFinished(initialSeed)
                .whenFailed(1334L);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1334L)
                .whenTestHasFinished(initialSeed);
    }

    @Test
    public void resetsTheSeedToDefaultAfterEachTestMethod() {
        final long initialSeed = seed().current();
        final Class<?> testClass = SeededTestClass.class;

        testRunner.scheduleTest(testClass).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1334L)
                .whenTestHasFinished(initialSeed);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1334L)
                .whenTestHasFinished(initialSeed);
    }

    @Test
    public void setsTheSeedBeforeEachAnnotatedTestMethodAndThenResetsTheSeedAfterEachTestMethod() {
        final long initialSeed = new Random().nextLong();
        final Class<?> testClass = TestClassWithSeededTestMethods.class;

        testRunner.scheduleTestWithObject(testClass, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(9876L)
                .whenTestHasFinished(initialSeed)
                .whenFailed(9876L);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(9371L)
                .whenTestHasFinished(initialSeed);
    }

    @Test
    public void alwaysDescribesTheCurrentSeedWhenTheTestFails() {
        final long initialSeed = new Random().nextLong();
        final Class<?> testClass = NonSeededTestClass.class;

        testRunner.scheduleTestWithObject(testClass, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .whenFailed(initialSeed);
    }

    @Test
    public void seedAnnotationAtMethodLevelTakesPrecedenceOverSeedAnnotationAtClassLevel() {
        final long initialSeed = new Random().nextLong();

        final Class<?> testClass = SeededTestClassWithSeededTestMethods.class;

        testRunner.scheduleTestWithObject(testClass, initialSeed, seed -> seed().next(seed)).run();

        assertThat(reporter.reportFor(testClass, "redTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(3891L)
                .whenTestHasFinished(initialSeed)
                .whenFailed(3891L);

        assertThat(reporter.reportFor(testClass, "greenTest"))
                .beforeTestStarts(initialSeed)
                .duringTest(1357L)
                .whenTestHasFinished(initialSeed);
    }

    @Test
    public void seedsForParallelTestsDoNotInterfereWithEachOther() {
        testRunner.scheduleTestWithObject(SeededTestClass.class, 0L, seed -> seed().next(seed))
                .scheduleTestWithObject(SeededTestClassWithSeededTestMethods.class, 1L, seed -> seed().next(seed))
                .scheduleTestWithObject(TestClassWithSeededTestMethods.class, 2L, seed -> seed().next(seed))
                .runInParallel();

        assertThat(reporter.reportFor(SeededTestClass.class, "redTest"))
                .beforeTestStarts(0L)
                .duringTest(1334L)
                .whenTestHasFinished(0L)
                .whenFailed(1334L);

        assertThat(reporter.reportFor(SeededTestClass.class, "greenTest"))
                .beforeTestStarts(0L)
                .duringTest(1334L)
                .whenTestHasFinished(0L);

        assertThat(reporter.reportFor(SeededTestClassWithSeededTestMethods.class, "redTest"))
                .beforeTestStarts(1L)
                .duringTest(3891L)
                .whenTestHasFinished(1L)
                .whenFailed(3891L);

        assertThat(reporter.reportFor(SeededTestClassWithSeededTestMethods.class, "greenTest"))
                .beforeTestStarts(1L)
                .duringTest(1357L)
                .whenTestHasFinished(1L);

        assertThat(reporter.reportFor(TestClassWithSeededTestMethods.class, "redTest"))
                .beforeTestStarts(2L)
                .duringTest(9876L)
                .whenTestHasFinished(2L)
                .whenFailed(9876L);

        assertThat(reporter.reportFor(TestClassWithSeededTestMethods.class, "greenTest"))
                .beforeTestStarts(2L)
                .duringTest(9371L)
                .whenTestHasFinished(2L);
    }

    @Seed(1334)
    public static final class SeededTestClass {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(false);
        }

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(true);
        }
    }

    @Seed(1984)
    public static final class SeededTestClassWithSeededTestMethods {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @Seed(3891)
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(false);
        }

        @Test
        @Seed(1357)
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(true);
        }
    }

    public static final class TestClassWithSeededTestMethods {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        @Seed(9876)
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(false);
        }

        @Test
        @Seed(9371)
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(true);
        }
    }

    public static final class NonSeededTestClass {

        @Rule
        public final TestRule rule = new FyodorTestRule();

        @Rule
        public final TestName testName = new TestName();

        @Test
        public void redTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(false);
        }

        @Test
        public void greenTest() {
            reporter.objectDuringTest(this.getClass(), testName.getMethodName(), seed().current());
            Assert.assertTrue(true);
        }
    }
}
