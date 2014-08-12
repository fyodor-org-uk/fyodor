package com.fyodor.generators;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringGeneratorTest {

    Multiset<Character> chars;

    @Before
    public void charSet(){
        chars = HashMultiset.create();
    }

    @Test
    public void lettersOnly(){
        generateRandomStringsForCharSet(StringGenerator.CharSet.LettersOnly);
    }

    @Test
    public void lettersAndNumbersOnly(){
        generateRandomStringsForCharSet(StringGenerator.CharSet.LettersAndNumbers);
    }

    @Test
    public void allCharsString() {
        generateRandomStringsForCharSet(StringGenerator.CharSet.AllChars);
    }

    private void generateRandomStringsForCharSet(StringGenerator.CharSet charSet) {
        Generator<String> generator = new StringGenerator(50, charSet);
        for (int i = 0; i < 1000; i++){
            String val = generator.next();
            for (char c : val.toCharArray()) {
                chars.add(c);
            }
        }
        assertThat(chars.elementSet()).hasSize(charSet.getCharset().length);
        for (char c : charSet.getCharset()) {
            assertThat(chars.elementSet()).contains(c);
        }
    }
}
