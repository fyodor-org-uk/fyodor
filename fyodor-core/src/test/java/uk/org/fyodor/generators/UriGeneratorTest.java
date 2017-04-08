package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTest;

import java.net.URI;
import java.util.regex.Pattern;

import static uk.org.fyodor.FyodorAssertions.assertThat;

public class UriGeneratorTest extends BaseTest {

    Pattern pattern = Pattern.compile("^https?://[\\w\\-.]*[\\.!?\\w]+$");

    @Test
    public void createUrls(){
        Generator<URI> urlGenerator = RDG.uri();
        for (int i = 0; i < 10000; i++) {
            URI url = urlGenerator.next();
            print(url.toString());
            if (!stringContainsWeirdChar(url)) {
                //RegEx-fu not strong enough to cope with non-ascii chars in some URLs that get generated
                //have to settle for checking format of those we can
                assertThat(pattern.matcher(url.toString()).matches()).isTrue();
            }
        }
    }

    private boolean stringContainsWeirdChar(URI url) {
        for (char c : url.toString().toCharArray()) {
            if ((int)c > 126) {
                return true;
            }
        }
        return false;
    }
}
