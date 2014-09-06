package com.fyodor.random;

final class ThrowableCauseSetter {

    static void setRootCause(final Throwable t, final Throwable cause) {
        if (t.getCause() == null) {
            t.initCause(cause);
        } else {
            setRootCause(t.getCause(), cause);
        }
    }

    private ThrowableCauseSetter() {
    }
}
