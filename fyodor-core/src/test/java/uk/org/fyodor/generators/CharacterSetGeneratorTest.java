package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTest;
import uk.org.fyodor.generators.characters.CharacterFilter;
import uk.org.fyodor.generators.characters.CharacterSetGenerator;
import uk.org.fyodor.range.Range;

import static uk.org.fyodor.FyodorAssertions.assertThat;

public class CharacterSetGeneratorTest extends BaseTest {

    CharacterFilter allowEverythingFilter = c -> true;

    @Test
    public void generateCorrectNumberOfChars(){
        Integer rangeStart = RDG.integer(50).next();
        Integer rangeEnd = RDG.integer(Range.closed(51, 1000)).next();
        CharacterSetGenerator generator = new CharacterSetGenerator(allowEverythingFilter, Range.closed(rangeStart, rangeEnd));
        assertThat(generator.getCharset()).hasSize(rangeEnd - rangeStart + 1);
    }
}
