package com.fyodor.generators.characters;

public class LettersAndDigitsFilter implements CharacterFilter {

    private static CharacterFilter filter = new LettersAndDigitsFilter();

    private LettersAndDigitsFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(int i) {
        return Character.isLetterOrDigit(i);
    }
}
