package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.range.Range;

import java.util.Arrays;
import java.util.Collection;

import static com.fyodor.generators.characters.CharacterSetGenerator.defaultFilter;
import static com.fyodor.generators.characters.CharacterSetGenerator.defaultRange;
import static com.fyodor.random.RandomValuesProvider.randomValues;

class StringGenerator implements Generator<String> {

    private Integer length;
    private Character[] charSet;

    StringGenerator(Integer length) {
        this(length, new CharacterSetGenerator(defaultRange, defaultFilter));
    }

    StringGenerator(Integer length, CharacterFilter filter) {
        this(length, new CharacterSetGenerator(defaultRange, filter));
    }

    StringGenerator(Integer length, Range<Integer> range) {
        this(length, new CharacterSetGenerator(range, defaultFilter));
    }

    StringGenerator(Integer length, CharacterSetGenerator characterSetGenerator) {
        this(length, characterSetGenerator.getCharset());
    }

    StringGenerator(Integer length, Collection<Character> charset) {
        this(length, charset.toArray(new Character[charset.size()]));
    }

    StringGenerator(Integer length, Character[] charset) {
        this.length = length;
        this.charSet = charset;
    }

    @Override
    public String next() {
        char[] ret = new char[length];
        for (int i = 0; i < length; i++) {
            ret[i] = charSet[randomValues().randomInteger(charSet.length)];
        }
        return String.valueOf(ret);
    }

    public Character[] getCharSet() {
        return Arrays.copyOf(charSet, charSet.length);
    }
}