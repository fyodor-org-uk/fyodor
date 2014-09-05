package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.generators.characters.LettersAndDigitsFilter;

import java.util.Arrays;

import static java.lang.String.format;
/*
see http://en.wikipedia.org/wiki/Email_address for further info about what is and isn't
allowed in an email address.
 */
public class EmailAddressGenerator implements Generator<String> {

    Generator<String> localPartGenerator;

    public EmailAddressGenerator() {
        Character[] lettersAndDigits = new CharacterSetGenerator(LettersAndDigitsFilter.getFilter()).getCharset();
        Character[] nonLetterLocalPartChars = new Character[]{'!', '#', '$', '%', '&', '\'', '*', '+', '-', '/', '=', '?', '^', '_', '`', '{', '|', '}', '~', '.', ',', '}'};
        Character[] localPartCharset = new Character[lettersAndDigits.length + nonLetterLocalPartChars.length];
        System.arraycopy(lettersAndDigits, 0, localPartCharset, 0, lettersAndDigits.length);
        System.arraycopy(nonLetterLocalPartChars, 0, localPartCharset, lettersAndDigits.length, nonLetterLocalPartChars.length);
        localPartGenerator = RDG.string(20, localPartCharset);
    }



    @Override
    public String next() {
        return format("%s@%s.%s", localPartGenerator.next(), RDG.string(20, LettersAndDigitsFilter.getFilter()).next(), RDG.domainSuffix().next());
    }
}
