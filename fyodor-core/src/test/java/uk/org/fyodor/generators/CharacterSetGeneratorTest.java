package uk.org.fyodor.generators;

import uk.org.fyodor.BaseTestWithRule;
import uk.org.fyodor.generators.characters.CharacterFilter;
import uk.org.fyodor.generators.characters.CharacterSetGenerator;
import uk.org.fyodor.range.Range;
import org.junit.Test;

import static uk.org.fyodor.FyodorAssertions.assertThat;

public class CharacterSetGeneratorTest extends BaseTestWithRule {

    CharacterFilter allowEverythingFilter = new CharacterFilter() {
        @Override
        public boolean includeCharacter(char c) {
            return true;
        }
    };

    @Test
    public void generateCorrectNumberOfChars(){
        Integer rangeStart = RDG.integer(50).next();
        Integer rangeEnd = RDG.integer(Range.closed(51, 1000)).next();
        CharacterSetGenerator generator = new CharacterSetGenerator(allowEverythingFilter, Range.closed(rangeStart, rangeEnd));
        assertThat(generator.getCharset()).hasSize(rangeEnd - rangeStart + 1);
    }
}
