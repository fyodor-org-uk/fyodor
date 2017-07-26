package uk.org.fyodor.junit;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
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
        public void fireTestFailure(final Failure failure) {
            this.report.testFailed(failure);
        }
    }

    static final class TestRunReport {

        private final Map<String, Failure> failures = new HashMap<>();

        TestRunDetails detailsFor(final Class<?> testClass, final String testName) {
            return () -> {
                final String[] split = testClass.getName().split("\\.");
                final String displayName = split[split.length - 1] + "." + testName + "()";

                return failures.get(displayName);
            };
        }

        private void testFailed(final Failure failure) {
            final Description description = failure.getDescription();

            failures.put(description.getClassName() + "." + description.getMethodName(), failure);
        }
    }
}
