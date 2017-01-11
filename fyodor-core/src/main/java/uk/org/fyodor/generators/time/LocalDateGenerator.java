package uk.org.fyodor.generators.time;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;

import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.IsoChronology;

public final class LocalDateGenerator implements Generator<LocalDate> {

    private static final int MIN_MONTH_VALUE = Month.JANUARY.getValue();
    private static final int MAX_MONTH_VALUE = Month.DECEMBER.getValue();

    private final RandomValues randomValues;
    private final Range<LocalDate> range;

    public LocalDateGenerator(final RandomValues randomValues, final Range<LocalDate> range) {
        this.range = range;
        this.randomValues = randomValues;
    }

    @Override
    public LocalDate next() {
        final LocalDate lower = range.lowerBound();
        final LocalDate upper = range.upperBound();

        if (lower.equals(upper)) {
            return lower;
        }

        final int year = generateYear(lower, upper);

        final int month = generateMonth(lower, upper, year);

        final int day = generateDay(lower, upper, year, month);

        return LocalDate.of(year, month, day);
    }

    private int generateYear(final LocalDate lower, final LocalDate upper) {
        return randomValues.randomInteger(lower.getYear(), upper.getYear());
    }

    private int generateMonth(final LocalDate lower, final LocalDate upper, final int year) {
        final int maxMonth = year < upper.getYear() ? MAX_MONTH_VALUE : upper.getMonthValue();
        final int minMonth = year > lower.getYear() ? MIN_MONTH_VALUE : lower.getMonthValue();

        return randomValues.randomInteger(minMonth, maxMonth);
    }

    private int generateDay(final LocalDate lower, final LocalDate upper, final int year, final int month) {
        final int minDay = (lower.getYear() == year && lower.getMonthValue() == month)
                ? lower.getDayOfMonth()
                : 1;

        final int maxDay = (year == upper.getYear() && month == upper.getMonthValue())
                ? upper.getDayOfMonth()
                : calculateMaxDayOfMonth(year, month);

        return randomValues.randomInteger(minDay, maxDay);
    }

    private static int calculateMaxDayOfMonth(final int year, final int month) {
        if (month == Month.FEBRUARY.getValue()) {
            return IsoChronology.INSTANCE.isLeapYear(year) ? 29 : 28;
        }

        return LocalDate.of(year, month, 1).getMonth().maxLength();
    }
}
