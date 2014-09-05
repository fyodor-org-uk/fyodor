package com.fyodor.generators;

import org.junit.Test;

public class EmailAddressGeneratorTest {

    Generator<String> generator = RDG.emailAddress();

    @Test
    public void checkoutEmailAddresses(){
        for (int i = 0; i < 100; i++) {
            System.out.println(generator.next());
        }
    }
}
