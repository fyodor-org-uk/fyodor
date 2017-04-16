package uk.org.fyodor.random;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

final class TestFailureListener extends RunListener {

    private final Reporter reporter;

    private TestFailureListener(final Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        final Description testDescription = failure.getDescription();
        reporter.seedReportedWhenTestFails(
                testDescription.getTestClass(),
                testDescription.getMethodName(),
                ((FailedWithSeed) failure.getException()).seed());
    }

    static RunListener testFailed(final Reporter reporter) {
        return new TestFailureListener(reporter);
    }
}
