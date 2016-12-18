package uk.org.fyodor.generators.collections;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.range.Range;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.collections.GeneratorUtils.generatingFrom;
import static uk.org.fyodor.generators.collections.GeneratorUtils.generatingRandomIntegers;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public final class ArrayGeneratorTest extends BaseTestWithRule {

    private static final int MAX_SIZE = 1000;
    private static final int DEFAULT_SIZE = 15;

    @Test
    public void generatesArrayOfDefaultSizeWhenNoneSpecified() {
        assertThat(RDG.array(Integer.class, generatingFrom(1, 2, 3, 4, 5, 6, 7, 6, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -6, -7)).next())
                .hasSize(DEFAULT_SIZE)
                .containsExactly(1, 2, 3, 4, 5, 6, 7, 6, 5, 4, 3, 2, 1, 0, -1);
    }

    @Test
    public void generatesEmptyArray() {
        assertThat(RDG.array(Integer.class, generatingRandomIntegers(), 0).next()).isEmpty();
    }

    @Test
    public void generatesArrayOfSize() {
        assertThat(RDG.array(String.class, generatingFrom("a", "b", "c", "d", "e"), 5).next())
                .hasSize(5)
                .containsExactly("a", "b", "c", "d", "e");
    }

    @Test
    public void generatesArrayForFixedSizeRange() {
        assertThat(RDG.array(String.class, generatingFrom("a", "b", "c"), fixed(3)).next())
                .hasSize(3)
                .containsExactly("a", "b", "c");
    }

    @Test
    public void generatesArrayForClosedSizeRange() {
        final Generator<Integer[]> generator = RDG.array(Integer.class, generatingRandomIntegers(), Range.closed(1, 3));

        for (final Integer[] generatedList : from(generator).sample(100)) {
            assertThat(generatedList.length).isBetween(1, 3);
        }
    }

    @Test
    public void generatesArrayUpToMaximumSize() {
        assertThat(RDG.array(Integer.class, generatingRandomIntegers(), MAX_SIZE + 1).next())
                .hasSize(MAX_SIZE);
    }

    @Test
    public void generatesEmptyArrayForNegativeSize() {
        assertThat(RDG.array(Integer.class, generatingRandomIntegers(), -1).next())
                .isEmpty();
    }

    @Test
    public void generatesEmptyArrayForNegativeFixedSizeRange() {
        assertThat(RDG.array(Integer.class, generatingRandomIntegers(), fixed(-1)).next())
                .isEmpty();
    }

    @Test
    public void generatesEmptyArrayForNegativeClosedSizeRange() {
        assertThat(RDG.array(Integer.class, generatingRandomIntegers(), closed(-10, -1)).next())
                .isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullSizeRange() {
        RDG.array(Integer.class, generatingFrom(1), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullGeneratorWithDefaultSize() {
        RDG.array(Object.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullGeneratorWithValidSize() {
        RDG.array(Object.class, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullGeneratorWithValidSizeRange() {
        RDG.array(Object.class, null, fixed(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullTypeClassWithValidSize() {
        RDG.array(null, generatingFrom(1), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullTypeClassWithValidSizeRange() {
        RDG.array(null, generatingFrom(1), fixed(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullTypeClassWithDefaultSize() {
        RDG.array(null, generatingFrom(1));
    }
}
