package com.fyodor.random;

public interface RandomValues {

    long randomLong();

    long randomLong(long lower, long upper);

    int randomInteger(int upper);

    int randomInteger(int lower, int upper);

    boolean randomBoolean();
}
