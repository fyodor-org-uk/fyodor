package com.fyodor.generators;

import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

final class DefaultRandomValues implements RandomValues {

    private final Random random;

    DefaultRandomValues(final Random random) {
        this.random = random;
    }

    @Override
    public long randomLong(final long lower, final long upper) {
        checkArgument(lower <= upper, "the upper bound must be equal to or greater than the lower bound");

        if (lower == upper) {
            return lower;
        }

        return randomLong(upper - lower + 1) + lower;
    }

    private long randomLong(final long max) {
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % max;
        } while (bits - val + (max - 1) < 0L);
        return val;
    }
}
