package com.fyodor.generators.characters;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CharacterSetGenerator {

    private final Character[] charset;

    public CharacterSetGenerator(Range<Integer> range, CharacterFilter filter) {
        List<Character> chars = new LinkedList<Character>();

        for (int i = (range.lowerBoundType() == BoundType.CLOSED ?
                range.lowerEndpoint() :
                range.lowerEndpoint() + 1);
             i <= (range.upperBoundType() == BoundType.CLOSED ?
                     range.upperEndpoint() :
                     range.upperEndpoint() - 1);
             i++) {
            if (filter.includeCharacter(i)) {
                chars.add((char) i);
            }
        }
        this.charset = chars.toArray(new Character[chars.size()]);
    }

    public Character[] getCharset() {
        return Arrays.copyOf(charset, charset.length);
    }
}
