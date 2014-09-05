package com.fyodor.generators;

import com.fyodor.generators.characters.EmailLocalPartFilter;

public class EmailLocalPartGenerator implements Generator<String> {

    Generator<String> generator;

    public EmailLocalPartGenerator(int length) {
        generator = new StringGenerator(length, EmailLocalPartFilter.getFilter());
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
