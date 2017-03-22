package uk.org.fyodor.junit;

public final class Report<T> {

    private T objectWhenTestStarts;
    private T objectDuringTest;
    private T objectWhenTestFinishes;
    private T objectReportedInTestFailure;
    private Throwable causeOfFailure;

    T getObjectWhenTestStarts() {
        return objectWhenTestStarts;
    }

    Report<T> setObjectWhenTestStarts(final T objectWhenTestStarts) {
        this.objectWhenTestStarts = objectWhenTestStarts;
        return this;
    }

    T getObjectWhenTestFinishes() {
        return objectWhenTestFinishes;
    }

    void setObjectWhenTestFinishes(final T objectWhenTestFinishes) {
        this.objectWhenTestFinishes = objectWhenTestFinishes;
    }

    T getObjectReportedInTestFailure() {
        return objectReportedInTestFailure;
    }

    Report<T> setObjectReportedInTestFailure(final T objectReportedInTestFailure) {
        this.objectReportedInTestFailure = objectReportedInTestFailure;
        return this;
    }

    T getObjectDuringTest() {
        return objectDuringTest;
    }

    void setObjectDuringTest(final T objectDuringTest) {
        this.objectDuringTest = objectDuringTest;
    }

    void setFailureCause(final Throwable cause) {
        this.causeOfFailure = cause;
    }

    Throwable getCauseOfFailure() {
        return causeOfFailure;
    }
}
