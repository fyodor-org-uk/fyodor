package uk.org.fyodor.generators.time;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;

import java.time.LocalTime;

public final class LocalTimeGenerator implements Generator<LocalTime> {

    private final RandomValues randomValues;
    private final Range<LocalTime> range;

    public LocalTimeGenerator(final RandomValues randomValues, final Range<LocalTime> range) {
        this.randomValues = randomValues;
        this.range = range;
    }

    @Override
    public LocalTime next() {
        final LocalTime lower = range.lowerBound();
        final LocalTime upper = range.upperBound();

        final int hour = randomValues.randomInteger(lower.getHour(), upper.getHour());
        final int minute = randomValues.randomInteger(lower.getMinute(), upper.getMinute());
        final int second = randomValues.randomInteger(lower.getSecond(), upper.getSecond());
        final int nanoOfSecond = randomValues.randomInteger(lower.getNano(), upper.getNano());

        return LocalTime.of(hour, minute, second, nanoOfSecond);
    }
}
