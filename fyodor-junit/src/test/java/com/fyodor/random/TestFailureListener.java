package com.fyodor.random;

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
        //noinspection ThrowableResultOfMethodCallIgnored
        final Throwable rootCause = rootCause(failure.getException());
        if (rootCause instanceof FailedWithSeedException) {
            final Description testDescription = failure.getDescription();
            reporter.seedReportedWhenTestFails(
                    testDescription.getTestClass(),
                    testDescription.getMethodName(),
                    ((FailedWithSeedException) rootCause).seed());
        }
    }

    private static Throwable rootCause(final Throwable t) {
        if (t.getCause() == null) {
            return t;
        }
        return rootCause(t.getCause());
    }

    static RunListener testFailed(final Reporter reporter) {
        return new TestFailureListener(reporter);
    }
}
