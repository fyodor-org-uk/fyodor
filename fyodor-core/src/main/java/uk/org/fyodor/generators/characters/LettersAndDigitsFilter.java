package uk.org.fyodor.generators.characters;

public class LettersAndDigitsFilter implements CharacterFilter {

    private static final CharacterFilter filter = new LettersAndDigitsFilter();

    private LettersAndDigitsFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(char c) {
        return Character.isLetterOrDigit(c);
    }
}
