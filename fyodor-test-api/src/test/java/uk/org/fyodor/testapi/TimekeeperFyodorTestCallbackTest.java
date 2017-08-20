package uk.org.fyodor.testapi;

import org.junit.Test;
import uk.org.fyodor.generators.RDG;
import uk.org.fyodor.testapi.TimekeeperFyodorTestCallback.TimeController;

import java.lang.annotation.Annotation;
import java.time.*;

import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static uk.org.fyodor.generators.RDG.zonedDateTime;
import static uk.org.fyodor.testapi.FyodorTestBuilder.fyodorTest;

public final class TimekeeperFyodorTestCallbackTest {

    @Test
    public void nextDateIsTheDateFromTheAnnotatedTestMethod() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final LocalDate annotatedDate = of(2000, 1, 1);
        underTest.beforeTestExecution(fyodorTest().withMethodAnnotations((Annotation) atDate(annotatedDate)).build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(annotatedDate, currentDateTime.toLocalTime(), currentDateTime.getZone()));
    }

    @Test
    public void nextTimeIsTheTimeFromTheAnnotatedTestMethod() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final LocalTime annotatedTime = LocalTime.of(11, 12, 13, 14);
        underTest.beforeTestExecution(fyodorTest().withMethodAnnotations((Annotation) atTime(annotatedTime)).build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(currentDateTime.toLocalDate(), annotatedTime, currentDateTime.getZone()));
    }

    @Test
    public void nextZoneIsTheZoneFromTheAnnotatedTestMethod() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final ZoneId annotatedZone = RDG.zoneId().next();
        underTest.beforeTestExecution(fyodorTest().withMethodAnnotations((Annotation) atZone(annotatedZone)).build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(currentDateTime.toLocalDate(), currentDateTime.toLocalTime(), annotatedZone));
    }

    @Test
    public void nextDateIsTheDateFromTheAnnotatedTestClass() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final LocalDate annotatedDate = of(2000, 1, 1);
        underTest.beforeTestExecution(fyodorTest().withClassAnnotations((Annotation) atDate(annotatedDate)).build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(annotatedDate, currentDateTime.toLocalTime(), currentDateTime.getZone()));
    }

    @Test
    public void nextTimeIsTheTimeFromTheAnnotatedTestClass() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final LocalTime annotatedTime = LocalTime.of(11, 12, 13, 14);
        underTest.beforeTestExecution(fyodorTest().withClassAnnotations((Annotation) atTime(annotatedTime)).build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(currentDateTime.toLocalDate(), annotatedTime, currentDateTime.getZone()));
    }

    @Test
    public void nextZoneIsTheZoneFromTheAnnotatedTestClass() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final ZoneId annotatedZone = RDG.zoneId().next();
        underTest.beforeTestExecution(fyodorTest().withClassAnnotations((Annotation) atZone(annotatedZone)).build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(currentDateTime.toLocalDate(), currentDateTime.toLocalTime(), annotatedZone));
    }

    @Test
    public void methodAnnotationHavePriorityOverClassAnnotation() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final LocalDate classDate = of(1999, 12, 31);
        final LocalTime classTime = LocalTime.of(23, 59, 59);
        final ZoneId classZone = ZoneId.of("Europe/London");

        final LocalDate methodDate = of(2000, 1, 1);
        final LocalTime methodTime = LocalTime.of(0, 0, 0, 1);
        final ZoneId methodZone = ZoneId.of("Europe/Paris");

        underTest.beforeTestExecution(fyodorTest()
                .withClassAnnotations(atDate(classDate), atTime(classTime), atZone(classZone))
                .withMethodAnnotations(atDate(methodDate), atTime(methodTime), atZone(methodZone))
                .build());

        assertThat(timeController.nextDateTime())
                .isEqualTo(ZonedDateTime.of(methodDate, methodTime, methodZone));
    }

    @Test
    public void revertsToPreviousDateTimeAndZone() {
        final StubTimeController timeController = new StubTimeController(zonedDateTime().next());
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final LocalDate methodDate = of(2000, 1, 1);
        final LocalTime methodTime = LocalTime.of(0, 0, 0, 1);
        final ZoneId methodZone = ZoneId.of("Europe/Paris");

        final FyodorTest test = fyodorTest()
                .withMethodAnnotations(atDate(methodDate), atTime(methodTime), atZone(methodZone))
                .build();

        underTest.beforeTestExecution(test);
        underTest.afterTestExecution(test);

        assertThat(timeController.revertedToPreviousDateTimeAndZone())
                .isTrue();
    }

    @Test
    public void doesNotSetNextDateAndDoesNotRevertDateIfItIsInvalid() {
        final StubTimeController timeController = new StubTimeController(zonedDateTime().next());
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final FyodorTest test = fyodorTest()
                .withMethodAnnotations(atDate("this-is-an-invalid-date-and-should-cause-an-exception"))
                .build();

        try {
            underTest.beforeTestExecution(test);
            fail("An exception should have been thrown here");
        } catch (final DateTimeException e) {
            //Ignore - we are expecting this
        }
        assertThat(timeController.nextDateTime()).isNull();

        underTest.afterTestExecution(test);
        assertThat(timeController.revertedToPreviousDateTimeAndZone())
                .describedAs("The date time and zone should not be reverted if it was never set in the first place")
                .isFalse();
    }

    @Test
    public void doesNotSetNextTimeAndDoesNotRevertTimeIfItIsInvalid() {
        final StubTimeController timeController = new StubTimeController(zonedDateTime().next());
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final FyodorTest test = fyodorTest()
                .withMethodAnnotations(atTime("this-is-an-invalid-time-and-should-cause-an-exception"))
                .build();

        try {
            underTest.beforeTestExecution(test);
            fail("An exception should have been thrown here");
        } catch (final DateTimeException e) {
            //Ignore - we are expecting this
        }
        assertThat(timeController.nextDateTime()).isNull();

        underTest.afterTestExecution(test);
        assertThat(timeController.revertedToPreviousDateTimeAndZone())
                .describedAs("The date time and zone should not be reverted if it was never set in the first place")
                .isFalse();
    }

    @Test
    public void doesNotSetNextZoneAndDoesNotRevertZoneIfItIsInvalid() {
        final StubTimeController timeController = new StubTimeController(zonedDateTime().next());
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final FyodorTest test = fyodorTest()
                .withMethodAnnotations(atZone("this-is-an-invalid-zone-id-and-should-cause-an-exception"))
                .build();

        try {
            underTest.beforeTestExecution(test);
            fail("An exception should have been thrown here");
        } catch (final DateTimeException e) {
            //Ignore - we are expecting this
        }
        assertThat(timeController.nextDateTime()).isNull();

        underTest.afterTestExecution(test);
        assertThat(timeController.revertedToPreviousDateTimeAndZone())
                .describedAs("The date time and zone should not be reverted if it was never set in the first place")
                .isFalse();
    }

    @Test
    public void setsAndRevertsNextDateTimeAndZoneEvenWhenThereAreNoAnnotations() {
        final ZonedDateTime currentDateTime = zonedDateTime().next();
        final StubTimeController timeController = new StubTimeController(currentDateTime);
        final TimekeeperFyodorTestCallback underTest = new TimekeeperFyodorTestCallback(timeController);

        final FyodorTest test = fyodorTest().build();

        underTest.beforeTestExecution(test);
        assertThat(timeController.nextDateTime()).isEqualTo(currentDateTime);

        underTest.afterTestExecution(test);
        assertThat(timeController.revertedToPreviousDateTimeAndZone())
                .describedAs("The date time and zone should be reverted if it was set successfully")
                .isTrue();
    }

    private static AtDate atDate(final LocalDate date) {
        return atDate(date.toString());
    }

    private static AtDate atDate(final String dateString) {
        return new AtDate() {
            @Override
            public String value() {
                return dateString;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtDate.class;
            }
        };
    }

    private static AtTime atTime(final LocalTime time) {
        return atTime(time.toString());
    }

    private static AtTime atTime(final String timeString) {
        return new AtTime() {
            @Override
            public String value() {
                return timeString;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtTime.class;
            }
        };
    }

    private static AtZone atZone(final ZoneId zone) {
        return atZone(zone.getId());
    }

    private static AtZone atZone(final String zoneId) {
        return new AtZone() {
            @Override
            public String value() {
                return zoneId;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtZone.class;
            }
        };
    }

    private static final class StubTimeController implements TimeController {

        private final ZonedDateTime currentDateTime;

        private ZonedDateTime nextDateTime;
        private boolean reverted;

        StubTimeController(final ZonedDateTime currentDateTime) {
            this.currentDateTime = currentDateTime;
        }

        @Override
        public LocalDate currentDate() {
            return currentDateTime.toLocalDate();
        }

        @Override
        public LocalTime currentTime() {
            return currentDateTime.toLocalTime();
        }

        @Override
        public ZoneId currentZone() {
            return currentDateTime.getZone();
        }

        @Override
        public void setDateTimeAndZone(final ZonedDateTime dateTimeAndZone) {
            if (nextDateTime != null) {
                throw new IllegalStateException("date time and zone have already been set");
            }
            this.nextDateTime = dateTimeAndZone;
        }

        @Override
        public void revertToPreviousDateTimeAndZone() {
            if (reverted) {
                throw new IllegalStateException("date time and zone have already been reverted");
            }
            this.reverted = true;
        }

        ZonedDateTime nextDateTime() {
            return nextDateTime;
        }

        boolean revertedToPreviousDateTimeAndZone() {
            return reverted;
        }
    }
}