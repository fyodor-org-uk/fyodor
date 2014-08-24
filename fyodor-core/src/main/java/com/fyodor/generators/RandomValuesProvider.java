package com.fyodor.generators;

import java.util.Random;

public final class RandomValuesProvider {

    public static RandomValues randomValues() {
        return new DefaultRandomValues(new Random());
    }

    private RandomValuesProvider() {
    }
}
