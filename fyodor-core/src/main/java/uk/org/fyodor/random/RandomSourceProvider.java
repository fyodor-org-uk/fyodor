package uk.org.fyodor.random;

import java.util.Random;
import java.util.Stack;

import static java.lang.ThreadLocal.withInitial;

public final class RandomSourceProvider {

    private static final ThreadLocal<Seeder> seeder = withInitial(() -> new Seeder(System.currentTimeMillis()));

    public static RandomValues sourceOfRandomness() {
        return new DefaultRandomValues(seeder.get().random());
    }

    public static Seeder seed() {
        return seeder.get();
    }

    private RandomSourceProvider() {
    }

    public static final class Seeder {

        private final Stack<Long> seedStack = new Stack<>();
        private final Random random = new Random();

        private Seeder(final long initialSeed) {
            next(initialSeed);
        }

        public void next(final long seed) {
            seedStack.push(seed);
            random.setSeed(seed);
        }

        public void previous() {
            if (seedStack.size() > 1) {
                seedStack.pop();
                random.setSeed(seedStack.peek());
            }
        }

        public long current() {
            return seedStack.peek();
        }

        private Random random() {
            return random;
        }
    }
}
