package com.fyodor.generators.characters;

import org.junit.Test;

import static com.fyodor.FyodorAsserts.assertThat;


public class EmailLocalPartFilterTest {

    CharacterFilter filter = EmailLocalPartFilter.getFilter();

    @Test
    public void localPartCharacters(){
        assertThat(filter).includes("a");
        assertThat(filter).includes("d");
        assertThat(filter).includes("r");
        assertThat(filter).includes("z");
        assertThat(filter).includes("A");
        assertThat(filter).includes("D");
        assertThat(filter).includes("R");
        assertThat(filter).includes("Z");
        assertThat(filter).includes("0");
        assertThat(filter).includes("3");
        assertThat(filter).includes("6");
        assertThat(filter).includes("9");
        assertThat(filter).doesNotInclude("!");
        assertThat(filter).doesNotInclude("#");
        assertThat(filter).doesNotInclude("$");
        assertThat(filter).doesNotInclude("%");
        assertThat(filter).doesNotInclude("&");
        assertThat(filter).doesNotInclude("'");
        assertThat(filter).doesNotInclude("*");
        assertThat(filter).doesNotInclude("+");
        assertThat(filter).doesNotInclude("-");
        assertThat(filter).doesNotInclude("/");
        assertThat(filter).doesNotInclude("=");
        assertThat(filter).doesNotInclude("?");
        assertThat(filter).doesNotInclude("^");
        assertThat(filter).doesNotInclude("_");
        assertThat(filter).doesNotInclude("`");
        assertThat(filter).doesNotInclude("{");
        assertThat(filter).doesNotInclude("|");
        assertThat(filter).doesNotInclude("}");
        assertThat(filter).doesNotInclude("~");
        assertThat(filter).includes(".");
    }
}
