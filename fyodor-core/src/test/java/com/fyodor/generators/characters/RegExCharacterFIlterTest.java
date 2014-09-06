package com.fyodor.generators.characters;

import org.junit.Test;

import static com.fyodor.FyodorAssertions.assertThat;


public class RegExCharacterFIlterTest {

    @Test
    public void useRegEx(){
        RegExCharacterFilter filter = new RegExCharacterFilter("[a-z]");
        assertThat(filter).includes("a");
        assertThat(filter).includes("b");
        assertThat(filter).includes("c");
        assertThat(filter).includes("x");
        assertThat(filter).includes("y");
        assertThat(filter).includes("z");
        assertThat(filter).doesNotInclude("*");
    }
}
