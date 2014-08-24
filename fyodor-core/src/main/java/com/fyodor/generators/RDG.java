package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.range.Range;

import java.util.Collection;

public class RDG {

    public static Generator<Integer> integer = integer(Integer.MAX_VALUE);

    public static Generator<Integer> integer(Integer max) {
        return new IntegerGenerator(max);
    }

    public static Generator<Integer> integer(Range<Integer> range) {
        return new IntegerGenerator(range);
    }

    public static Generator<String> string = string(30);

    public static Generator<String> string(Integer max) {
        return new StringGenerator(max);
    }

    public static Generator<String> string(Integer max, CharacterFilter filter) {
        return new StringGenerator(max, filter);
    }

    public static Generator<String> string(Integer max, Range<Integer> range) {
        return new StringGenerator(max, range);
    }

    public static Generator<String> string(Integer max, CharacterSetGenerator generator) {
        return new StringGenerator(max, generator);
    }

    public static Generator<String> string(Integer max, Collection<Character> chars) {
        return new StringGenerator(max, chars);
    }

    public static Generator<String> string(Integer max, Character[] chars) {
        return new StringGenerator(max, chars);
    }
}
