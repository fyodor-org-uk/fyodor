package com.fyodor.jodatime.generators;

import com.fyodor.generators.Generator;
import com.fyodor.range.Range;
import org.joda.time.LocalDate;

import static com.fyodor.random.RandomValuesProvider.randomValues;

public final class RDG {

    public static Generator<LocalDate> localDate(final Range<LocalDate> range) {
        if (range == null) {
            throw new IllegalArgumentException("range cannot be null");
        }

        return new LocalDateGenerator(randomValues(), range);
    }

    private RDG() {
    }
}
