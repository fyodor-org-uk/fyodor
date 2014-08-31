package com.fyodor.random;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static com.fyodor.random.RandomValuesProvider.seed;

public final class SeedRule extends TestWatcher {

    @Override
    protected void starting(final Description description) {
        final Seed seed = seedFrom(description);

        if (seed != null) {
            seed().next(seed.value());
        }
    }

    @Override
    protected void finished(final Description description) {
        seed().previous();
    }

    @Override
    protected void failed(final Throwable e, final Description description) {
        throw new FailedWithSeedException(seed().current());
    }

    private static Seed seedFrom(final Description description) {
        final Seed classAnnotation = description.getTestClass().getAnnotation(Seed.class);
        final Seed methodAnnotation = description.getAnnotation(Seed.class);
        return methodAnnotation != null ? methodAnnotation : classAnnotation;
    }
}
