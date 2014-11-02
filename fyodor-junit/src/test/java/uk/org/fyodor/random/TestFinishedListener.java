package uk.org.fyodor.random;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import static uk.org.fyodor.random.RandomSourceProvider.seed;

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
