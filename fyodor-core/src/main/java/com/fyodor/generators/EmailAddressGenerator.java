package com.fyodor.generators;

import com.fyodor.generators.characters.EmailLocalPartFilter;
import com.fyodor.generators.characters.LettersAndDigitsFilter;

import static java.lang.String.format;
/*
see http://en.wikipedia.org/wiki/Email_address for further info about what is and isn't
allowed in an email address.
 */
public class EmailAddressGenerator implements Generator<String> {

    Generator<String> localPartGenerator;

    public EmailAddressGenerator() {
        localPartGenerator = RDG.string(20, EmailLocalPartFilter.getFilter());
    }

    @Override
    public String next() {
        return format("%s@%s.%s", localPartGenerator.next(), RDG.string(20, LettersAndDigitsFilter.getFilter()).next(), RDG.domainSuffix().next());
    }
}
