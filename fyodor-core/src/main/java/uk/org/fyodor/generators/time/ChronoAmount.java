package uk.org.fyodor.generators.time;

import java.time.temporal.ChronoUnit;

public abstract class ChronoAmount implements Comparable<ChronoAmount> {

    private ChronoAmount() {
    }

    abstract long amount();

    abstract ChronoUnit unit();

    @Override
    public final int compareTo(final ChronoAmount that) {
        if (!this.getClass().equals(that.getClass())) {
            throw new IllegalArgumentException(this.unit() + " cannot be compared to " + that.unit());
        }

        long thatQuantity = that.amount();
        long thisQuantity = this.amount();

        if (thisQuantity == thatQuantity) {
            return 0;
        }

        return thisQuantity < thatQuantity ? -1 : 1;
    }

    @Override
    public final String toString() {
        return String.format("%s %s", amount(), unit());
    }

    public static ChronoAmount days(final long numberOfDays) {
        return new ChronoAmount() {
            @Override
            public long amount() {
                return numberOfDays;
            }

            @Override
            public ChronoUnit unit() {
                return ChronoUnit.DAYS;
            }
        };
    }

    public static ChronoAmount years(final long numberOfYears) {
        return new ChronoAmount() {
            @Override
            public long amount() {
                return numberOfYears;
            }

            @Override
            public ChronoUnit unit() {
                return ChronoUnit.YEARS;
            }
        };
    }

    public static ChronoAmount months(final long numberOfMonths) {
        return new ChronoAmount() {
            @Override
            public long amount() {
                return numberOfMonths;
            }

            @Override
            public ChronoUnit unit() {
                return ChronoUnit.MONTHS;
            }
        };
    }
}
