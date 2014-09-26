package uk.org.fyodor.random;

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

import static java.util.Arrays.asList;

final class TestRunner {

    private final List<RunListener> runListeners;
    private final Map<Class<?>, Long> startingSeedByTestClass = new HashMap<Class<?>, Long>();

    TestRunner(final RunListener... runListeners) {
        this.runListeners = asList(runListeners);
    }

    TestRunner scheduleTestWithInitialSeed(final Class<?> testClass, final long startingSeed) {
        startingSeedByTestClass.put(testClass, startingSeed);
        return this;
    }

    TestRunner scheduleTest(final Class<?> testClass) {
        startingSeedByTestClass.put(testClass, null);
        return this;
    }

    void run() {
        for (final Class<?> testClass : startingSeedByTestClass.keySet()) {
            executeTest(testClass);
        }
    }

    void runInParallel() {
        final List<Runnable> runnableTestClasses = new LinkedList<Runnable>();
        for (final Class<?> testClass : startingSeedByTestClass.keySet()) {
            runnableTestClasses.add(new Runnable() {
                @Override
                public void run() {
                    executeTest(testClass);
                }
            });
        }
        executeTestsAndWaitForThemToFinish(runnableTestClasses);
    }

    private void executeTest(final Class<?> testClass) {
        final Long seed = startingSeedByTestClass.get(testClass);
        if (seed == null) {
            executeJunitTest(testClass);
        } else {
            executeSeededJunitTest(testClass, seed);
        }
    }

    private void executeSeededJunitTest(final Class<?> testClass, final long seed) {
        RandomValuesProvider.seed().next(seed);
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
