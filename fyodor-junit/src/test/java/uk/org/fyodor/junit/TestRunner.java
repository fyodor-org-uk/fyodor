package uk.org.fyodor.junit;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public final class TestRunner<T> {

    private final List<RunListener> runListeners;
    private final Map<Class<?>, T> startingObjectByTestClass = new HashMap<>();
    private Consumer<T> whatToDoWithT = t -> {
    };

    public TestRunner(final RunListener... runListeners) {
        this.runListeners = asList(runListeners);
    }

    public TestRunner<T> scheduleTestWithObject(final Class<?> testClass, final T startingObject, final Consumer<T> whatToDoWithT) {
        this.whatToDoWithT = whatToDoWithT;
        this.startingObjectByTestClass.put(testClass, startingObject);
        return this;
    }

    public TestRunner<T> scheduleTest(final Class<?> testClass) {
        this.startingObjectByTestClass.put(testClass, null);
        return this;
    }

    public void run() {
        for (final Class<?> testClass : startingObjectByTestClass.keySet()) {
            executeTest(testClass);
        }
    }

    public void runInParallel() {
        final List<Runnable> runnableTestClasses = new LinkedList<>();
        for (final Class<?> testClass : startingObjectByTestClass.keySet()) {
            runnableTestClasses.add(() -> executeTest(testClass));
        }
        executeTestsAndWaitForThemToFinish(runnableTestClasses);
    }

    private void executeTest(final Class<?> testClass) {
        final T object = startingObjectByTestClass.get(testClass);
        if (object == null) {
            executeJunitTest(testClass);
        } else {
            executeJunitTestWithSpecificObject(testClass, object);
        }
    }

    private void executeJunitTestWithSpecificObject(final Class<?> testClass, final T object) {
        whatToDoWithT.accept(object);
        executeJunitTest(testClass);
    }

    private void executeJunitTest(final Class<?> testClass) {
        try {
            new JUnit4(testClass).run(runNotifierWith(runListeners));
        } catch (final InitializationError initializationError) {
            throw new RuntimeException(initializationError);
        }
    }

    private static RunNotifier runNotifierWith(final List<RunListener> runListeners) {
        final RunNotifier runNotifier = new RunNotifier();
        for (final RunListener listener : runListeners) {
            runNotifier.addListener(listener);
        }
        return runNotifier;
    }

    private static void executeTestsAndWaitForThemToFinish(final List<Runnable> listOfRunnables) {
        final ExecutorService executorService = Executors.newFixedThreadPool(listOfRunnables.size());
        for (final Runnable runnableTestClass : listOfRunnables) {
            executorService.execute(runnableTestClass);
        }
        executorService.shutdown();
        try {
            boolean finished = executorService.awaitTermination(1, TimeUnit.MINUTES);
            if (!finished) {
                throw new IllegalStateException("tests took too long to finish");
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
