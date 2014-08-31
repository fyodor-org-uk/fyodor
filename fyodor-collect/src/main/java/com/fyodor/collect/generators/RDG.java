package com.fyodor.collect.generators;

import com.fyodor.generators.Generator;
import com.fyodor.range.Range;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fyodor.internal.Preconditions.checkArgumentIsNotNull;
import static com.fyodor.random.RandomValuesProvider.randomValues;
import static com.fyodor.range.Range.fixed;

public final class RDG {

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT) {
        return list(generatorOfT, 15);
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT, final int size) {
        return list(generatorOfT, fixed(size));
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        checkArgumentIsNotNull(generatorOfT, "generator cannot be null");
        checkArgumentIsNotNull(sizeRange, "size range cannot be null");

        return new ListGenerator<T>(randomValues(), generatorOfT, sizeRange);
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT, final Generator<? extends T> generatorOfT) {
        return array(classOfT, generatorOfT, 15);
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT, final Generator<? extends T> generatorOfT, final int size) {
        return array(classOfT, generatorOfT, fixed(size));
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT, final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        checkArgumentIsNotNull(classOfT, "type of array elements cannot be null");
        checkArgumentIsNotNull(generatorOfT, "generator cannot be null");
        checkArgumentIsNotNull(sizeRange, "size range cannot be null");

        return new ArrayGenerator<T>(classOfT, generatorOfT, sizeRange);
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT) {
        return set(generatorOfT, 15);
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT, final int size) {
        return set(generatorOfT, fixed(size));
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        checkArgumentIsNotNull(generatorOfT, "generator cannot be null");
        checkArgumentIsNotNull(sizeRange, "size range cannot be null");

        return new SetGenerator<T>(generatorOfT, sizeRange);
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK, final Generator<? extends V> generatorOfV) {
        return map(generatorOfK, generatorOfV, 15);
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK, final Generator<? extends V> generatorOfV, final int size) {
        return map(generatorOfK, generatorOfV, fixed(size));
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK, final Generator<? extends V> generatorOfV, final Range<Integer> sizeRange) {
        checkArgumentIsNotNull(generatorOfK, "key generator cannot be null");
        checkArgumentIsNotNull(generatorOfV, "value generator cannot be null");
        checkArgumentIsNotNull(sizeRange, "size range cannot be null");

        return new MapGenerator<K, V>(generatorOfK, generatorOfV, sizeRange);
    }

    private RDG() {
    }
}
