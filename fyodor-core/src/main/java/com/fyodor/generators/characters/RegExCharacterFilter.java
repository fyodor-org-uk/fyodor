package com.fyodor.generators.characters;

import java.util.regex.Pattern;

public class RegExCharacterFilter implements CharacterFilter {

    private final Pattern pattern;

    public RegExCharacterFilter(String regex) {
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean includeCharacter(int i) {
        return pattern.matcher(String.valueOf((char)i)).find();
    }
}
