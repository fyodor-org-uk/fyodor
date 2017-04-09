package uk.org.fyodor.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

final class TestFailureListener<T> extends RunListener {

    private final Reporter<T> reporter;
    private final ObjectDuringFailureSupplier<T> obtainObject;

    private TestFailureListener(final Reporter<T> reporter, final ObjectDuringFailureSupplier<T> obtainObject) {
        this.reporter = reporter;
        this.obtainObject = obtainObject;
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        final Description testDescription = failure.getDescription();
        reporter.objectReportedWhenTestFails(
                testDescription.getTestClass(),
                testDescription.getMethodName(),
                obtainObject.objectDuringFailure(failure),
                failure.getException());
    }

    static <T> RunListener testFailed(final Reporter<T> reporter, final ObjectDuringFailureSupplier<T> obtainObject) {
        return new TestFailureListener<>(reporter, obtainObject);
    }

    interface ObjectDuringFailureSupplier<T> {
        T objectDuringFailure(Failure failure);
    }
}
