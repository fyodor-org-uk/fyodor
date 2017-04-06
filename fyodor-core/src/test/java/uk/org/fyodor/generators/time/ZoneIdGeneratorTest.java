package uk.org.fyodor.generators.time;

import org.junit.Test;
import uk.org.fyodor.Sampler.Sample;

import java.time.ZoneId;
import java.util.List;

import static java.time.ZoneId.getAvailableZoneIds;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.RDG.zoneId;

public final class ZoneIdGeneratorTest {

    @Test
    public void generatesRegionBasedZoneIds() {
        final Sample<ZoneId> sample = from(zoneId()).sample(10000);

        assertThat(sample.unique()).containsAll(availableZones());
    }

    private static List<ZoneId> availableZones() {
        return getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .collect(toList());
    }
}
