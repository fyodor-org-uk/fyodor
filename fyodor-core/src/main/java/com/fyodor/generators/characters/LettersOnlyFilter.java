package com.fyodor.generators.characters;

public class LettersOnlyFilter implements CharacterFilter {

    private static CharacterFilter filter = new LettersOnlyFilter();

    private LettersOnlyFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(int i) {
        return Character.isLetter(i);
    }
}
