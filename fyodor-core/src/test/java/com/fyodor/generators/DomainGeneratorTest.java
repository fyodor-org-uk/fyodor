package com.fyodor.generators;

import org.junit.Test;

import static com.fyodor.FyodorAssertions.assertThat;

public class DomainGeneratorTest {

    Generator<String> domainGenerator = RDG.domain();

    @Test
    public void noInvalidCharacters(){
        for (int i = 0;i < 10000; i++) {
            String domain = domainGenerator.next();
            System.out.println(domain);
            assertThat(domain.startsWith("-")).isFalse();
            assertThat(domain.endsWith("-")).isFalse();
        }
    }
}
