package com.fyodor.random;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import static com.fyodor.random.RandomValuesProvider.seed;

final class TestStartedListener extends RunListener {

    private final Reporter reporter;

    private TestStartedListener(final Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        reporter.seedBeforeTestStarts(description.getTestClass(), description.getMethodName(), seed().current());
    }

    static RunListener testStarted(final Reporter reporter) {
        return new TestStartedListener(reporter);
    }
}
