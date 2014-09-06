package com.fyodor.random;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.fyodor.random.RandomValuesProvider.seed;
import static com.fyodor.random.ThrowableCauseSetter.setRootCause;

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
}
