package com.fyodor.generators;

class LongGenerator implements Generator<Long> {

    @Override
    public Long next() {
        return RDG.random.nextLong();
    }
}
