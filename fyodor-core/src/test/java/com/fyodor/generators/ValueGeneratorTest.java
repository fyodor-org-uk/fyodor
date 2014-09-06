package com.fyodor.generators;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.fyodor.Sampler.Sample;
import static com.fyodor.Sampler.from;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

public final class ValueGeneratorTest {

    @Test
    public void generatesValuesFromListOfStrings() {
        final List<String> source = asList("a", "b", "c", "d");
        final Generator<String> generator = RDG.value(source);
        assertThat(from(generator).sample(1000).unique())
                .containsAll(source);
    }

    @Test
    public void generatesSingleValueForListWithOneElement() {
        final Generator<String> generator = RDG.value(asList("a"));
        assertThat(from(generator).sample(1000).unique())
                .containsExactly("a");
    }

    @Test
    public void generatesValuesFromSetOfStrings() {
        final Set<String> source = newHashSet("a", "b", "c", "d");
        final Generator<String> generator = RDG.value(source);
        assertThat(from(generator).sample(1000).unique())
                .containsAll(source);
    }

    @Test
    public void generatesSingleValueForSetWithOneElement() {
        final Generator<String> generator = RDG.value(newHashSet("a"));
        assertThat(from(generator).sample(1000).unique())
                .containsExactly("a");
    }

    @Test
    public void generatesValuesFromArray() {
        final Integer[] source = new Integer[] {1, 5, 7, 3, 5, 7, 8};
        final Generator<Integer> generator = RDG.value(source);
        assertThat(from(generator).sample(1000).unique())
                .contains(source);
    }

    @Test
    public void generatesSingleValueForArrayWithOneElement() {
        final Generator<Integer> generator = RDG.value(new Integer[] {5});
        assertThat(from(generator).sample(1000).unique())
                .containsOnly(5);
    }

    @Test
    public void generatesValuesFromVarArgs() {
        final Generator<Integer> generator = RDG.value(100, 1, 2, 3, 4, 5);
        assertThat(from(generator).sample(1000).unique())
                .containsOnly(100, 1, 2, 3, 4, 5);
    }

    @Test
    public void generatesSingleValueWhenVarArgsIsEmpty() {
        final Generator<Integer> generator = RDG.value(100);
        assertThat(from(generator).sample(1000).unique())
                .containsOnly(100);
    }

    @Test
    public void generatedValuesAreNotTheSameEachTimeForSourceList() {
        final List<String> source = asList("a", "b", "c", "d");

        final Sample<String> first = from(RDG.value(source)).sample(10);
        final Sample<String> second = from(RDG.value(source)).sample(10);
        final Sample<String> third = from(RDG.value(source)).sample(10);

        assertThat(first).isNotEqualTo(second);
        assertThat(first).isNotEqualTo(third);
        assertThat(second).isNotEqualTo(third);
    }

    @Test
    public void generatedValuesAreNotTheSameEachTimeForSourceSet() {
        final Set<String> source = newHashSet("a", "b", "c", "d");

        final Sample<String> first = from(RDG.value(source)).sample(10);
        final Sample<String> second = from(RDG.value(source)).sample(10);
        final Sample<String> third = from(RDG.value(source)).sample(10);

        assertThat(first).isNotEqualTo(second);
        assertThat(first).isNotEqualTo(third);
        assertThat(second).isNotEqualTo(third);
    }

    @Test
    public void generatedValuesAreNotTheSameEachTimeForSourceArray() {
        final Integer[] source = new Integer[] {1, 5, 7, 3, 5, 7, 8};

        final Sample<Integer> first = from(RDG.value(source)).sample(10);
        final Sample<Integer> second = from(RDG.value(source)).sample(10);
        final Sample<Integer> third = from(RDG.value(source)).sample(10);

        assertThat(first).isNotEqualTo(second);
        assertThat(first).isNotEqualTo(third);
        assertThat(second).isNotEqualTo(third);
    }

    @Test
    public void generatedValuesAreNotTheSameEachTimeForSourceVarArgs() {
        final Sample<Integer> first = from(RDG.value(1, 5, 7, 3, 5, 7, 8)).sample(10);
        final Sample<Integer> second = from(RDG.value(1, 5, 7, 3, 5, 7, 8)).sample(10);
        final Sample<Integer> third = from(RDG.value(1, 5, 7, 3, 5, 7, 8)).sample(10);

        assertThat(first).isNotEqualTo(second);
        assertThat(first).isNotEqualTo(third);
        assertThat(second).isNotEqualTo(third);
    }

    @Test
    public void canGenerateNullValuesFromNullListElements() {
        final List<Integer> source = asList(1, null, 3, 4, 5, null, 6);
        final Generator<Integer> generator = RDG.value(source);
        assertThat(from(generator).sample(1000).unique())
                .containsAll(source);
    }

    @Test
    public void canGenerateNullValuesFromNullSetElements() {
        final Set<Integer> source = newHashSet(1, null, 3, 4, 5, 6);
        final Generator<Integer> generator = RDG.value(source);
        assertThat(from(generator).sample(1000).unique())
                .containsAll(source);
    }

    @Test
    public void canGenerateNullValuesFromNullArrayElements() {
        final Integer[] source = new Integer[] {1, null, 7, 3, null, 7, 8};
        final Generator<Integer> generator = RDG.value(source);
        assertThat(from(generator).sample(1000).unique())
                .contains(source);
    }

    @Test
    public void canGenerateNullValuesFromNullVarArgsElements() {
        final Generator<Integer> generator = RDG.value(1, null, 3, 4, 5, null, 6);
        assertThat(from(generator).sample(1000).unique())
                .contains(1, null, 3, 4, 5, null, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromAnEmptyList() {
        RDG.value(emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromAnEmptySet() {
        RDG.value(emptySet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromAnEmptyArray() {
        RDG.value(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromNullList() {
        RDG.value((List) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromNullSet() {
        RDG.value((Set) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromNullArray() {
        RDG.value((String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromNullVarArgs() {
        //noinspection NullArgumentToVariableArgMethod
        RDG.value(1, null);
    }

    @Test
    public void generatesEnumValues() {
        final Generator<SampleEnum> generator = RDG.value(SampleEnum.class);
        assertThat(from(generator).sample(1000).unique())
                .hasSize(SampleEnum.values().length)
                .contains(SampleEnum.values());
    }

    @Test
    public void generatedEnumValuesAreNotTheSameEachTime() {
        final Sample<SampleEnum> first = from(RDG.value(SampleEnum.class)).sample(10);
        final Sample<SampleEnum> second = from(RDG.value(SampleEnum.class)).sample(10);
        final Sample<SampleEnum> third = from(RDG.value(SampleEnum.class)).sample(10);

        assertThat(first).isNotEqualTo(second);
        assertThat(first).isNotEqualTo(third);
        assertThat(second).isNotEqualTo(third);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesForNullEnumClass() {
        RDG.value((Class<SampleEnum>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGenerateValuesFromEnumWithZeroConstants() {
        RDG.value(EmptyEnum.class);
    }

    private static enum SampleEnum {
        A, B, C, D, E, F
    }

    private static enum EmptyEnum {}
}
