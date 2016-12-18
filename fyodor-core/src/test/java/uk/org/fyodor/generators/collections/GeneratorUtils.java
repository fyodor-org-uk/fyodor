package uk.org.fyodor.generators.collections;

import uk.org.fyodor.generators.Generator;

import java.util.Iterator;
import java.util.Random;

import static java.util.Arrays.asList;

final class GeneratorUtils {

    private GeneratorUtils() {
    }

    @SafeVarargs
    static <T> Generator<T> generatingFrom(final T... arrayOfTs) {
        return generatingFrom(asList(arrayOfTs));
    }

    @SafeVarargs
    static <T> Generator<T> endlesslyGeneratingFrom(final T... arrayOfTs) {
        return new Generator<T>() {
            int index = 0;

            @Override
            public T next() {
                if (index == arrayOfTs.length) {
                    index = 0;
                }
                return arrayOfTs[index++];
            }
        };
    }

    static <T> Generator<T> generatingFrom(final Iterable<T> iterableOfT) {
        final Iterator<T> iterator = iterableOfT.iterator();
        return iterator::next;
    }

    static Generator<Integer> generatingRandomIntegers() {
        final Random random = new Random();
        return random::nextInt;
    }
}
