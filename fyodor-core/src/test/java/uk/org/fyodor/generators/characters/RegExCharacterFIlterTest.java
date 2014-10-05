package uk.org.fyodor.generators.characters;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import static uk.org.fyodor.FyodorAssertions.assertThat;


public class RegExCharacterFIlterTest extends BaseTestWithRule {

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
