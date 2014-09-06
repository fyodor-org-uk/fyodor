package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.generators.collections.ArrayGenerator;
import com.fyodor.generators.collections.ListGenerator;
import com.fyodor.generators.collections.MapGenerator;
import com.fyodor.generators.collections.SetGenerator;
import com.fyodor.range.Range;

import java.util.*;

import static com.fyodor.random.RandomValuesProvider.randomValues;
import static com.fyodor.range.Range.fixed;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class RDG {

    public static Generator<Integer> integer = integer(Integer.MAX_VALUE);

    public static Generator<Integer> integer(Integer max) {
        return new IntegerGenerator(max);
    }

    public static Generator<Integer> integer(Range<Integer> range) {
        return new IntegerGenerator(range);
    }

    public static Generator<String> string = string(30);

    public static Generator<String> string(Integer max) {
        return new StringGenerator(max);
    }

    public static Generator<String> string(Integer max, CharacterFilter filter) {
        return new StringGenerator(max, filter);
    }

    public static Generator<String> string(Integer max, Range<Integer> range) {
        return new StringGenerator(max, range);
    }

    public static Generator<String> string(Integer max, CharacterSetGenerator generator) {
        return new StringGenerator(max, generator);
    }

    public static Generator<String> string(Integer max, Collection<Character> chars) {
        return new StringGenerator(max, chars);
    }

    public static Generator<String> string(Integer max, Character[] chars) {
        return new StringGenerator(max, chars);
    }

    public static <T extends Enum<T>> Generator<T> value(final Class<T> classOfEnumT) {
        cannotBeNull(classOfEnumT, "enum class cannot be null");

        final T[] enumConstants = classOfEnumT.getEnumConstants();
        satisfies(enumConstants.length > 0, format("enum %s does not have any constants", classOfEnumT));

        return value(enumConstants);
    }

    public static <T> Generator<T> value(final T first, T... arrayOfTs) {
        cannotBeNull(arrayOfTs, "varargs array of values cannot be null");

        final List<T> listOfTs = new ArrayList<T>(asList(arrayOfTs));
        listOfTs.add(0, first);
        return value(listOfTs);
    }

    public static <T> Generator<T> value(final T[] arrayOfTs) {
        cannotBeNull(arrayOfTs, "array of values cannot be null");
        satisfies(arrayOfTs.length > 0, "array of values cannot be empty");

        return value(asList(arrayOfTs));
    }

    public static <T> Generator<T> value(final Iterable<T> iterableOfT) {
        cannotBeNull(iterableOfT, "values cannot be null");
        satisfies(iterableOfT.iterator().hasNext(), "there must be at-least one value");

        return new ValueGenerator<T>(randomValues(), iterableOfT);
    }

    public static Generator<String> domainSuffix() {
        return new SuffixGenerator();
    }

    public static Generator<String> emailAddress() {
        return new EmailAddressGenerator();
    }

    public static Generator<String> domain() {
        return new DomainGenerator();
    }

    public static Generator<String> domain(Range<Integer> range) {
        return new DomainGenerator(range);
    }

    private static void cannotBeNull(final Object argument, final String message) {
        if (argument == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void satisfies(final boolean check, final String message) {
        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT) {
        return list(generatorOfT, 15);
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT, final int size) {
        return list(generatorOfT, fixed(size));
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT,
                                              final Range<Integer> sizeRange) {
        cannotBeNull(generatorOfT, "generator cannot be null");
        cannotBeNull(sizeRange, "size range cannot be null");

        return new ListGenerator<T>(randomValues(), generatorOfT, sizeRange);
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT,
                                           final Generator<? extends T> generatorOfT) {
        return array(classOfT, generatorOfT, 15);
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT,
                                           final Generator<? extends T> generatorOfT,
                                           final int size) {
        return array(classOfT, generatorOfT, fixed(size));
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT,
                                           final Generator<? extends T> generatorOfT,
                                           final Range<Integer> sizeRange) {
        cannotBeNull(classOfT, "type of array elements cannot be null");
        cannotBeNull(generatorOfT, "generator cannot be null");
        cannotBeNull(sizeRange, "size range cannot be null");

        return new ArrayGenerator<T>(randomValues(), classOfT, generatorOfT, sizeRange);
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT) {
        return set(generatorOfT, 15);
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT, final int size) {
        return set(generatorOfT, fixed(size));
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT,
                                            final Range<Integer> sizeRange) {
        cannotBeNull(generatorOfT, "generator cannot be null");
        cannotBeNull(sizeRange, "size range cannot be null");

        return new SetGenerator<T>(randomValues(), generatorOfT, sizeRange);
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK,
                                                  final Generator<? extends V> generatorOfV) {
        return map(generatorOfK, generatorOfV, 15);
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK,
                                                  final Generator<? extends V> generatorOfV,
                                                  final int size) {
        return map(generatorOfK, generatorOfV, fixed(size));
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK,
                                                  final Generator<? extends V> generatorOfV,
                                                  final Range<Integer> sizeRange) {
        cannotBeNull(generatorOfK, "key generator cannot be null");
        cannotBeNull(generatorOfV, "value generator cannot be null");
        cannotBeNull(sizeRange, "size range cannot be null");

        return new MapGenerator<K, V>(randomValues(), generatorOfK, generatorOfV, sizeRange);
    }

    private RDG() {
    }
}
