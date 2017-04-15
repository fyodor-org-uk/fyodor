package uk.org.fyodor.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.org.fyodor.testapi.Seed;

import java.util.Optional;
import java.util.function.Consumer;

import static uk.org.fyodor.random.RandomSourceProvider.seed;

final class FyodorSeedRule implements TestRule {

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
                    finished(description);
                }
            }
        };
    }

    private static void failed(final Throwable t) throws Throwable {
        throw new FailedWithSeed(seed().current(), t);
    }

    private static void starting(final Description description) {
        seedFrom(description).ifPresent(useSeed());
    }

    private static void finished(final Description description) {
        seedFrom(description).ifPresent(revertToPreviousSeed());
    }

    private static Consumer<Seed> useSeed() {
        return seed -> seed().next(seed.value());
    }

    private static Consumer<Seed> revertToPreviousSeed() {
        return seed -> seed().previous();
    }

    private static Optional<Seed> seedFrom(final Description description) {
        final Seed classAnnotation = description.getTestClass().getAnnotation(Seed.class);
        final Seed methodAnnotation = description.getAnnotation(Seed.class);

        return methodAnnotation != null
                ? Optional.of(methodAnnotation)
                : Optional.ofNullable(classAnnotation);
    }
}
