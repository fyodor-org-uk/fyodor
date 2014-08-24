package com.fyodor.generators;

import static com.fyodor.generators.RandomValuesProvider.randomValues;

class BooleanGenerator implements Generator<Boolean> {

    @Override
    public Boolean next() {
        return randomValues().randomBoolean();
    }
}
