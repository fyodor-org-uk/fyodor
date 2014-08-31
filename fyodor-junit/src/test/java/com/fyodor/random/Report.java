package com.fyodor.random;

final class Report {

    private long seedWhenTestStarts = 0;
    private long seedDuringTest = 0;
    private long seedWhenTestFinishes = 0;
    private long seedReportedInTestFailure = 0;

    long getSeedWhenTestStarts() {
        return seedWhenTestStarts;
    }

    Report setSeedWhenTestStarts(final long seedWhenTestStarts) {
        this.seedWhenTestStarts = seedWhenTestStarts;
        return this;
    }

    long getSeedWhenTestFinishes() {
        return seedWhenTestFinishes;
    }

    void setSeedWhenTestFinishes(final long seedWhenTestFinishes) {
        this.seedWhenTestFinishes = seedWhenTestFinishes;
    }

    long getSeedReportedInTestFailure() {
        return seedReportedInTestFailure;
    }

    void setSeedReportedInTestFailure(final long seedReportedInTestFailure) {
        this.seedReportedInTestFailure = seedReportedInTestFailure;
    }

    long getSeedDuringTest() {
        return seedDuringTest;
    }

    void setSeedDuringTest(final long seedDuringTest) {
        this.seedDuringTest = seedDuringTest;
    }
}
