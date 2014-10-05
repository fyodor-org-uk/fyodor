package uk.org.fyodor.rule;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static uk.org.fyodor.random.RandomValuesProvider.seed;

public final class FyodorTestRule extends TestWatcher {

    @Override
    protected void starting(final Description description) {
        new JunitTestSeeder(description).nextSeed();
    }

    @Override
    protected void finished(final Description description) {
        seed().previous();
    }

    @Override
    protected void failed(final Throwable t, final Description description) {
        setRootCause(t, new FailedWithSeedException(seed().current()));
    }

    private static void setRootCause(final Throwable t, final Throwable cause) {
        if (t.getCause() == null) {
            t.initCause(cause);
        } else {
            setRootCause(t.getCause(), cause);
        }
    }
}
