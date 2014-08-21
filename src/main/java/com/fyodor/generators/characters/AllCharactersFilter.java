package com.fyodor.generators.characters;

public class AllCharactersFilter implements CharacterFilter {

    private static CharacterFilter filter = new AllCharactersFilter();

    private AllCharactersFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(int i) {
        return true;
    }
}
