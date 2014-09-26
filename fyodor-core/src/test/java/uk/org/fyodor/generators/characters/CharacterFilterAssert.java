package uk.org.fyodor.generators.characters;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class CharacterFilterAssert extends AbstractAssert<CharacterFilterAssert, CharacterFilter> {

    public CharacterFilterAssert(CharacterFilter actual) {
        super(actual, CharacterFilterAssert.class);
    }

    public CharacterFilterAssert includes(String c) {
        assert (c.length() == 1);
        assertThat(actual.includeCharacter(c.charAt(0))).isTrue();
        return this;
    }

    public CharacterFilterAssert doesNotInclude(String c) {
        assert (c.length() == 1);
        assertThat(actual.includeCharacter(c.charAt(0))).isFalse();
        return this;
    }
}
