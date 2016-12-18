package uk.org.fyodor.generators.collections;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.generators.RDG;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.collections.GeneratorUtils.generatingFrom;
import static uk.org.fyodor.generators.collections.GeneratorUtils.generatingRandomIntegers;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public final class SetGeneratorTest extends BaseTestWithRule {

    private static final int MAX_SIZE = 1000;

    @Test
    public void generatesSetWithDefaultSize() {
        final Generator<Set<Integer>> generator = RDG.set(generatingFrom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        assertThat(generator.next())
                .hasSize(15)
                .containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    }

    @Test
    public void generatesSetWithFixedSize() {
        final Generator<Set<Integer>> generator = RDG.set(generatingFrom(1, 2, 3, 4, 5, 6), 5);
        assertThat(generator.next())
                .hasSize(5)
                .containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void generatesSetWithFixedSizeRange() {
        final Generator<Set<Integer>> generator = RDG.set(generatingFrom(1, 2, 3, 4, 5, 6), fixed(5));
        assertThat(generator.next())
                .hasSize(5)
                .containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void generatesSetWithClosedSizeRange() {
        final Generator<Set<Integer>> generator = RDG.set(generatingRandomIntegers(), closed(1, 5));
        for (final Set<Integer> sample : from(generator).sample(100)) {
            assertThat(sample.size()).isBetween(1, 5);
        }
    }

    @Test
    public void generatesSetOfSpecifiedSizeEvenWhenValueGeneratorReturnsDuplicates() {
        final Generator<Set<Integer>> generator = RDG.set(generatingFrom(1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4), 3);
        assertThat(generator.next())
                .hasSize(3)
                .containsExactly(1, 2, 3);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsAnExceptionWhenValueGeneratorGeneratesTooManyDuplicatesAndTheFullSetSizeCannotBeAchieved() {
        final Generator<Integer> fixedValueGenerator = () -> 1;
        RDG.set(fixedValueGenerator, 3).next();
        //Throws an exception because the value generator generates the same value too many times
    }

    @Test
    public void generatesEmptySetForNegativeSize() {
        assertThat(RDG.set(generatingFrom(1), -1).next()).isEmpty();
    }

    @Test
    public void generatesEmptySetForNegativeFixedRangeSize() {
        assertThat(RDG.set(generatingFrom(1), fixed(-1)).next()).isEmpty();
    }

    @Test
    public void generatesEmptySetForNegativeClosedRangeSize() {
        assertThat(RDG.set(generatingFrom(1), closed(-10, -1)).next()).isEmpty();
    }

    @Test
    public void generatesSetOfAtLeastZeroSizeForSizeRangeCoveringNegativeToPositive() {
        final Generator<Set<Integer>> generator = RDG.set(generatingRandomIntegers(), closed(-2, 2));
        for (final Set<Integer> set : from(generator).sample(1000)) {
            assertThat(set.size())
                    .isBetween(0, 2);
        }
    }

    @Test
    public void canGenerateSetOfObjectsOfType() {
        final Generator<Set<Type>> generator = RDG.set(randomConcreteTypes(), 10);
        for (final Type type : generator.next()) {
            assertThat(type).isInstanceOf(ConcreteType.class);
        }
    }

    @Test
    public void generatesSetAtTheMaximumSize() {
        final Generator<Set<Integer>> generator = RDG.set(generatingRandomIntegers(), fixed(1000));
        assertThat(generator.next()).hasSize(MAX_SIZE);
    }

    @Test
    public void doesNotGenerateSetOfElementsLargerThanTheMaximumSize() {
        final Generator<Set<Integer>> generator = RDG.set(generatingRandomIntegers(), fixed(5000));
        assertThat(generator.next()).hasSize(MAX_SIZE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateSetWithNullValueGeneratorForDefaultSize() {
        RDG.set(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateSetWithNullValueGeneratorAndValidSize() {
        RDG.set(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateSetWithNullValueGeneratorAndValidSizeRange() {
        RDG.set(null, fixed(50));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateSetWithNullSizeRangeAndValidGenerator() {
        RDG.set(generatingRandomIntegers(), null);
    }

    private Generator<? extends Type> randomConcreteTypes() {
        return (Generator<Type>) ConcreteType::new;
    }


    private interface Type {
    }

    private static final class ConcreteType implements Type {
    }
}
