package com.fyodor.generators.characters;

import org.junit.Test;

import static com.fyodor.FyodorAssertions.assertThat;

public class CharacterSetGeneratorTest {

    CharacterFilter filter = new CharacterFilter() {
        @Override
        public boolean includeCharacter(int i) {
            return 'A' == (char) i || 'B' == (char) i;
        }
    };

    @Test
    public void onlyValidCharsInArray() {
        CharacterSetGenerator generator = new CharacterSetGenerator(filter);
        assertThat(generator.getCharset())
                .hasSize(2)
                .containsOnly('A', 'B');
    }
}
