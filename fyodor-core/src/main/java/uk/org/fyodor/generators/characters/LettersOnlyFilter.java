package uk.org.fyodor.generators.characters;

public class LettersOnlyFilter implements CharacterFilter {

    private static CharacterFilter filter = new LettersOnlyFilter();

    private LettersOnlyFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(char c) {
        return Character.isLetter(c);
    }
}
