package uk.org.fyodor.testapi;

public final class FailedWithSeed extends Throwable {

    private final long seed;

    FailedWithSeed(final long seed, final Throwable cause) {
        super("Test failed with seed " + seed, cause);
        this.seed = seed;
    }

    public long seed() {
        return seed;
    }
}
