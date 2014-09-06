package com.fyodor.generators;

import com.fyodor.generators.characters.DomainNameFilter;

public class DomainGenerator implements Generator<String> {

    Generator<String> domainGenerator = new StringGenerator(20, DomainNameFilter.getFilter());

    @Override
    public String next() {
        String domain = domainGenerator.next();
        if (domain.startsWith("-") || domain.endsWith("-")) {
            return next();
        }
        return domain;
    }
}
