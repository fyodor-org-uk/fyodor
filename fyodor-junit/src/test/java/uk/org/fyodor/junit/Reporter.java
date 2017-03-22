package uk.org.fyodor.junit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Reporter<T> {

    private final Map<String, Report<T>> objectReportByMethod = new ConcurrentHashMap<>();

    void objectBeforeTestStarts(final Class<?> testClass, final String testMethod, final T object) {
        final String testName = testName(testClass, testMethod);

        objectReportByMethod.put(
                testName,
                new Report<T>().setObjectWhenTestStarts(object));
    }

    public void objectDuringTest(final Class<?> testClass, final String testMethod, final T object) {
        final String testName = testName(testClass, testMethod);

        objectReportByMethod.get(testName)
                .setObjectDuringTest(object);
    }

    void objectAfterTestFinishes(final Class<?> testClass, final String testMethod, final T object) {
        final String testName = testName(testClass, testMethod);

        objectReportByMethod.get(testName)
                .setObjectWhenTestFinishes(object);
    }

    void objectReportedWhenTestFails(final Class<?> testClass, final String testMethod, final T object, final Throwable cause) {
        final String testName = testName(testClass, testMethod);

        objectReportByMethod.get(testName)
                .setObjectReportedInTestFailure(object)
                .setFailureCause(cause);
    }

    public Report<T> reportFor(final Class<?> testClass, final String methodName) {
        final String fullMethodName = testName(testClass, methodName);
        return objectReportByMethod.get(fullMethodName);
    }

    public static <T> Reporter<T> reporter() {
        return new Reporter<>();
    }

    private static String testName(final Class<?> testClass, final String testMethod) {
        return testClass.getSimpleName() + "." + testMethod;
    }
}
