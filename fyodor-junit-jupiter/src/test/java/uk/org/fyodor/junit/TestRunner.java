package uk.org.fyodor.junit;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.Map;

final class TestRunner {

    private final Class<?> testClass;

    private TestRunner(final Class<?> testClass) {
        this.testClass = testClass;
    }

    TestRunReport run() {
        final TestRunReport report = new TestRunReport();
        final RunCapture runCapture = new RunCapture(report);
        junitPlatformFor(testClass).run(runCapture);
        return report;
    }

    private static JUnitPlatform junitPlatformFor(final Class<?> testClass) {
        try {
            return new JUnitPlatform(testClass);
        } catch (final InitializationError initializationError) {
            throw new RuntimeException(initializationError);
        }
    }

    static TestRunner runnerFor(final Class<?> testClass) {
        return new TestRunner(testClass);
    }

    interface TestRunDetails {
        Failure failure();
    }

    private static final class RunCapture extends RunNotifier {

        private final TestRunReport report;

        private RunCapture(final TestRunReport report) {
            this.report = report;
        }

        @Override
        public void addListener(final RunListener listener) {
            super.addListener(listener);
        }

        @Override
        public void removeListener(final RunListener listener) {
            super.removeListener(listener);
        }

        @Override
        public void fireTestRunStarted(final Description description) {
            super.fireTestRunStarted(description);
        }

        @Override
        public void fireTestRunFinished(final Result result) {
            super.fireTestRunFinished(result);
        }

        @Override
        public void fireTestStarted(final Description description) throws StoppedByUserException {
            super.fireTestStarted(description);
        }

        @Override
        public void fireTestFailure(final Failure failure) {
            this.report.testFailed(failure);
        }

        @Override
        public void fireTestAssumptionFailed(final Failure failure) {
            super.fireTestAssumptionFailed(failure);
        }

        @Override
        public void fireTestFinished(final Description description) {
            super.fireTestFinished(description);
        }
    }

    static final class TestRunReport {

        private final Map<String, Failure> failures = new HashMap<>();

        TestRunDetails detailsFor(final Class<?> testClass, final String testName) {
            return new TestRunDetails() {
                @Override
                public Failure failure() {
                    final String[] split = testClass.getName().split("\\.");
                    final String displayName = split[split.length - 1] + "." + testName + "()";

                    return failures.get(displayName);
                }
            };
        }

        private void testFailed(final Failure failure) {
            final Description description = failure.getDescription();

            failures.put(description.getClassName() + "." + description.getMethodName(), failure);
        }
    }
}
