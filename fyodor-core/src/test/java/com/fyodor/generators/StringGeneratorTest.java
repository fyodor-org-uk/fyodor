package com.fyodor.generators;

import com.fyodor.generators.characters.AllCharactersFilter;
import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.LettersAndDigitsFilter;
import com.fyodor.generators.characters.LettersOnlyFilter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;

import static com.fyodor.generators.RandomValuesProvider.randomValues;
import static org.assertj.core.api.Assertions.assertThat;

public class StringGeneratorTest {

    private Multiset<Character> chars;
    private static final int SIZE_OF_RANDOM_STRING = 50;

    @Before
    public void charSet() {
        chars = HashMultiset.create();
    }

    @Test
    public void lettersOnly() {
        generateRandomStringsForCharSet(LettersOnlyFilter.getFilter());
    }

    @Test
    public void lettersAndNumbersOnly() {
        generateRandomStringsForCharSet(LettersAndDigitsFilter.getFilter());
    }

    @Test
    public void allCharsString() {
        generateRandomStringsForCharSet(AllCharactersFilter.getFilter());
    }

    @Test
    public void cannotChangeCharset() {
        StringGenerator generator = new StringGenerator(SIZE_OF_RANDOM_STRING);
        Character[] chars = generator.getCharSet();
        chars[randomValues().randomInteger(chars.length)] = null;
        assertThat(chars).isNotEqualTo(generator.getCharSet());
    }

    private void generateRandomStringsForCharSet(CharacterFilter filter) {
        StringGenerator generator = new StringGenerator(SIZE_OF_RANDOM_STRING, filter);
        for (int i = 0; i < 1000; i++) {
            String val = generator.next();
            assertThat(val).hasSize(SIZE_OF_RANDOM_STRING);
            for (char c : val.toCharArray()) {
                chars.add(c);
            }
        }
        assertThat(chars.elementSet()).hasSize(generator.getCharSet().length);
        for (char c : generator.getCharSet()) {
            assertThat(chars.elementSet()).contains(c);
        }
    }
}
