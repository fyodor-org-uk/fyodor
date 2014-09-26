package uk.org.fyodor.generators.collections;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;

import java.util.HashMap;
import java.util.Map;

import static uk.org.fyodor.range.Range.closed;
import static java.lang.String.format;

public final class MapGenerator<K, V> implements Generator<Map<K, V>> {

    private final RandomValues randomValues;
    private final Generator<? extends K> generatorOfK;
    private final Generator<? extends V> generatorOfV;
    private final Range<Integer> sizeRange;

    public MapGenerator(final RandomValues randomValues,
                        final Generator<? extends K> generatorOfK,
                        final Generator<? extends V> generatorOfV,
                        final Range<Integer> sizeRange) {
        this.randomValues = randomValues;
        this.generatorOfK = generatorOfK;
        this.generatorOfV = generatorOfV;
        this.sizeRange = sizeRange.limit(closed(0, 1000));
    }

    @Override
    public Map<K, V> next() {
        final int size = randomValues.randomInteger(sizeRange.lowerBound(), sizeRange.upperBound());
        final HashMap<K, V> map = new HashMap<K, V>();

        int misses = 0;
        while (map.size() < size) {
            final int sizeBeforePut = map.size();
            final K key = generatorOfK.next();
            if (key == null) {
                throw new NullPointerException("key generator generated a null value");
            }
            map.put(key, generatorOfV.next());
            if (map.size() == sizeBeforePut) {
                if (++misses > 2000) {
                    throw new IllegalStateException(format("unable to generate map of size %s because the key generator returned too many duplicate values", size));
                }
            }
        }
        return map;
    }
}
