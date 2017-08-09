package uk.org.fyodor.generators;

import org.junit.Test;
import uk.org.fyodor.Sampler.Sample;

import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.RDG.uuid;
import static uk.org.fyodor.random.RandomSourceProvider.seed;

public final class UuidGeneratorTest {

    @Test
    public void doesNotGenerateNulls() {
        final Sample<UUID> sample = from(uuid()).sample(1000);
        assertThat(sample.unique()).doesNotContain((UUID) null);
    }

    @Test
    public void generateUniqueUuids() {
        final Sample<UUID> sample = from(uuid()).sample(100000);
        assertThat(sample.unique()).hasSize(100000);
    }

    @Test
    public void generatesSameUuidForSameSeed() {
        final long initialSeed = RDG.longVal().next();
        seed().next(initialSeed);

        final UUID first = uuid().next();
        final UUID second = uuid().next();

        seed().next(initialSeed);
        final UUID third = uuid().next();

        assertThat(first).isEqualTo(third);
        assertThat(first).isNotEqualTo(second);
    }
}
