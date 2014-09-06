package com.fyodor.random;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import static com.fyodor.random.RandomValuesProvider.seed;
import static com.fyodor.random.ThrowableCauseSetter.setRootCause;

public final class FyodorRunListener extends RunListener {

    @Override
    public void testStarted(final Description description) throws Exception {
        new JunitTestSeeder(description).nextSeed();
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        seed().previous();
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        setRootCause(failure.getException(), new FailedWithSeedException(seed().current()));
    }
}
