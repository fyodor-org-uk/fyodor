package uk.org.fyodor.generators;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.assertj.core.api.Condition;
import org.junit.Test;
import uk.org.fyodor.BaseTest;
import uk.org.fyodor.FyodorAssertions;
import uk.org.fyodor.range.Range;

import static uk.org.fyodor.random.RandomSourceProvider.sourceOfRandomness;

public class VariableLengthStringGeneratorTest extends BaseTest {

    private Multiset<Integer> generated;
    private Generator<String> generator;

    @Test
    public void variableLength() {
        generated = HashMultiset.create();
        final Integer minSize = sourceOfRandomness().randomInteger(5, 15);
        final Integer maxSize = sourceOfRandomness().randomInteger(20, 100);
        generator = new StringGenerator(Range.closed(minSize, maxSize));
        for (int i = 0; i < 10000; i++) {
            String next = generator.next();
            generated.add(next.length());
            print(next);
        }
        FyodorAssertions.assertThat(generated.elementSet())
                .hasSize(maxSize - minSize + 1)
                .has(new Condition<Iterable<? extends Integer>>() {
                    @Override
                    public boolean matches(Iterable<? extends Integer> value) {
                        for (Integer val : value) {
                            if (val < minSize || val > maxSize) {
                                return false;
                            }
                        }
                        return true;
                    }
                });
    }
}
