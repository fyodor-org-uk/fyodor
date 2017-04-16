package uk.org.fyodor.random;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static uk.org.fyodor.random.RandomSourceProvider.seed;

public final class FyodorTestRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                starting(description);
                try {
                    base.evaluate();
                } catch (final Throwable t) {
                    failed(t);
                } finally {
                    finished();
                }
            }
        };
    }

    private static void failed(final Throwable t) throws Throwable {
        throw new FailedWithSeed(seed().current(), t);
    }

    private static void starting(final Description description) {
        new JunitTestSeeder(description).nextSeed();
    }

    private static void finished() {
        seed().previous();
    }
}
