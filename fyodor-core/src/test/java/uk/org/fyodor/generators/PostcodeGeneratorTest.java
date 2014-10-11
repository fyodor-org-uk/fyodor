package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

import java.util.regex.Pattern;

import static uk.org.fyodor.FyodorAssertions.assertThat;

/*
see http://en.wikipedia.org/wiki/Postcodes_in_the_United_Kingdom
and
https://www.gov.uk/government/uploads/system/uploads/attachment_data/file/359448/4__Bulk_Data_Transfer_-_additional_validation_valid.pdf
for details about what's valid for postcodes
 */
public class PostcodeGeneratorTest extends BaseTestWithRule {

    Pattern pattern = Pattern.compile("^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2})$");

    @Test
    public void generatePostcode() {
        Generator<String> postcodeGenerator = RDG.postcode();
        for (int i = 0; i < 10000; i++) {
            String postcode = postcodeGenerator.next();
            print(postcode);
            assertThat(pattern.matcher(postcode).matches()).isTrue();
        }
    }
}
