package com.fyodor.generators.characters;

import com.fyodor.range.Range;

import java.util.Arrays;

public class CharacterSetGenerator {

    public static final Range<Integer> defaultRange = Range.closed(33, 126);
    public static final CharacterFilter defaultFilter = AllCharactersFilter.getFilter();
    private final char[] charset;

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
        char[] charset = new char[range.upperBound() - range.lowerBound() + 1];

        int j = 0;
        for (int i = range.lowerBound(); i <= range.upperBound(); i++) {
            if (filter.includeCharacter(i)) {
                charset[j] = (char) i;
                j++;
            }
        }
        this.charset = Arrays.copyOf(charset, j);
    }

    public char[] getCharset() {
        return Arrays.copyOf(charset, charset.length);
    }
}
