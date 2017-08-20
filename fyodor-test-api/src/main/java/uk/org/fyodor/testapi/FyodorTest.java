package uk.org.fyodor.testapi;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static uk.org.fyodor.testapi.Annotatable.of;

public interface FyodorTest extends Annotatable {
    Annotatable testMethod();

    Annotatable testClass();

    @Override
    default <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
        return of(testMethod(), testClass()).getAnnotation(annotationClass);
    }
}
