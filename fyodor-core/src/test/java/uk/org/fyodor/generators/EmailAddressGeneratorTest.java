package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTest;

public class EmailAddressGeneratorTest extends BaseTest {

    Generator<String> generator = RDG.emailAddress();

    @Test
    public void checkoutEmailAddresses(){
        for (int i = 0; i < 100; i++) {
            String emailAddress = generator.next();
            //TODO: invent a regex to validate email addresses
            //http://www.regular-expressions.info/email.html
            print(emailAddress);
        }
    }
}
