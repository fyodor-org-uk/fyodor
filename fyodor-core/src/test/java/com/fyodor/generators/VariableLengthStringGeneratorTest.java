package com.fyodor.generators;

import com.fyodor.range.Range;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.assertj.core.api.Condition;
import org.junit.Test;

import static com.fyodor.FyodorAssertions.assertThat;
import static com.fyodor.random.RandomValuesProvider.randomValues;

public class VariableLengthStringGeneratorTest {

    Multiset<Integer> generated;
    Generator<String> generator;

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
        assertThat(generated.elementSet())
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
