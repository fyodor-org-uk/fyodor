package uk.org.fyodor.generators;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.assertj.core.api.Condition;
import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;
import uk.org.fyodor.FyodorAssertions;
import uk.org.fyodor.range.Range;

import static uk.org.fyodor.random.RandomValuesProvider.randomValues;

public class VariableLengthStringGeneratorTest extends BaseTestWithRule {

    Multiset<Integer> generated;
    Generator<String> generator;

    @Test
    public void parp(){
        generator = new StringGenerator(Range.closed(10,20));

        for (int i = 0; i < 10; i++){
            System.out.println(generator.next());
        }
    }

    @Test
    public void variableLength() {
        generated = HashMultiset.create();
        final Integer minSize = randomValues().randomInteger(5, 15);
        final Integer maxSize = randomValues().randomInteger(20, 100);
        generator = new StringGenerator(Range.closed(minSize, maxSize));
        for (int i = 0; i < 10000; i++) {
            String next = generator.next();
            generated.add(next.length());
        }
        FyodorAssertions.assertThat(generated.elementSet())
                .hasSize(maxSize - minSize + 1)
                .has(new Condition<Iterable<Integer>>() {
                    @Override
                    public boolean matches(Iterable<Integer> value) {
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
