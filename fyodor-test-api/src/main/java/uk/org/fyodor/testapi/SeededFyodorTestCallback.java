package uk.org.fyodor.testapi;

public final class SeededFyodorTestCallback implements FyodorTestCallback {

    private final SeedController seedController;

    public SeededFyodorTestCallback(final SeedController seedController) {
        this.seedController = seedController;
    }

    @Override
    public void beforeTestExecution(final FyodorTest test) {
        final long seed = test.getAnnotation(Seed.class)
                .map(Seed::value)
                .orElse(seedController.currentSeed());

        seedController.setCurrentSeed(seed);
    }

    @Override
    public void afterTestExecution(final FyodorTest test) {
        seedController.revertToPreviousSeed();
    }

    @Override
    public void testFailed(final FyodorTest test, final Throwable throwable) throws Throwable {
        throw new FailedWithSeed(seedController.currentSeed(), throwable);
    }

    public interface SeedController {
        long currentSeed();

        void setCurrentSeed(long currentSeed);

        void revertToPreviousSeed();
    }
}
