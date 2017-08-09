package uk.org.fyodor.random;

import java.util.Random;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_UP;

final class DefaultRandomValues implements RandomValues {

    private final Random random;

    DefaultRandomValues(final Random random) {
        this.random = random;
    }

    @Override
    public boolean randomBoolean() {
        return random.nextBoolean();
    }

    @Override
    public int randomInteger(final int lower, final int upper) {
        satisfies(lower <= upper, "the upper bound must be equal to or greater than the lower bound");

        if (lower == upper) {
            return lower;
        }

        int diff = upper - lower;
        if (diff >= 0 && diff != Integer.MAX_VALUE) {
            return (lower + random.nextInt(diff + 1));
        }
        int i;
        do {
            i = random.nextInt();
        } while (i < lower || i > upper);
        return i;
    }

    @Override
    public long randomLong(final long lower, final long upper) {
        satisfies(lower <= upper, "the upper bound must be equal to or greater than the lower bound");

        if (lower == upper) {
            return lower;
        }

        final long diff = upper - lower;
        if (diff >= 0 && diff != Integer.MAX_VALUE) {
            return (lower + randomLong(diff + 1));
        }
        long i;
        do {
            i = random.nextLong();
        } while (i < lower || i > upper);
        return i;
    }

    @Override
    public double randomDouble(final double lower, final double upper) {
        satisfies(lower <= upper, "the upper bound must be equal to or greater than the lower bound");

        if (lower == upper) {
            return lower;
        }

        final double next = random.nextDouble();

        return next * upper + (1.0 - next) * lower;
    }

    @Override
    public double randomDouble(final double lower, final double upper, final int scale) {
        satisfies(scale >= 0, "scale cannot be negative");

        final double unscaledRandomDouble = randomDouble(lower, upper);

        final double scaledHalfUp = scaleAndRoundHalfUp(unscaledRandomDouble, scale);

        return scaledHalfUp > upper
                ? scaleAndRoundDown(unscaledRandomDouble, scale)
                : scaledHalfUp;
    }

    @Override
    public byte randomByte(final byte lower, final byte upper) {
        return (byte) randomInteger(lower, upper);
    }

    @Override
    public byte[] randomBytes(final int length) {
        final byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public short randomShort(final short lower, final short upper) {
        return (short) randomInteger(lower, upper);
    }

    private long randomLong(final long max) {
        long bits, val;
        do {
            bits = (random.nextLong() << 1) >>> 1;
            val = bits % max;
        } while (bits - val + (max - 1) < 0L);
        return val;
    }

    private static double scaleAndRoundHalfUp(final double unscaledRandomDouble, final int scale) {
        return valueOf(unscaledRandomDouble).setScale(scale, HALF_UP).doubleValue();
    }

    private static double scaleAndRoundDown(final double unscaledRandomDouble, final int scale) {
        return valueOf(unscaledRandomDouble).setScale(scale, DOWN).doubleValue();
    }

    private static void satisfies(final boolean check, final String message) {
        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }
}
