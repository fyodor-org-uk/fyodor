package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.DomainNameFilter;
import uk.org.fyodor.range.Range;

public class DomainGenerator implements Generator<String> {

    Generator<String> domainGenerator;

    public DomainGenerator(){
        this(Range.closed(5, 40));
    }

    public DomainGenerator(Range<Integer> range) {
        domainGenerator = new StringGenerator(range, DomainNameFilter.getFilter());
    }

    @Override
    public String next() {
        String domain = domainGenerator.next();
        if (domain.startsWith("-") || domain.endsWith("-")) {
            return next();
        }
        return domain;
    }
}
