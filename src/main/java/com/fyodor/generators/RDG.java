package com.fyodor.generators;

import com.google.common.collect.Range;

import java.util.Random;

public class RDG {

    static Random random = new Random();

    public static Generator<Integer> integer = integer(Integer.MAX_VALUE);

    public static Generator<Integer> integer(Integer max) {
        return new IntegerGenerator(max);
    }

    public static Generator<Integer> integer(Range<Integer> range) {
        return new IntegerGenerator(range);
    }
}
