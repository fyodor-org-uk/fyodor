package uk.org.fyodor.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import uk.org.fyodor.random.Seed;

import java.util.Optional;

import static uk.org.fyodor.random.RandomSourceProvider.seed;

final class FyodorSeedRule extends TestWatcher {

    @Override
    protected void failed(final Throwable t, final Description description) {
        setRootCause(t, new FailedWithSeedException(seed().current()));
    }

    @Override
    protected void starting(final Description description) {
        seedFrom(description)
                .ifPresent(seed -> seed().next(seed.value()));
    }

    @Override
    protected void finished(final Description description) {
        seedFrom(description)
                .ifPresent(seed -> seed().previous());
    }

    private static Optional<Seed> seedFrom(final Description description) {
        final Seed classAnnotation = description.getTestClass().getAnnotation(Seed.class);
        final Seed methodAnnotation = description.getAnnotation(Seed.class);

        return methodAnnotation != null
                ? Optional.of(methodAnnotation)
                : Optional.ofNullable(classAnnotation);
    }

    private static void setRootCause(final Throwable t, final Throwable cause) {
        if (t.getCause() == null) {
            t.initCause(cause);
        } else {
            setRootCause(t.getCause(), cause);
        }
    }
}
