package uk.org.fyodor.jodatime.generators;

import org.assertj.core.api.AbstractAssert;
import org.joda.time.LocalDate;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

final class LocalDateAssert extends AbstractAssert<LocalDateAssert, LocalDate> {

    LocalDateAssert(final LocalDate actual) {
        super(actual, LocalDateAssert.class);
    }

    LocalDateAssert isGreaterThanOrEqualTo(final LocalDate atLeast) {
        isNotNull();
        assertThat(atLeast).describedAs("Cannot be at-least null").isNotNull();

        assertThat(actual.compareTo(atLeast))
                .describedAs(format("Expecting %s to be greater than or equal to %s", actual, atLeast))
                .isGreaterThanOrEqualTo(0);

        return this;
    }

    public LocalDateAssert isLessThanOrEqualTo(final LocalDate atMost) {
        isNotNull();
        assertThat(atMost).describedAs("Cannot be at-most null").isNotNull();

        assertThat(actual.compareTo(atMost))
                .describedAs(format("Expecting %s to be less than or equal to %s", actual, atMost))
                .isLessThanOrEqualTo(0);

        return this;
    }

    public LocalDateAssert isGreaterThan(final LocalDate greaterThan) {
        isNotNull();
        assertThat(greaterThan).describedAs("Cannot be greater than null").isNotNull();

        assertThat(actual.compareTo(greaterThan))
                .describedAs(format("Expecting %s to be greater than %s", actual, greaterThan))
                .isGreaterThan(0);

        return this;
    }

    public LocalDateAssert isLessThan(final LocalDate lessThan) {
        isNotNull();
        assertThat(lessThan).describedAs("Cannot be less than null").isNotNull();

        assertThat(actual.compareTo(lessThan))
                .describedAs(format("Expecting %s to be less than %s", actual, lessThan))
                .isLessThan(0);
        return this;
    }
}
