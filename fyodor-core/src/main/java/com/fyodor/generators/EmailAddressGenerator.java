package com.fyodor.generators;

import com.fyodor.range.Range;

import static java.lang.String.format;
/*
see http://en.wikipedia.org/wiki/Email_address for further info about what is and isn't
allowed in an email address.
 */
public class EmailAddressGenerator implements Generator<String> {

    Generator<String> localPartGenerator;
    Generator<String> domainPartGenerator;

    public EmailAddressGenerator() {
        localPartGenerator = new EmailLocalPartGenerator(Range.closed(3, 50));
        domainPartGenerator = RDG.domain();
    }

    @Override
    public String next() {
        return format("%s@%s.%s", localPartGenerator.next(), domainPartGenerator.next(), RDG.domainSuffix().next());
    }
}
