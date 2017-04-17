package uk.org.fyodor.testapi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

final class FyodorTestBuilder {

    private final List<Annotation> classAnnotations = new ArrayList<>();
    private final List<Annotation> methodAnnotations = new ArrayList<>();

    private FyodorTestBuilder() {
    }

    FyodorTestBuilder withMethodAnnotations(final Annotation... methodAnnotation) {
        this.methodAnnotations.addAll(Arrays.asList(methodAnnotation));
        return this;
    }

    FyodorTestBuilder withClassAnnotations(final Annotation... classAnnotation) {
        this.classAnnotations.addAll(Arrays.asList(classAnnotation));
        return this;
    }

    FyodorTest build() {
        return new FyodorTest() {
            @Override
            public Annotatable testMethod() {
                return annotatableOf(methodAnnotations);
            }

            @Override
            public Annotatable testClass() {
                return annotatableOf(classAnnotations);
            }
        };
    }

    static FyodorTestBuilder fyodorTest() {
        return new FyodorTestBuilder();
    }

    private static Annotatable annotatableOf(final Iterable<Annotation> annotations) {
        return new Annotatable() {
            @SuppressWarnings("unchecked")
            @Override
            public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                for (final Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(annotationClass)) {
                        return Optional.of((A) annotation);
                    }
                }
                return Optional.empty();
            }
        };
    }
}
