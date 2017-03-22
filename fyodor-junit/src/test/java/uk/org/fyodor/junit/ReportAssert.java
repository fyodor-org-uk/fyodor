package uk.org.fyodor.junit;

import org.assertj.core.api.AbstractAssert;

final class ReportAssert<T> extends AbstractAssert<ReportAssert<T>, Report<T>> {

    private ReportAssert(final Report<T> actual) {
        super(actual, ReportAssert.class);
    }

    ReportAssert<T> beforeTestStarts(final T expectedObject) {
        isNotNull();
        org.assertj.core.api.Assertions.assertThat(actual.getObjectWhenTestStarts())
                .describedAs("object before the test starts")
                .isEqualTo(expectedObject);
        return this;
    }

    ReportAssert<T> whenTestHasFinished(final T expectedObject) {
        isNotNull();
        org.assertj.core.api.Assertions.assertThat(actual.getObjectWhenTestFinishes())
                .describedAs("object after the test finishes")
                .isEqualTo(expectedObject);
        return this;
    }

    ReportAssert<T> whenFailed(final T expectedObject) {
        isNotNull();

        org.assertj.core.api.Assertions.assertThat(actual.getObjectReportedInTestFailure())
                .describedAs("object reported when the test failed")
                .isEqualTo(expectedObject);
        return this;
    }

    ReportAssert<T> duringTest(final T expectedObject) {
        isNotNull();

        org.assertj.core.api.Assertions.assertThat(actual.getObjectDuringTest())
                .describedAs("object during test execution")
                .isEqualTo(expectedObject);

        return this;
    }

    ReportAssert<T> failedBecauseOf(final Class<? extends Throwable> classOfCause) {
        isNotNull();

        org.assertj.core.api.Assertions.assertThat(actual.getCauseOfFailure())
                .describedAs("cause of failure")
                .isInstanceOf(classOfCause);

        return this;
    }

    ReportAssert<T> didNotFail() {
        isNotNull();

        org.assertj.core.api.Assertions.assertThat(actual.getCauseOfFailure())
                .describedAs("test should not have failed, and should not have a cause of failure")
                .isNull();

        return this;
    }

    static <T> ReportAssert<T> assertThat(final Report<T> actual) {
        return new ReportAssert<>(actual);
    }
}
