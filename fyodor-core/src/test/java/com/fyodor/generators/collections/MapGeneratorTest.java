package com.fyodor.generators.collections;

import com.fyodor.generators.Generator;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fyodor.generators.collections.GeneratorUtils.generator;
import static com.fyodor.generators.collections.GeneratorUtils.randomIntegers;
import static com.fyodor.internal.Sampler.from;
import static com.fyodor.range.Range.closed;
import static com.fyodor.range.Range.fixed;
import static org.assertj.core.api.Assertions.assertThat;

public final class MapGeneratorTest {

    private static final int MAX_SIZE = 1000;

    @Test
    public void generatesMapWithTheDefaultSize() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17),
                generator("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q"));

        assertThat(generator.next())
                .hasSize(15)
                .containsEntry(1, "a")
                .containsEntry(2, "b")
                .containsEntry(3, "c")
                .containsEntry(4, "d")
                .containsEntry(5, "e")
                .containsEntry(6, "f")
                .containsEntry(7, "g")
                .containsEntry(8, "h")
                .containsEntry(9, "i")
                .containsEntry(10, "j")
                .containsEntry(11, "k")
                .containsEntry(12, "l")
                .containsEntry(13, "m")
                .containsEntry(14, "n")
                .containsEntry(15, "o");
    }

    @Test
    public void generatesMapWithFixedSize() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 2, 3, 4, 5),
                generator("a", "b", "c", "d", "e", "f"),
                3);

        assertThat(generator.next())
                .hasSize(3)
                .containsEntry(1, "a")
                .containsEntry(2, "b")
                .containsEntry(3, "c");
    }

    @Test
    public void generatesMapWithFixedSizeRange() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 2, 3, 4, 5),
                generator("a", "b", "c", "d", "e", "f"),
                fixed(3));

        assertThat(generator.next())
                .hasSize(3)
                .containsEntry(1, "a")
                .containsEntry(2, "b")
                .containsEntry(3, "c");
    }

    @Test
    public void generatesMapWithDifferentSizesWithinClosedRange() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 2, 3, 4, 5),
                generator("a", "b", "c", "d", "e", "f"),
                closed(1, 3));

        final Set<Integer> setOfMapSizes = new HashSet<Integer>();
        for (final Map<Integer, String> map : from(generator).sample(100)) {
            setOfMapSizes.add(map.size());
        }

        assertThat(setOfMapSizes).containsExactly(1, 2, 3);
    }

    @Test
    public void generatesEmptyMapForNegativeFixedSize() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(randomIntegers(), randomIntegers(), -1);
        assertThat(generator.next()).isEmpty();
    }

    @Test
    public void generatesEmptyMapForNegativeFixedSizeRange() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(randomIntegers(), randomIntegers(), fixed(-1));
        assertThat(generator.next()).isEmpty();
    }

    @Test
    public void generatesEmptyMapForNegativeSizeRange() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(randomIntegers(), randomIntegers(), closed(-10, -1));
        assertThat(generator.next()).isEmpty();
    }

    @Test
    public void generatesMapThatIsAtLeastEmptyForRangeThatCrossesZero() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(randomIntegers(), randomIntegers(), closed(-2, 2));
        for (final Map<Integer, Integer> map : from(generator).sample(1000)) {
            assertThat(map.size()).isBetween(0, 2);
        }
    }

    @Test
    public void neverReturnsNullMap() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(randomIntegers(), randomIntegers(), 100);
        assertThat(from(generator).sample(MAX_SIZE)).doesNotContainNull();
    }

    @Test
    public void generatesMapOfFixedSizeEvenWhenGeneratedKeysContainDuplicates() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 4, 5),
                generator("a", "b", "c", "d", "e", "f"),
                3);

        for (final Map<Integer, String> map : from(generator).sample(100)) {
            assertThat(map).hasSize(3);
        }
    }

    @Test
    public void generatesMapOfFixedSizeRangeEvenWhenGeneratedKeysContainDuplicates() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 4, 5),
                generator("a", "b", "c", "d", "e", "f"),
                fixed(3));

        for (final Map<Integer, String> map : from(generator).sample(100)) {
            assertThat(map).hasSize(3);
        }
    }

    @Test
    public void generatesMapOfClosedSizeRangeEvenWhenGeneratedKeysContainDuplicates() {
        final Generator<Map<Integer, String>> generator = RDG.map(
                generator(1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 4, 5),
                generator("a", "b", "c", "d", "e", "f"),
                closed(2, 4));

        for (final Map<Integer, String> map : from(generator).sample(100)) {
            assertThat(map.size()).isBetween(2, 4);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void throwsAnExceptionWhenGeneratedKeysContainsTooManyDuplicatesAndFixedSizeCannotBeAchieved() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(
                generator(1),
                randomIntegers(),
                3);

        //Throws an exception because to attempt fulfilling set size would cause an infinite loop
        generator.next();
    }

    @Test
    public void canGenerateMapWithMaximumSize() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(
                randomIntegers(),
                randomIntegers(),
                MAX_SIZE);

        assertThat(generator.next()).hasSize(MAX_SIZE);
    }

    @Test
    public void sizeWillNotExceedMaximumForFixedSizeExceedingTheMaximum() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(
                randomIntegers(),
                randomIntegers(),
                MAX_SIZE * 2);

        assertThat(generator.next()).hasSize(MAX_SIZE);
    }

    @Test
    public void sizeWillNotExceedMaximumForFixedSizeRangeExceedingTheMaximum() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(
                randomIntegers(),
                randomIntegers(),
                fixed(MAX_SIZE * 2));

        assertThat(generator.next()).hasSize(MAX_SIZE);
    }

    @Test(expected = NullPointerException.class)
    public void throwsExceptionWhenKeyGeneratorGeneratesNull() {
        final Generator<Map<Integer, Integer>> generator = RDG.map(
                generator(1, 2, 3, null, 5, 6, 7),
                randomIntegers(),
                6);
        generator.next();
    }

    @Test
    public void keysAndValuesCanBeConcreteImplementationsOfType() {
        final Generator<Map<Type, Type>> generator = RDG.map(ofConcreteTypes(), ofConcreteTypes());

        for (final Type type : generator.next().keySet()) {
            assertThat(type).isInstanceOf(ConcreteType.class);
        }

        for (final Type type : generator.next().values()) {
            assertThat(type).isInstanceOf(ConcreteType.class);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapWithNullKeyGenerator() {
        RDG.map(null, randomIntegers());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapWithNullValueGenerator() {
        RDG.map(randomIntegers(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapWithNullKeyGeneratorAndValidFixedSize() {
        RDG.map(null, randomIntegers(), 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapWithNullValueGeneratorAndValidFixedSize() {
        RDG.map(randomIntegers(), null, 15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapWithNullKeyGeneratorAndValidSizeRange() {
        RDG.map(null, randomIntegers(), fixed(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapWithNullValueGeneratorAndValidSizeRange() {
        RDG.map(randomIntegers(), null, fixed(15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateMapForNullSizeRange() {
        RDG.map(randomIntegers(), randomIntegers(), null);
    }

    private static Generator<? extends Type> ofConcreteTypes() {
        return new Generator<Type>() {
            @Override
            public Type next() {
                return new ConcreteType();
            }
        };
    }

    private static interface Type {
    }

    private static final class ConcreteType implements Type {
    }
}
