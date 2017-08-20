package uk.org.fyodor.random;

final class FailedWithSeed extends Throwable {

    private final long seed;

    FailedWithSeed(final long seed, final Throwable cause) {
        super("Test failed with seed " + seed, cause);
        this.seed = seed;
    }

    long seed() {
        return seed;
    }
}
