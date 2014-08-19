package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.google.common.collect.Range;
import org.joda.time.LocalDate;

import java.util.Random;

public class RDG {

    static Random random = new Random();

    public static Generator<String> string = string(30);
    public static Generator<Integer> integer = integer(Integer.MAX_VALUE);

    public static Generator<String> string(Integer max) {
        return new StringGenerator(max);
    }

    public static Generator<String> string(Integer max, CharacterFilter filter) {
        return new StringGenerator(max, filter);
    }

    public static Generator<Integer> integer(Integer max) {
        return new IntegerGenerator(max);
    }

    public static Generator<Integer> integer(Range<Integer> range) {
        return new IntegerGenerator(range);
    }

    public static Generator<LocalDate> localDate(final Range<LocalDate> range) {
        return new LocalDateGenerator(new DefaultRandomValues(random), range);
    }
}
