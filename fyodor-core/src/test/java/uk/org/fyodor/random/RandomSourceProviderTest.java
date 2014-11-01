package uk.org.fyodor.random;

import uk.org.fyodor.BaseTestWithRule;
import uk.org.fyodor.generators.Generator;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uk.org.fyodor.Sampler.Sample;
import static uk.org.fyodor.Sampler.from;
import static org.assertj.core.api.Assertions.assertThat;

public final class RandomSourceProviderTest extends BaseTestWithRule {

    @Test
    public void consistentSequenceOfBooleansShouldBeReturnedForKnownSeed() {
        final long seed = new Random().nextLong();

        RandomSourceProvider.seed().next(seed);

        assertThat(from(randomValuesNextBoolean()).sample(100))
                .containsExactlyElementsOf(expectedBooleansFor(seed));
    }

    @Test
    public void resettingTheSeedRevertsToTheInitialSeed() {
        final long initialSeed = RandomSourceProvider.seed().current();
        final long nextSeed = new Random().nextLong();

        RandomSourceProvider.seed().next(nextSeed);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(nextSeed);

        RandomSourceProvider.seed().previous();
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void seedsCanBeSetAndRevertedInSequence() {
        final long initialSeed = RandomSourceProvider.seed().current();

        RandomSourceProvider.seed().next(1);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(1);

        RandomSourceProvider.seed().next(2);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(2);

        RandomSourceProvider.seed().next(3);
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(3);

        RandomSourceProvider.seed().previous();
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(2);

        RandomSourceProvider.seed().previous();
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(1);

        RandomSourceProvider.seed().previous();
        assertThat(RandomSourceProvider.seed().current()).isEqualTo(initialSeed);
    }

    @Test
    public void revertingSeedProvidesConsistentSequenceOfBooleans() {
        final long initialSeed = RandomSourceProvider.seed().current();
        final List<Boolean> expectedBooleansForInitialSeed = expectedBooleansFor(initialSeed);

        final long nextRandomSeed = new Random().nextLong();
        RandomSourceProvider.seed().next(nextRandomSeed);

        assertThat(from(randomValuesNextBoolean()).sample(100).asList())
                .isNotEqualTo(expectedBooleansForInitialSeed);

        RandomSourceProvider.seed().previous();
        assertThat(from(randomValuesNextBoolean()).sample(100).asList())
                .isEqualTo(expectedBooleansForInitialSeed);
    }

    @Test
    public void revertingToThePreviousSeedFromTheInitialSeedDoesNothing() throws ExecutionException, InterruptedException {
        final SeedHolder holder = new SeedHolder();
        //We need to run in a new thread so it has its own ThreadLocal random with an initial seed
        final ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                holder.setSampleBefore(from(randomValuesNextBoolean()).sample(100));
                holder.setStartingSeed(RandomSourceProvider.seed().current());

                RandomSourceProvider.seed().previous();

                holder.setSeed(RandomSourceProvider.seed().current());
                holder.setSampleAfter(from(randomValuesNextBoolean()).sample(100));
            }
        }).get();

        //Seeds should be the same
        assertThat(holder.seed).isEqualTo(holder.startingSeed);

        //But samples should be different because we should not have set the initial seed again
        assertThat(holder.sampleBefore.asList()).isNotEqualTo(holder.sampleAfter.asList());
    }

    private static final class SeedHolder {

        private long startingSeed;
        private long seed;
        private Sample<Boolean> sampleBefore;
        private Sample<Boolean> sampleAfter;

        void setStartingSeed(final long seed) {
            this.startingSeed = seed;
        }

        void setSeed(final long seed) {
            this.seed = seed;
        }

        public void setSampleBefore(final Sample<Boolean> sampleBefore) {
            this.sampleBefore = sampleBefore;
        }

        public void setSampleAfter(final Sample<Boolean> sampleAfter) {
            this.sampleAfter = sampleAfter;
        }
    }

    private static List<Boolean> expectedBooleansFor(final long seed) {
        final Random random = new Random(seed);
        final List<Boolean> booleans = new LinkedList<Boolean>();
        for (int i = 0; i < 100; i++) {
            booleans.add(random.nextBoolean());
        }
        return booleans;
    }

    private static Generator<Boolean> randomValuesNextBoolean() {
        return new Generator<Boolean>() {
            @Override
            public Boolean next() {
                return RandomSourceProvider.sourceOfRandomness().randomBoolean();
            }
        };
    }
}