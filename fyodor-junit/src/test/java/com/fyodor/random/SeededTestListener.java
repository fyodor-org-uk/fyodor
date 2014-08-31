package com.fyodor.random;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

import static com.fyodor.random.RandomValuesProvider.seed;

final class SeededTestListener extends RunNotifier {

    private final Reporter reporter;

    SeededTestListener(final Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void fireTestStarted(final Description description) throws StoppedByUserException {
        reporter.seedBeforeTestStarts(description.getTestClass(), description.getMethodName(), seed().current());
    }

    @Override
    public void fireTestFinished(final Description description) {
        reporter.seedAfterTestFinishes(description.getTestClass(), description.getMethodName(), seed().current());
    }

    @Override
    public void fireTestFailure(final Failure failure) {
        //noinspection ThrowableResultOfMethodCallIgnored
        if (failure.getException() instanceof FailedWithSeedException) {
            //noinspection ThrowableResultOfMethodCallIgnored
            final FailedWithSeedException cause = (FailedWithSeedException) failure.getException();
            final Description description = failure.getDescription();
            reporter.seedReportedWhenTestFails(description.getTestClass(), description.getMethodName(), cause.seed());
        }
    }
}
