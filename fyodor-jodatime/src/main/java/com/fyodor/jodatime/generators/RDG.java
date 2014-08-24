package com.fyodor.jodatime.generators;

import com.fyodor.generators.Generator;
import com.fyodor.range.Range;
import org.joda.time.LocalDate;

import static com.fyodor.generators.RandomValuesProvider.randomValues;

public final class RDG {

    public static Generator<LocalDate> localDate(final Range<LocalDate> range) {
        return new LocalDateGenerator(randomValues(), range);
    }

    private RDG() {
    }
}
