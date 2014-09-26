package uk.org.fyodor;

import uk.org.fyodor.generators.characters.CharacterFilterAssert;
import uk.org.fyodor.generators.characters.CharacterFilter;
import org.assertj.core.api.Assertions;

public class FyodorAssertions extends Assertions {

    public static CharacterFilterAssert assertThat(CharacterFilter characterFilter) {
        return new CharacterFilterAssert(characterFilter);
    }
}
