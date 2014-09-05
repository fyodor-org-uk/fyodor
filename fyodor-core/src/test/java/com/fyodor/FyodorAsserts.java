package com.fyodor;

import com.fyodor.generators.characters.CharacterFilterAssert;
import com.fyodor.generators.characters.CharacterFilter;
import org.assertj.core.api.Assertions;

public class FyodorAsserts extends Assertions {

    public static CharacterFilterAssert assertThat(CharacterFilter characterFilter) {
        return new CharacterFilterAssert(characterFilter);
    }
}
