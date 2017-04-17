package uk.org.fyodor.testapi;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface Annotatable {
    <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass);

    static Annotatable of(final Annotatable... annotatables) {
        return new Annotatable() {
            @Override
            public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                for (final Annotatable annotatable : annotatables) {
                    final Optional<A> annotation = annotatable.getAnnotation(annotationClass);
                    if (annotation.isPresent()) {
                        return annotation;
                    }
                }
                return Optional.empty();
            }
        };
    }
}
