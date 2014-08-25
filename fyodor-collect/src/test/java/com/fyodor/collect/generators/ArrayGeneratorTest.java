package com.fyodor.collect.generators;

import com.fyodor.generators.Generator;
import com.fyodor.range.Range;
import org.junit.Test;

import static com.fyodor.collect.generators.GeneratorUtils.generator;
import static com.fyodor.internal.Sampler.from;
import static com.fyodor.range.Range.closed;
import static com.fyodor.range.Range.fixed;
import static org.assertj.core.api.Assertions.assertThat;

public final class ArrayGeneratorTest {

    private static final int MAX_SIZE = 1000;
    private static final int DEFAULT_SIZE = 15;

    @Test
    public void generatesArrayOfDefaultSizeWhenNoneSpecified() {
        assertThat(RDG.array(Integer.class, generator(1, 2, 3, 4)).next())
                .hasSize(DEFAULT_SIZE)
                .containsExactly(1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3);
    }

    @Test
    public void generatesEmptyArray() {
        assertThat(RDG.array(Integer.class, generator(1, 2, 3, 4), 0).next()).isEmpty();
    }

    @Test
    public void generatesArrayOfSize() {
        assertThat(RDG.array(String.class, generator("a", "b", "c", "d", "e"), 5).next())
                .hasSize(5)
                .containsExactly("a", "b", "c", "d", "e");
    }

    @Test
    public void generatesArrayForFixedSizeRange() {
        assertThat(RDG.array(String.class, generator("a", "b", "c"), fixed(3)).next())
                .hasSize(3)
                .containsExactly("a", "b", "c");
    }

    @Test
    public void generatesArrayForClosedSizeRange() {
        final Generator<String[]> generator = RDG.array(String.class, generator("a", "b", "c"), Range.closed(1, 3));

        for (final String[] generatedList : from(generator).sample(100)) {
            assertThat(generatedList.length).isBetween(1, 3);
        }
    }

    @Test
    public void generatesArrayUpToMaximumSize() {
        assertThat(RDG.array(Integer.class, generator(1), MAX_SIZE + 1).next())
                .hasSize(MAX_SIZE);
    }

    @Test
    public void generatesEmptyArrayForNegativeSize() {
        assertThat(RDG.array(Integer.class, generator(1), -1).next())
                .isEmpty();
    }

    @Test
    public void generatesEmptyArrayForNegativeFixedSizeRange() {
        assertThat(RDG.array(Integer.class, generator(1), fixed(-1)).next())
                .isEmpty();
    }

    @Test
    public void generatesEmptyArrayForNegativeClosedSizeRange() {
        assertThat(RDG.array(Integer.class, generator(1), closed(-10, -1)).next())
                .isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullSizeRange() {
        RDG.array(Integer.class, generator(1), null);
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
        RDG.array(null, generator(1), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullTypeClassWithValidSizeRange() {
        RDG.array(null, generator(1), fixed(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateArrayForNullTypeClassWithDefaultSize() {
        RDG.array(null, generator(1));
    }
}
