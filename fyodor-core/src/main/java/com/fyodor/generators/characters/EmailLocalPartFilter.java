package com.fyodor.generators.characters;

public class EmailLocalPartFilter implements CharacterFilter {

    private static EmailLocalPartFilter filter = new EmailLocalPartFilter();
    private CharacterFilter lettersAndDigitsFilter;

    private EmailLocalPartFilter() {
        lettersAndDigitsFilter = LettersAndDigitsFilter.getFilter();
    }

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(int i) {
        return lettersAndDigitsFilter.includeCharacter(i) || ".".charAt(0) == i;
    }
}
