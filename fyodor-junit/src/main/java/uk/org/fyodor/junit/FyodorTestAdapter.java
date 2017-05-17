package uk.org.fyodor.junit;

import org.junit.runner.Description;
import uk.org.fyodor.testapi.Annotatable;
import uk.org.fyodor.testapi.FyodorTest;

import java.lang.annotation.Annotation;
import java.util.Optional;

final class FyodorTestAdapter implements FyodorTest {

    private final Description description;

    private FyodorTestAdapter(final Description description) {
        this.description = description;
    }

    @Override
    public Annotatable testMethod() {
        return new Annotatable() {
            @Override
            public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                return Optional.ofNullable(description.getAnnotation(annotationClass));
            }
        };
    }

    @Override
    public Annotatable testClass() {
        return new Annotatable() {
            @Override
            public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                return Optional.ofNullable(description.getTestClass().getAnnotation(annotationClass));
            }
        };
    }

    static FyodorTest fyodorTestOf(final Description description) {
        return new FyodorTestAdapter(description);
    }
}
