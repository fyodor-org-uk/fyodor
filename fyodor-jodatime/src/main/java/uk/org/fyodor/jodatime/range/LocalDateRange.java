package uk.org.fyodor.jodatime.range;

import org.joda.time.LocalDate;
import org.joda.time.base.BaseSingleFieldPeriod;
import uk.org.fyodor.range.Range;

import static org.joda.time.LocalDate.now;

public final class LocalDateRange extends Range<LocalDate> {

    private static final LocalDate LOWER_BOUND = new LocalDate(0, 1, 1);
    private static final LocalDate UPPER_BOUND = new LocalDate(2999, 12, 31);

    private LocalDateRange(final LocalDate lowerBound, final LocalDate upperBound) {
        super(lowerBound, upperBound);
    }

    public LocalDateRange limit() {
        final Range<LocalDate> limitedRange = limit(Range.closed(LOWER_BOUND, UPPER_BOUND));
        return new LocalDateRange(limitedRange.lowerBound(), limitedRange.upperBound());
    }

    public static LocalDateRange all() {
        return new LocalDateRange(LOWER_BOUND, UPPER_BOUND);
    }

    public static LocalDateRange closed(final LocalDate lowerBound, final LocalDate upperBound) {
        return new LocalDateRange(lowerBound, upperBound);
    }

    public static LocalDateRange fixed(final LocalDate fixed) {
        return new LocalDateRange(fixed, fixed);
    }

    public static LocalDateRange inThePast() {
        return new LocalDateRange(LOWER_BOUND, now().minusDays(1));
    }

    public static LocalDateRange inTheFuture() {
        return new LocalDateRange(now().plusDays(1), UPPER_BOUND);
    }

    public static LocalDateRange greaterThan(final LocalDate greaterThan) {
        return new LocalDateRange(greaterThan.plusDays(1), UPPER_BOUND);
    }

    public static LocalDateRange lessThan(final LocalDate lessThan) {
        return new LocalDateRange(LOWER_BOUND, lessThan.minusDays(1));
    }

    public static LocalDateRange aged(final Range<? extends BaseSingleFieldPeriod> ageRange) {
        return new LocalDateRange(now().minus(ageRange.upperBound()), now().minus(ageRange.lowerBound()));
    }
}
