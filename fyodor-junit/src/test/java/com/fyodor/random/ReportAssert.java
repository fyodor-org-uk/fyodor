package com.fyodor.random;

import org.assertj.core.api.AbstractAssert;

final class ReportAssert extends AbstractAssert<ReportAssert, Report> {

    private ReportAssert(final Report actual) {
        super(actual, ReportAssert.class);
    }

    ReportAssert seedBeforeTestStarts(final long expectedSeed) {
        isNotNull();
        org.assertj.core.api.Assertions.assertThat(actual.getSeedWhenTestStarts())
                .describedAs("seed before the test starts")
                .isEqualTo(expectedSeed);
        return this;
    }

    ReportAssert seedWhenTestHasFinished(final long expectedSeed) {
        isNotNull();
        org.assertj.core.api.Assertions.assertThat(actual.getSeedWhenTestFinishes())
                .describedAs("seed after the test finishes")
                .isEqualTo(expectedSeed);
        return this;
    }

    ReportAssert seedReportedInTestFailure(final long expectedSeed) {
        isNotNull();
        org.assertj.core.api.Assertions.assertThat(actual.getSeedReportedInTestFailure())
                .describedAs("seed reported when the test failed")
                .isEqualTo(expectedSeed);
        return this;
    }

    ReportAssert seedDuringTest(final int expectedSeed) {
        isNotNull();
        org.assertj.core.api.Assertions.assertThat(actual.getSeedDuringTest())
                .describedAs("seed during test execution")
                .isEqualTo(expectedSeed);
        return this;
    }

    static ReportAssert assertThat(final Report actual) {
        return new ReportAssert(actual);
    }
}
