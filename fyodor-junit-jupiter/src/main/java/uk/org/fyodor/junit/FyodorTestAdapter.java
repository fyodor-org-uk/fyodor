package uk.org.fyodor.junit;

import org.junit.jupiter.api.extension.TestExtensionContext;
import uk.org.fyodor.testapi.Annotatable;
import uk.org.fyodor.testapi.FyodorTest;

import java.lang.annotation.Annotation;
import java.util.Optional;

final class FyodorTestAdapter implements FyodorTest {

    private final TestExtensionContext context;

    private FyodorTestAdapter(final TestExtensionContext context) {
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

    static FyodorTestAdapter fyodorTestOf(final TestExtensionContext testExtensionContext) {
        return new FyodorTestAdapter(testExtensionContext);
    }
}
