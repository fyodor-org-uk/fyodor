package com.fyodor.generators.characters;

import com.fyodor.range.Range;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CharacterSetGenerator {

    public static final Range<Integer> defaultRange = Range.closed(33, 126);
    public static final CharacterFilter defaultFilter = AllCharactersFilter.getFilter();
    private final Character[] charset;

    public CharacterSetGenerator(){
        this(defaultRange, defaultFilter);
    }

    public CharacterSetGenerator(Range<Integer> range) {
        this(range, defaultFilter);
    }

    public CharacterSetGenerator(CharacterFilter filter) {
        this(defaultRange, filter);
    }

    public CharacterSetGenerator(Range<Integer> range, CharacterFilter filter) {
        List<Character> chars = new LinkedList<Character>();

        for (int i = range.lowerBound(); i <= range.upperBound(); i++) {
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
