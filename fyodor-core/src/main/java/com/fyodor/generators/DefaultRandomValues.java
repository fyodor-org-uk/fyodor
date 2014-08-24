package com.fyodor.generators;

import java.util.Random;

final class DefaultRandomValues implements RandomValues {

    private final Random random;

    DefaultRandomValues(final Random random) {
        this.random = random;
    }

    @Override
    public long randomLong() {
        return random.nextLong();
    }

    @Override
    public long randomLong(final long lower, final long upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("the upper bound must be equal to or greater than the lower bound");
        }

        if (lower == upper) {
            return lower;
        }

        return randomLong(upper - lower + 1) + lower;
    }

    @Override
    public int randomInteger(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max cannot be negative");
        }

        return random.nextInt(max);
    }

    @Override
    public int randomInteger(final int lower, final int upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("the upper bound must be equal to or greater than the lower bound");
        }

        if (lower == upper) {
            return lower;
        }
        return random.nextInt(upper - lower + 1) + lower;
    }

    @Override
    public boolean randomBoolean() {
        return random.nextBoolean();
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
