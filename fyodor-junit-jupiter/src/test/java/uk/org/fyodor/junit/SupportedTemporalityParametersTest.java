package uk.org.fyodor.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import uk.org.fyodor.generators.time.Temporality;
import uk.org.fyodor.testapi.Current;

import java.lang.reflect.Parameter;
import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.junit.SupportedTemporalityParametersTest.Parameters.*;

final class SupportedTemporalityParametersTest {

    private final TemporalityParameterResolver underTest = new TemporalityParameterResolver();

    @Test
    void supportsTemporalityParameter() {
        assertThat(resolverSupports(currentTemporality()))
                .describedAs("@Temporality parameters with a @Current annotation are supported")
                .isTrue();

        assertThat(resolverSupports(unAnnotatedTemporality()))
                .describedAs("Temporality parameters without a @Current annotation are supported")
                .isTrue();
    }

    @Test
    void supportsAnnotatedTemporalParameterTypes() {
        assertThat(resolverSupports(currentLocalDate()))
                .describedAs("@Current on LocalDate parameters is supported")
                .isTrue();

        assertThat(resolverSupports(currentLocalTime()))
                .describedAs("@Current on LocalTime parameters is supported")
                .isTrue();

        assertThat(resolverSupports(currentLocalDateTime()))
                .describedAs("@Current on LocalDateTime parameters is supported")
                .isTrue();

        assertThat(resolverSupports(currentZonedDateTime()))
                .describedAs("@Current on ZonedDateTime parameters is supported")
                .isTrue();

        assertThat(resolverSupports(currentZoneId()))
                .describedAs("@Current on ZoneId parameters is supported")
                .isTrue();
    }

    @Test
    void doesNotSupportTemporalTypesWithoutCurrentAnnotation() {
        assertThat(resolverSupports(unAnnotatedLocalDate()))
                .describedAs("LocalDate parameters without an @Current annotation are not supported")
                .isFalse();

        assertThat(resolverSupports(unAnnotatedLocalTime()))
                .describedAs("LocalTime parameters without an @Current annotation are not supported")
                .isFalse();

        assertThat(resolverSupports(unAnnotatedLocalDateTime()))
                .describedAs("LocalDateTime parameters without an @Current annotation are not supported")
                .isFalse();

        assertThat(resolverSupports(unAnnotatedZonedDateTime()))
                .describedAs("ZonedDateTime parameters without an @Current annotation are not supported")
                .isFalse();

        assertThat(resolverSupports(unAnnotatedZoneId()))
                .describedAs("ZoneId parameters without an @Current annotation are not supported")
                .isFalse();
    }

    @Test
    void doesNotSupportOtherTypes() {
        assertThat(resolverSupports(currentString()))
                .describedAs("@Current on String parameters is not supported")
                .isFalse();

        assertThat(resolverSupports(currentLong()))
                .describedAs("@Current on Long parameters is not supported")
                .isFalse();

        assertThat(resolverSupports(currentPeriod()))
                .describedAs("@Current on Period parameters is not supported")
                .isFalse();
    }

    private boolean resolverSupports(final Parameter stringParameter) {
        return underTest.supports(parameterContextOf(stringParameter), null);
    }

    private static ParameterContext parameterContextOf(final Parameter parameter) {
        return new ParameterContext() {
            @Override
            public Parameter getParameter() {
                return parameter;
            }

            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public Optional<Object> getTarget() {
                return Optional.empty();
            }
        };
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    static final class Parameters {

        public void methodWithCurrentStringParameter(@Current final String parameter) {
        }

        public void methodWithCurrentLongParameter(@Current final Long parameter) {
        }

        public void methodWithCurrentLocalDateParameter(@Current final LocalDate parameter) {
        }

        public void methodWithCurrentLocalTimeParameter(@Current final LocalTime parameter) {
        }

        public void methodWithCurrentLocalDateTimeParameter(@Current final LocalDateTime parameter) {
        }

        public void methodWithCurrentZonedDateTimeParameter(@Current final ZonedDateTime parameter) {
        }

        public void methodWithCurrentZoneIdParameter(@Current final ZoneId parameter) {
        }

        public void methodWithCurrentPeriodParameter(@Current final Period parameter) {
        }

        public void methodWithCurrentTemporalityParameter(@Current final Temporality parameter) {
        }

        public void methodWithLocalDateParameter(final LocalDate parameter) {
        }

        public void methodWithLocalTimeParameter(final LocalTime parameter) {
        }

        public void methodWithLocalDateTimeParameter(final LocalDateTime parameter) {
        }

        public void methodWithZonedDateTimeParameter(final ZonedDateTime parameter) {
        }

        public void methodWithZoneIdParameter(final ZoneId parameter) {
        }

        public void methodWithPeriodParameter(final Period parameter) {
        }

        public void methodWithTemporalityParameter(final Temporality parameter) {
        }

        static Parameter currentLong() {
            return getParameter("methodWithCurrentLongParameter", Long.class);
        }

        static Parameter currentPeriod() {
            return getParameter("methodWithCurrentPeriodParameter", Period.class);
        }

        static Parameter currentString() {
            return getParameter("methodWithCurrentStringParameter", String.class);
        }

        static Parameter currentLocalDate() {
            return getParameter("methodWithCurrentLocalDateParameter", LocalDate.class);
        }

        static Parameter currentLocalTime() {
            return getParameter("methodWithCurrentLocalTimeParameter", LocalTime.class);
        }

        static Parameter currentLocalDateTime() {
            return getParameter("methodWithCurrentLocalDateTimeParameter", LocalDateTime.class);
        }

        static Parameter currentZonedDateTime() {
            return getParameter("methodWithCurrentZonedDateTimeParameter", ZonedDateTime.class);
        }

        static Parameter currentZoneId() {
            return getParameter("methodWithCurrentZoneIdParameter", ZoneId.class);
        }

        static Parameter currentTemporality() {
            return getParameter("methodWithCurrentTemporalityParameter", Temporality.class);
        }

        static Parameter unAnnotatedLocalDate() {
            return getParameter("methodWithLocalDateParameter", LocalDate.class);
        }

        static Parameter unAnnotatedLocalTime() {
            return getParameter("methodWithLocalTimeParameter", LocalTime.class);
        }

        static Parameter unAnnotatedLocalDateTime() {
            return getParameter("methodWithLocalDateTimeParameter", LocalDateTime.class);
        }

        static Parameter unAnnotatedZonedDateTime() {
            return getParameter("methodWithZonedDateTimeParameter", ZonedDateTime.class);
        }

        static Parameter unAnnotatedZoneId() {
            return getParameter("methodWithZoneIdParameter", ZoneId.class);
        }

        static Parameter unAnnotatedTemporality() {
            return getParameter("methodWithTemporalityParameter", Temporality.class);
        }

        private static Parameter getParameter(final String name, final Class<?> type) {
            try {
                return Parameters.class.getMethod(name, type).getParameters()[0];
            } catch (final NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}