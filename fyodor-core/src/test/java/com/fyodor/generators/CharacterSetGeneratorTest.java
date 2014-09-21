package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.range.Range;
import org.junit.Test;

import static com.fyodor.FyodorAssertions.assertThat;

public class CharacterSetGeneratorTest {

    CharacterFilter allowEverythingFilter = new CharacterFilter() {
        @Override
        public boolean includeCharacter(int i) {
            return true;
        }
    };

    @Test
    public void generateCorrectNumberOfChars(){
        Integer rangeStart = RDG.integer(50).next();
        Integer rangeEnd = RDG.integer(Range.closed(51, 1000)).next();
        CharacterSetGenerator generator = new CharacterSetGenerator(Range.closed(rangeStart, rangeEnd), allowEverythingFilter);
        assertThat(generator.getCharset()).hasSize(rangeEnd - rangeStart + 1);
    }
}
