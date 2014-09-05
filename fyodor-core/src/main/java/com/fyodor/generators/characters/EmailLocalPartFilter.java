package com.fyodor.generators.characters;

import java.lang.reflect.Array;
import java.util.Arrays;

public class EmailLocalPartFilter implements CharacterFilter {

    private static EmailLocalPartFilter filter = new EmailLocalPartFilter();
    private final CharacterFilter lettersAndDigitsFilter;
    Character[] nonLetters = new Character[]{'!', '#', '$', '%', '&', '\'', '*', '+', '-', '/', '=', '?', '^', '_', '`', '{', '|', '}', '~', '.', ',', '}'}
    private EmailLocalPartFilter(){
        lettersAndDigitsFilter = LettersAndDigitsFilter.getFilter();
    }

    public CharacterFilter getFilter(){
        return filter;
    }

    @Override
    public boolean includeCharacter(int i) {
        return lettersAndDigitsFilter.includeCharacter(i) || Arrays.binarySearch(nonLetters, i)
                ;
    }
}
