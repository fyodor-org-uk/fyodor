package com.fyodor.jodatime.generators;

import com.fyodor.range.Range;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.base.BaseSingleFieldPeriod;

import static com.fyodor.range.Range.closed;

public final class JodaTimeRanges {

    private static final LocalDate LOWER_BOUND = new LocalDate(0, 1, 1);
    private static final LocalDate UPPER_BOUND = new LocalDate(2999, 12, 31);

    public static Range<LocalDate> allDates() {
        return closed(LOWER_BOUND, UPPER_BOUND);
    }

    public static Range<LocalDate> futureDate() {
        return closed(LocalDate.now().plusDays(1), UPPER_BOUND);
    }

    public static Range<LocalDate> pastDate() {
        return closed(LOWER_BOUND, LocalDate.now().minusDays(1));
    }

    public static Range<LocalDate> aged(final Range<? extends BaseSingleFieldPeriod> range) {
        return closed(LocalDate.now().minus(range.upperBound()), LocalDate.now().minus(range.lowerBound()));
    }

    public static Range<Years> years(final int lowerBound, final int upperBound) {
        return closed(Years.years(lowerBound), Years.years(upperBound));
    }

    public static Range<Months> months(final int lowerBound, final int upperBound) {
        return closed(Months.months(lowerBound), Months.months(upperBound));
    }

    public static Range<Days> days(final int lowerBound, final int upperBound) {
        return closed(Days.days(lowerBound), Days.days(upperBound));
    }

    private JodaTimeRanges() {
    }
}
