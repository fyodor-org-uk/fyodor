package uk.org.fyodor.random;

import org.junit.runner.Description;

import static uk.org.fyodor.random.RandomValuesProvider.seed;

final class JunitTestSeeder {

    private final Seed seed;

    JunitTestSeeder(final Description description) {
        this.seed = seedFrom(description);
    }

    void nextSeed() {
        if (seed != null) {
            seed().next(seed.value());
        }
    }

    private static Seed seedFrom(final Description description) {
        final Seed classAnnotation = description.getTestClass().getAnnotation(Seed.class);
        final Seed methodAnnotation = description.getAnnotation(Seed.class);
        return methodAnnotation != null ? methodAnnotation : classAnnotation;
    }
}
