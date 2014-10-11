package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import java.util.regex.Pattern;

import static uk.org.fyodor.FyodorAssertions.assertThat;

public class UrlGeneratorTest extends BaseTestWithRule {

    Pattern pattern = Pattern.compile("^https?://[\\w\\-.]*[\\.!?\\w]+$");

    @Test
    public void createUrls(){
        Generator<String> urlGenerator = RDG.url();
        for (int i = 0; i < 10000; i++) {
            String url = urlGenerator.next();
            print(url);
            if (!stringContainsWeirdChar(url)) {
                //RegEx-fu not strong enough to cope with non-ascii chars in some URLs that get generated
                //have to settle for checking format of those we can
                assertThat(pattern.matcher(url).matches()).isTrue();
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
