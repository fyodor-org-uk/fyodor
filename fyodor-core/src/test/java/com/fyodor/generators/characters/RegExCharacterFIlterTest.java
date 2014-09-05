package com.fyodor.generators.characters;

import com.fyodor.generators.characters.RegExCharacterFilter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegExCharacterFIlterTest {

    @Test
    public void useRegEx(){
        RegExCharacterFilter filter = new RegExCharacterFilter("[a-z]");
        assertThat(filter.includeCharacter("a".charAt(0))).isTrue();
        assertThat(filter.includeCharacter("b".charAt(0))).isTrue();
        assertThat(filter.includeCharacter("c".charAt(0))).isTrue();
        assertThat(filter.includeCharacter("x".charAt(0))).isTrue();
        assertThat(filter.includeCharacter("y".charAt(0))).isTrue();
        assertThat(filter.includeCharacter("z".charAt(0))).isTrue();
        assertThat(filter.includeCharacter("*".charAt(0))).isFalse();
    }
}
