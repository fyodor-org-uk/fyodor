package com.fyodor.generators.collections;

import com.fyodor.generators.Generator;

import java.util.Random;

final class GeneratorUtils {

    static <T> Generator<T> generator(final T... arrayOfTs) {
        return new Generator<T>() {

            private int index = 0;

            @Override
            public T next() {
                if (index == arrayOfTs.length) {
                    index = 0;
                }
                return arrayOfTs[index++];
            }
        };
    }

    static Generator<Integer> randomIntegers() {
        return new Generator<Integer>() {

            private final Random random = new Random();

            @Override
            public Integer next() {
                return random.nextInt();
            }
        };
    }

    private GeneratorUtils() {
    }
}
