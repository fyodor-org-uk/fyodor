package uk.org.fyodor.random;

import java.util.HashMap;
import java.util.Map;

final class Reporter {

    private final Map<String, Report> seedReportByMethod = new HashMap<String, Report>();

    void seedBeforeTestStarts(final Class<?> testClass, final String methodName, final long seed) {
        final String fullMethodName = testClass.getName() + "." + methodName;
        if (seedReportByMethod.containsKey(fullMethodName)) {
            seedReportByMethod.get(fullMethodName).setSeedWhenTestStarts(seed);
        }
        seedReportByMethod.put(fullMethodName, new Report().setSeedWhenTestStarts(seed));
    }

    void seedDuringTest(final Class<?> testClass, final String methodName, final long seed) {
        final String fullMethodName = testClass.getName() + "." + methodName;
        seedReportByMethod.get(fullMethodName).setSeedDuringTest(seed);
    }

    void seedAfterTestFinishes(final Class<?> testClass, final String methodName, final long seed) {
        final String fullMethodName = testClass.getName() + "." + methodName;
        seedReportByMethod.get(fullMethodName).setSeedWhenTestFinishes(seed);
    }

    void seedReportedWhenTestFails(final Class<?> testClass, final String methodName, final long seed) {
        final String fullMethodName = testClass.getName() + "." + methodName;
        seedReportByMethod.get(fullMethodName).setSeedReportedInTestFailure(seed);
    }

    Report reportFor(final Class<?> testClass, final String methodName) {
        final String fullMethodName = testClass.getName() + "." + methodName;
        return seedReportByMethod.get(fullMethodName);
    }
}
