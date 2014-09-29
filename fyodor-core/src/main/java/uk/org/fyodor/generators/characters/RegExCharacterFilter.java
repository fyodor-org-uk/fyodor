package uk.org.fyodor.generators.characters;

import java.util.regex.Pattern;

public class RegExCharacterFilter implements CharacterFilter {

    private final Pattern pattern;

    public RegExCharacterFilter(String regex) {
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean includeCharacter(char c) {
        return pattern.matcher(String.valueOf(c)).find();
    }
}
