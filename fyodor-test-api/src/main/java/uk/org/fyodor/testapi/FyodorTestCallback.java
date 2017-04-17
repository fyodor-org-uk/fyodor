package uk.org.fyodor.testapi;

public interface FyodorTestCallback {

    void beforeTestExecution(FyodorTest test);

    void afterTestExecution(FyodorTest test);

    void testFailed(FyodorTest test, Throwable throwable) throws Throwable;
}
