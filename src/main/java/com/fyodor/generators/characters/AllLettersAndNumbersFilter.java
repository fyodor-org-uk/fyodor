package com.fyodor.generators.characters;

public class AllLettersAndNumbersFilter implements CharacterFilter {

    private static CharacterFilter filter = new AllLettersAndNumbersFilter();

    private AllLettersAndNumbersFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(int i) {
        return true;
    }
}
