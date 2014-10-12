package uk.org.fyodor.generators;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;
import uk.org.fyodor.BaseTestWithRule;

public class PercentageChanceGeneratorTest extends BaseTestWithRule {

    Multiset<Boolean> results = HashMultiset.create();

    @Test
    public void percentageChances(){
        for (int p = 1; p < 100; p++) {
            Generator<Boolean> percentageChance = RDG.percentageChanceOf(p);
            for (int i = 0; i < 10000; i++) {
                results.add(percentageChance.next());
            }
            print(p);
            print("True: " + results.count(Boolean.TRUE));
            print("False: " + results.count(Boolean.FALSE));
            results.clear();
        }
    }
}
