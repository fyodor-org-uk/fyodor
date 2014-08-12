package com.fyodor.generators;

public class BooleanGenerator implements Generator<Boolean> {

    @Override
    public Boolean next() {
        return RDG.random.nextBoolean();
    }
}
