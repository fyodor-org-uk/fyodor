package uk.org.fyodor.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.generators.time.Timekeeper;
import uk.org.fyodor.testapi.Current;

import java.lang.reflect.Parameter;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static uk.org.fyodor.generators.time.Timekeeper.current;

final class TemporalityParameterResolver implements ParameterResolver {

    private static final Map<Class<?>, Supplier<Object>> parameterSuppliersByType = new HashMap<Class<?>, Supplier<Object>>() {{
        put(LocalDate.class, () -> current().date());
        put(LocalTime.class, () -> current().time());
        put(LocalDateTime.class, () -> current().dateTime());
        put(ZonedDateTime.class, () -> current().zonedDateTime());
        put(ZoneId.class, () -> current().zone());
        put(Temporality.class, Timekeeper::current);
    }};

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();

        return parameter.getType().equals(Temporality.class)
                || isSupportedJavaTimeParameterWithCurrentAnnotation(parameter);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterSuppliersByType.get(parameterContext.getParameter().getType()).get();
    }

    private static boolean isSupportedJavaTimeParameterWithCurrentAnnotation(final Parameter parameter) {
        return parameterSuppliersByType.containsKey(parameter.getType())
                && parameter.isAnnotationPresent(Current.class);
    }
}
