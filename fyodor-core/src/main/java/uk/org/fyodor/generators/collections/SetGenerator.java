package uk.org.fyodor.generators.collections;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public final class SetGenerator<T> implements Generator<Set<T>> {

    private final RandomValues randomValues;
    private final Generator<? extends T> generatorOfT;
    private final Range<Integer> sizeRange;

    public SetGenerator(final RandomValues randomValues, final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        this.randomValues = randomValues;
        this.generatorOfT = generatorOfT;
        this.sizeRange = sizeRange.limit(Range.closed(0, 1000));
    }

    @Override
    public Set<T> next() {
        final int size = randomValues.randomInteger(sizeRange.lowerBound(), sizeRange.upperBound());
        final Set<T> setOfT = new HashSet<>();
        int missCount = 0;
        while (setOfT.size() < size) {
            final int sizeBefore = setOfT.size();
            setOfT.add(generatorOfT.next());
            if (setOfT.size() == sizeBefore) {
                missCount++;
            }
            if (missCount >= 2000) {
                throw new IllegalStateException(format("unable to generate set of size %s because the value generator returned too many duplicate values", size));
            }
        }
        return setOfT;
    }
}
