package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.LettersOnlyFilter;

/*
rules for format taken from http://en.wikipedia.org/wiki/National_Insurance_number#Format
 */
public class NINumberGenerator implements Generator<String> {

    Generator<String> beginning;
    String[] end = new String[]{"A", "B", "C", "D"};

    public NINumberGenerator() {
        beginning = RDG.string(2, LettersOnlyFilter.getFilter());
    }

    @Override
    public String next() {
        String start = beginning.next();
        if (start.matches("^(?!BG)(?!GB)(?!NK)(?!KN)(?!TN)(?!NT)(?!ZZ)(?:[A-CEGHJ-PR-TW-Z][A-CEGHJ-NPR-TW-Z])")) {
            return String.format("%s%06d%s", start.toUpperCase(), RDG.integer(999999).next(), RDG.value(end).next());
        }
        return next();
    }
}
