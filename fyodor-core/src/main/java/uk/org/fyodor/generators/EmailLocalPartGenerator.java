package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.CharacterSetFilter;
import uk.org.fyodor.range.Range;

public class EmailLocalPartGenerator implements Generator<String> {

    Generator<String> generator;

    public EmailLocalPartGenerator(int length) {
        generator = RDG.string(length, CharacterSetFilter.EmailLocalPart);
    }

    public EmailLocalPartGenerator(Range<Integer> range) {
        generator = RDG.string(range, CharacterSetFilter.EmailLocalPart);
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
