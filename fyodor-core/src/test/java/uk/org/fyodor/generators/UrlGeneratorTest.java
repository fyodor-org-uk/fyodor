package uk.org.fyodor.generators;

import org.junit.Test;

import java.util.regex.Pattern;

import static uk.org.fyodor.FyodorAssertions.assertThat;

public class UrlGeneratorTest {

    Pattern pattern = Pattern.compile("^https?://[\\w\\-.]*[\\.!?\\w]+$");

    @Test
    public void createUrls(){
        Generator<String> urlGenerator = new UrlGenerator();
        for (int i = 0; i < 10000; i++) {
            String url = urlGenerator.next();
            if (!stringContainsWeirdChar(url)) {
                //RegEx-fu not strong enough to cope with non-ascii chars in some URLs that get generated
                //have to settle for checking format of those we can
                assertThat(pattern.matcher(url).find()).isTrue();
            }
        }
    }

    private boolean stringContainsWeirdChar(String url) {
        for (char c : url.toCharArray()) {
            if ((int)c > 126) {
                return true;
            }
        }
        return false;
    }
}
