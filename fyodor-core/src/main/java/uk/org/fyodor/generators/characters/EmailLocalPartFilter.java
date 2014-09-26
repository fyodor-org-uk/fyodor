package uk.org.fyodor.generators.characters;

public class EmailLocalPartFilter extends RegExCharacterFilter {

    private static EmailLocalPartFilter filter = new EmailLocalPartFilter();

    private EmailLocalPartFilter(){
        super("[a-zA-Z0-9\\.\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]");
    }

    public static CharacterFilter getFilter() {
        return filter;
    }

}
