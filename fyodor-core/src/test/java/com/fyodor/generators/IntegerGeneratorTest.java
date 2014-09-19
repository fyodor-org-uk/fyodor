package com.fyodor.generators;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fyodor.range.Range.closed;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegerGeneratorTest {

    private final static Integer LOWER_BOUND = 20;
    private final static Integer UPPER_BOUND = 60;
    private Multiset<Integer> randomValues;

    @Before
    public void randomValues() {
        randomValues = HashMultiset.create();
    }

    @Test
    public void closedRangeCheck() {
        Generator<Integer> generator = RDG.integer(closed(LOWER_BOUND, UPPER_BOUND));
        generateRandomData(generator);
        Set<Integer> expectedIntegers = new HashSet<Integer>();
        for (int i = LOWER_BOUND; i <= UPPER_BOUND; i++) {
            expectedIntegers.add(i);
        }
        assertThat(randomValues.elementSet()).containsExactlyElementsOf(expectedIntegers);
    }

    private void generateRandomData(Generator<Integer> generator) {
        generateRandomData(generator, 1000);
    }

    private void generateRandomData(Generator<Integer> generator, int times) {
        for (int i = 0; i < times; i++) {
            randomValues.add(generator.next());
        }
        printOut();
    }

    private void printOut() {
        List<Integer> sortedValues = newArrayList(randomValues.elementSet());
        Collections.sort(sortedValues);
        for (Integer value : sortedValues) {
            System.out.println(value + ": " + randomValues.count(value));
        }
    }
}
