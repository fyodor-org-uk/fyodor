package uk.org.fyodor.junit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.org.fyodor.testapi.CurrentSeed;
import uk.org.fyodor.testapi.Seed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.org.fyodor.generators.RDG.longVal;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

@SuppressWarnings("Duplicates")
@ExtendWithFyodor
final class SeedExtensionTest {

    private final long initialSeed = longVal().next();

    @BeforeEach
    void configureFyodor() {
        seed().next(initialSeed);
    }

    @AfterEach
    void revertFyodor() {
        seed().previous();
    }

    @Nested
    final class NotAnnotated {
        @Test
        void usesInitialSeed(final CurrentSeed seed) {
            final long expectedSeed = initialSeed;

            assertEquals(expectedSeed, seed.value(), "the parameter seed value should be the initial seed");
            assertEquals(expectedSeed, seed().current(), "the current seed should be the initial seed");
        }
    }

    @Nested
    @Seed(98723473L)
    final class Annotated {
        @Test
        @Seed(34765632L)
        void usesSeedFromTestMethodAnnotation(final CurrentSeed seed) {
            final long expectedSeed = 34765632L;

            assertEquals(expectedSeed, seed.value(), "the parameter seed should be the test method annotation seed");
            assertEquals(expectedSeed, seed().current(), "the current seed should be the test method annotation seed");
        }

        @Test
        void usesSeedFromTestClassAnnotation(final CurrentSeed seed) {
            final long expectedSeed = 98723473L;

            assertEquals(expectedSeed, seed.value(), "the parameter seed should be the test class annotation seed");
            assertEquals(expectedSeed, seed().current(), "the current seed should be the test method annotation seed");
        }
    }
}
