package uk.org.fyodor.generators;

public final class Validations {
    public static void ensure(final boolean check, final String message) {
        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isNotNull(final Object value) {
        return value != null;
    }

    static boolean isNumber(final double value) {
        return value == value;
    }

    static boolean isNotInfinite(final double value) {
        return value != Double.NEGATIVE_INFINITY &&
                value != Double.POSITIVE_INFINITY;
    }

    static boolean isNotNegative(final int value) {
        return value >= 0;
    }

    static boolean isNotNegative(final double value) {
        return value >= 0;
    }

    static boolean isNotNegative(final long value) {
        return value >= 0;
    }
}
