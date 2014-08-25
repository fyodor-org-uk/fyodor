package com.fyodor.internal;

public final class Preconditions {

    public static void checkArgument(final boolean check, final String message) {
        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArgumentIsNotNull(final Object arg, final String message) {
        if (arg == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private Preconditions() {
    }
}
