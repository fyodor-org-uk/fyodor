package com.fyodor.generators;

import static java.lang.String.format;

public class EmailAddressGenerator implements Generator<String> {
    @Override
    public String next() {
        return format("%s@.%s", RDG.string(20).next(), RDG.domainSuffix().next());   }
}
