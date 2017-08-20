package uk.org.fyodor.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.testapi.FyodorTest;
import uk.org.fyodor.testapi.FyodorTestCallback;
import uk.org.fyodor.testapi.SeededFyodorTestCallback;
import uk.org.fyodor.testapi.SeededFyodorTestCallback.SeedController;

import static uk.org.fyodor.junit.FyodorTestAdapter.fyodorTestOf;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

final class SeedRule implements TestRule {

    private final FyodorTestCallback callback;

    SeedRule() {
        this.callback = new SeededFyodorTestCallback(new SeedController() {
            @Override
            public long currentSeed() {
                return seed().current();
            }

            @Override
            public void setCurrentSeed(final long currentSeed) {
                seed().next(currentSeed);
            }

            @Override
            public void revertToPreviousSeed() {
                seed().previous();
            }
        });
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final FyodorTest test = fyodorTestOf(description);
                callback.beforeTestExecution(test);
                try {
                    base.evaluate();
                } catch (final Throwable t) {
                    callback.testFailed(test, t);
                } finally {
                    callback.afterTestExecution(test);
                }
            }
        };
    }

}
