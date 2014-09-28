package uk.org.fyodor.generators.characters;

import uk.org.fyodor.range.Range;

import java.util.Arrays;

public class CharacterSetGenerator {

    public static final Range<Integer> basicLatinRange = Range.closed(33, 126);
    public static final Range<Integer> latin1Range = Range.closed(160, 255);
    public static final Range<Integer> latinExtendedARange = Range.closed(256, 383);
    public static final Range<Integer> latinExtendedBRange = Range.closed(384, 591);
    public static final CharacterFilter defaultFilter = AllCharactersFilter.getFilter();
    private final char[] charset;

    public CharacterSetGenerator() {
        this(defaultFilter, basicLatinRange);
    }

    public CharacterSetGenerator(Range<Integer>... ranges) {
        this(defaultFilter, ranges);
    }

    public CharacterSetGenerator(CharacterFilter filter) {
        this(filter, basicLatinRange);
    }

    public CharacterSetGenerator(CharacterFilter filter, Range<Integer>... ranges) {

        char[] charset = new char[getTotalSizeOfRanges(ranges)];

        int j = 0;
        for (Range<Integer> range : ranges) {
            for (int i = range.lowerBound(); i <= range.upperBound(); i++) {
                if (filter.includeCharacter(i)) {
                    charset[j] = (char) i;
                    j++;
                }
            }
        }
        this.charset=Arrays.copyOf(charset,j);
    }

    private int getTotalSizeOfRanges(Range<Integer>[] ranges) {
        int rangesSize = 0;
        for (Range<Integer> range : ranges) {
            rangesSize += range.upperBound() - range.lowerBound() + 1;
        }
        return rangesSize;
    }

    public char[] getCharset() {
        return Arrays.copyOf(charset, charset.length);
    }
}
