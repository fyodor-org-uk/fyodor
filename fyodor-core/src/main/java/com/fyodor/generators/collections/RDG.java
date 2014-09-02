package com.fyodor.generators.collections;

import com.fyodor.generators.Generator;
import com.fyodor.range.Range;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fyodor.random.RandomValuesProvider.randomValues;
import static com.fyodor.range.Range.fixed;

public final class RDG {

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

    private static void cannotBeNull(final Object argument, final String message) {
        if (argument == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
