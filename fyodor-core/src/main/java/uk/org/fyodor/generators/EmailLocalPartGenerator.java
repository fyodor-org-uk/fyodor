package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.EmailLocalPartFilter;
import uk.org.fyodor.range.Range;

public class EmailLocalPartGenerator implements Generator<String> {

    Generator<String> generator;

    public EmailLocalPartGenerator(int length) {
        generator = new StringGenerator(length, EmailLocalPartFilter.getFilter());
    }

    public EmailLocalPartGenerator(Range<Integer> range) {
        generator = new StringGenerator(range, EmailLocalPartFilter.getFilter());
    }

    @Override
    public String next() {
        String localPart = generator.next();
        if (localPart.contains("..") || localPart.startsWith(".") || localPart.endsWith(".")) {
            return next();
        }
        return localPart;
    }
}
