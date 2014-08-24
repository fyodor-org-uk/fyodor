package com.fyodor.generators;

import static com.fyodor.generators.RandomValuesProvider.randomValues;

class LongGenerator implements Generator<Long> {

    @Override
    public Long next() {
        return randomValues().randomLong();
    }
}
