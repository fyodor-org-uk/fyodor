package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static uk.org.fyodor.random.RandomSourceProvider.seed;

public final class FyodorTestRule extends TestWatcher {

    @Override
    protected void failed(final Throwable t, final Description description) {
        setRootCause(t, new FailedWithSeedException(seed().current()));
    }

    @Override
    protected void starting(final Description description) {
        new JunitTestSeeder(description).nextSeed();
    }

    @Override
    protected void finished(final Description description) {
        seed().previous();
    }

    private static void setRootCause(final Throwable t, final Throwable cause) {
        if (t.getCause() == null) {
            t.initCause(cause);
        } else {
            setRootCause(t.getCause(), cause);
        }
    }
}
