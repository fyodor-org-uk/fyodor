package uk.org.fyodor.generators.time;

import org.junit.Test;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.RDG.localDate;
import static uk.org.fyodor.generators.time.LocalDateRange.today;

public class TimekeeperTest {

    @Test
    public void todayCanBeConfiguredFromSystemClock() {
        Timekeeper.from(Clock.systemDefaultZone());

        final LocalDate systemDate = LocalDate.now();
        final LocalDate today = Timekeeper.currentDate();

        assertThat(today).isEqualTo(systemDate);
    }

    @Test
    public void todayCanBeConfiguredByCustomClock() {
        final LocalDate systemDate = LocalDate.now();
        final Instant systemDateAsInstant = systemDate.atStartOfDay().toInstant(ZoneOffset.ofHours(0));

        Timekeeper.from(Clock.fixed(systemDateAsInstant, ZoneId.systemDefault()));

        final LocalDate today = Timekeeper.currentDate();

        assertThat(today).isEqualTo(systemDate);
    }

    @Test
    public void currentDateIsSpecificToTheCurrentThread() throws InterruptedException {
        final LocalDate firstDate = LocalDate.now().minusDays(1);
        final LocalDate secondDate = LocalDate.now();
        final LocalDate thirdDate = LocalDate.now().plusDays(1);

        final ResultHolder firstResult = new ResultHolder();
        final ResultHolder secondResult = new ResultHolder();
        final ResultHolder thirdResult = new ResultHolder();

        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.execute(setAndThenGetCurrentDate(thirdDate, thirdResult));
            executorService.execute(setAndThenGetCurrentDate(firstDate, firstResult));
            executorService.execute(setAndThenGetCurrentDate(secondDate, secondResult));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        assertThat(firstResult.uniqueValues()).containsExactly(firstDate);
        assertThat(secondResult.uniqueValues()).containsExactly(secondDate);
        assertThat(thirdResult.uniqueValues()).containsExactly(thirdDate);
    }

    private static Runnable setAndThenGetCurrentDate(final LocalDate fixedDate, final ResultHolder resultHolder) {
        return () -> {
            Timekeeper.from(fixedClock(fixedDate));
            final LocalDate today = localDate(today()).next();
            resultHolder.addResult(today);
        };
    }

    private static Clock fixedClock(final LocalDate today) {
        return Clock.fixed(today.atTime(12, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }

    static final class ResultHolder {
        private List<LocalDate> values = new CopyOnWriteArrayList<>();

        void addResult(final LocalDate result) {
            values.add(result);
        }

        Set<LocalDate> uniqueValues() {
            return new HashSet<>(values);
        }
    }
}