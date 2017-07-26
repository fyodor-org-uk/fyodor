package uk.org.fyodor.junit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import uk.org.fyodor.testapi.SeededFyodorTestCallback;
import uk.org.fyodor.testapi.SeededFyodorTestCallback.SeedController;

import static uk.org.fyodor.junit.FyodorTestAdapter.fyodorTestOf;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

final class SeedExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, TestExecutionExceptionHandler {

    private final SeededFyodorTestCallback seedCallback = new SeededFyodorTestCallback(seedController());

    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) throws Exception {
        seedCallback.beforeTestExecution(fyodorTestOf(extensionContext));
    }

    @Override
    public void afterTestExecution(final ExtensionContext extensionContext) throws Exception {
        seedCallback.afterTestExecution(fyodorTestOf(extensionContext));
    }

    @Override
    public void handleTestExecutionException(final ExtensionContext extensionContext,
                                             final Throwable throwable) throws Throwable {
        seedCallback.testFailed(fyodorTestOf(extensionContext), throwable);
    }

    private static SeedController seedController() {
        return new SeedController() {
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
        };
    }
}
