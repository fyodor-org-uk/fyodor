package com.fyodor.random;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import static com.fyodor.random.RandomValuesProvider.seed;

final class TestFinishedListener extends RunListener {

    private final Reporter reporter;

    private TestFinishedListener(final Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        reporter.seedAfterTestFinishes(description.getTestClass(), description.getMethodName(), seed().current());
    }

    static RunListener testFinished(final Reporter reporter) {
        return new TestFinishedListener(reporter);
    }
}
