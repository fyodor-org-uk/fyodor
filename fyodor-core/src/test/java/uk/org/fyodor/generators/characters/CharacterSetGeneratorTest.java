package uk.org.fyodor.generators.characters;

import org.junit.Test;

import static uk.org.fyodor.FyodorAssertions.assertThat;

public class CharacterSetGeneratorTest {

    CharacterFilter filter = new CharacterFilter() {
        @Override
        public boolean includeCharacter(char c) {
            return 'A' == c || 'B' == c;
        }
    };

    @Test
    public void onlyValidCharsInArray() {
        CharacterSetGenerator generator = new CharacterSetGenerator(filter);
        assertThat(generator.getCharset())
                .hasSize(2)
                .containsOnly('A', 'B');
    }

    @Test
    public void canTakeMultipleRanges() {
        CharacterSetGenerator generator = new CharacterSetGenerator(CharacterSetRange.values());
        assertThat(generator.getCharset()).hasSize(525);
        System.out.println(generator.getCharset());
    }
}
