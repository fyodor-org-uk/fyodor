package com.fyodor.generators.characters;

import com.fyodor.range.Range;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CharacterSetGenerator {

    private final Character[] charset;

    public CharacterSetGenerator(Range<Integer> range, CharacterFilter filter) {
        List<Character> chars = new LinkedList<Character>();

        for (int i = range.lowerBound(); i < range.upperBound(); i++) {
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
