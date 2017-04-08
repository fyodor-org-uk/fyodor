package uk.org.fyodor.generators.collections;

import org.junit.Test;
import uk.org.fyodor.BaseTest;
import uk.org.fyodor.Sampler;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.range.Range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.collections.GeneratorUtils.generatingFrom;
import static uk.org.fyodor.generators.collections.GeneratorUtils.generatingRandomIntegers;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public final class ListGeneratorTest extends BaseTest {

    private static final int MAX_SIZE = 1000;
    private static final int DEFAULT_SIZE = 15;

    @Test
    public void generatesListOfGeneratedValues() {
        final Generator<List<Integer>> generator = RDG.list(generatingFrom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5);
        assertThat(generator.next()).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void generatesEmptyListForSizeOfZero() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers(), 0);
        assertThat(generator.next()).isEmpty();
    }

    @Test
    public void generatesEmptyListForFixedSizeOfZero() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers(), fixed(0));
        assertThat(generator.next()).isEmpty();
    }

    @Test
    public void generatesListsOfSizeUpToMaximum() {
        assertThat(RDG.list(generatingRandomIntegers(), MAX_SIZE).next()).hasSize(MAX_SIZE);
        assertThat(RDG.list(generatingRandomIntegers(), fixed(MAX_SIZE)).next()).hasSize(MAX_SIZE);
    }

    @Test
    public void sizeGreaterThanMaximumReturnsListOfMaximumSize() {
        assertThat(RDG.list(generatingRandomIntegers(), MAX_SIZE + 1).next()).hasSize(MAX_SIZE);
    }

    @Test
    public void fixedSizeRangeGreaterThanMaximumReturnsListOfMaximumSize() {
        assertThat(RDG.list(generatingRandomIntegers(), fixed(MAX_SIZE + 1)).next()).hasSize(MAX_SIZE);
    }

    @Test
    public void closedRangeWithUpperBoundGreaterThanMaximumReturnsListOfMaximumSize() {
        assertThat(RDG.list(generatingRandomIntegers(), closed(MAX_SIZE + 1, MAX_SIZE + 1)).next()).hasSize(MAX_SIZE);
    }

    @Test
    public void listHasSizeWithinClosedRange() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers(), Range.closed(1, 3));

        for (final List<Integer> generatedList : from(generator).sample(100)) {
            assertThat(generatedList.size()).isBetween(1, 3);
        }
    }

    @Test
    public void generatesEmptyListForNegativeSize() {
        assertThat(RDG.list(generatingRandomIntegers(), -1).next()).isEmpty();
    }

    @Test
    public void generatesListOfAtLeastZeroSizeForSizeRangeCoveringNegativeToPositive() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers(), closed(-2, 2));
        for (final List<Integer> list : Sampler.from(generator).sample(1000)) {
            assertThat(list.size())
                    .isBetween(0, 2);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowNullGeneratorWithValidSize() {
        RDG.list(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowNullGeneratorWithValidSizeRange() {
        RDG.list(null, fixed(1));
    }

    @Test
    public void generatesListAtLeastWithSizeZeroWhenLowerBoundIsNegative() {
        assertThat(RDG.list(generatingRandomIntegers(), Range.closed(-1, 0)).next()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowNullSizeRange() {
        RDG.list(generatingFrom(1), null);
    }

    @Test
    public void generatedListsDoNotAlwaysHaveTheSameSizeForRange() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers(), Range.closed(1, 3));

        final Set<Integer> setOfListSizes = new HashSet<>();
        for (final List<Integer> generatedList : from(generator).sample(1000)) {
            setOfListSizes.add(generatedList.size());
        }

        assertThat(setOfListSizes.size()).isGreaterThan(1);
    }

    @Test
    public void generatedListsAlwaysHaveTheSameSizeForFixedRange() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers(), fixed(5));

        final Set<Integer> setOfListSizes = new HashSet<>();
        for (final List<Integer> generatedList : from(generator).sample(1000)) {
            setOfListSizes.add(generatedList.size());
        }

        assertThat(setOfListSizes).containsExactly(5);
    }

    @Test
    public void generatedListWithoutSpecifiedSizeIsTheDefaultSize() {
        final Generator<List<Integer>> generator = RDG.list(generatingRandomIntegers());

        final Set<Integer> setOfListSizes = new HashSet<>();
        for (final List<Integer> generatedList : from(generator).sample(1000)) {
            setOfListSizes.add(generatedList.size());
        }

        assertThat(setOfListSizes).containsExactly(DEFAULT_SIZE);
    }
}
