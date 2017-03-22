package uk.org.fyodor.generators.time;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;

import java.time.Instant;

public final class InstantGenerator implements Generator<Instant> {

    private final Range<Instant> range;
    private final RandomValues randomValues;

    public InstantGenerator(final RandomValues randomValues, final Range<Instant> range) {
        this.randomValues = randomValues;
        this.range = range;
    }

    @Override
    public Instant next() {
        final long lower = range.lowerBound().getEpochSecond();
        final long upper = range.upperBound().getEpochSecond();

        if (lower == upper) {
            return range.lowerBound();
        }

        return Instant.ofEpochSecond(randomValues.randomLong(lower, upper));
    }
}
