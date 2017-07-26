package uk.org.fyodor.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import uk.org.fyodor.testapi.Annotatable;
import uk.org.fyodor.testapi.FyodorTest;

import java.lang.annotation.Annotation;
import java.util.Optional;

final class FyodorTestAdapter implements FyodorTest {

    private final ExtensionContext context;

    private FyodorTestAdapter(final ExtensionContext context) {
        this.context = context;
    }

    @Override
    public Annotatable testMethod() {
        return new Annotatable() {
            @Override
            public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                return context.getTestMethod().map(m -> m.getAnnotation(annotationClass));
            }
        };
    }

    @Override
    public Annotatable testClass() {
        return new Annotatable() {
            @Override
            public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annotationClass) {
                return context.getTestClass().map(c -> c.getAnnotation(annotationClass));
            }
        };
    }

    static FyodorTestAdapter fyodorTestOf(final ExtensionContext extensionContext) {
        return new FyodorTestAdapter(extensionContext);
    }
}
