package uk.org.fyodor.generators;

import org.junit.Test;

public class EmailAddressGeneratorTest {

    Generator<String> generator = RDG.emailAddress();

    @Test
    public void checkoutEmailAddresses(){
        for (int i = 0; i < 100; i++) {
            //TODO: invent a regex to validate email addresses
            //http://www.regular-expressions.info/email.html
            System.out.println(generator.next());
        }
    }
}
