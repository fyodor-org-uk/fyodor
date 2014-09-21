package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.range.Range;

import java.util.Arrays;

import static com.fyodor.generators.characters.CharacterSetGenerator.defaultFilter;
import static com.fyodor.generators.characters.CharacterSetGenerator.defaultRange;
import static com.fyodor.random.RandomValuesProvider.randomValues;

class StringGenerator implements Generator<String> {

    private Range<Integer> range;
    private char[] charSet;

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

    StringGenerator(Integer length, char[] charset) {
        this(Range.fixed(length), charset);
    }

    StringGenerator(Range<Integer> range) {
        this(range, new CharacterSetGenerator(defaultRange, defaultFilter));
    }

    StringGenerator(Range<Integer> range, CharacterFilter filter) {
        this(range, new CharacterSetGenerator(defaultRange, filter));
    }

    StringGenerator(Range<Integer> stringRange, Range<Integer> charsetRange) {
        this(stringRange, new CharacterSetGenerator(charsetRange, defaultFilter));
    }

    StringGenerator(Range<Integer> range, CharacterSetGenerator characterSetGenerator) {
        this(range, characterSetGenerator.getCharset());
    }

    StringGenerator(Integer length, String charset) {
        this(Range.fixed(length), charset.toCharArray());
    }

    StringGenerator(Range<Integer> range, char[] charset) {
        this.range = range;
        this.charSet = charset;
    }

    @Override
    public String next() {
        Integer length = RDG.integer(range).next();
        char[] ret = new char[length];
        for (int i = 0; i < length; i++) {
            ret[i] = charSet[randomValues().randomInteger(charSet.length - 1)];
        }
        return String.valueOf(ret);
    }

    public char[] getCharSet() {
        return Arrays.copyOf(charSet, charSet.length);
    }
}
