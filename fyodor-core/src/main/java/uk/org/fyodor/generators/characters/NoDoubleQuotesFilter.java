package uk.org.fyodor.generators.characters;

public class NoDoubleQuotesFilter implements CharacterFilter {

    private static final CharacterFilter filter = new NoDoubleQuotesFilter();

    private NoDoubleQuotesFilter() {}

    public static CharacterFilter getFilter() {
        return filter;
    }

    @Override
    public boolean includeCharacter(char c) {
        return !"\"".equals(String.valueOf(c));
    }
}
