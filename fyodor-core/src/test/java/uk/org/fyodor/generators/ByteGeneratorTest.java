package uk.org.fyodor.generators;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler.Sample;

import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.RDG.byteArray;
import static uk.org.fyodor.generators.RDG.byteVal;
import static uk.org.fyodor.random.RandomSourceProvider.seed;
import static uk.org.fyodor.range.Range.closed;

public final class ByteGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void generatesUniqueByteArrays() {
        final Sample<byte[]> sample = from(byteArray()).sample(100);

        assertThat(sample.map(Arrays::hashCode).unique()).hasSize(100);
    }

    @Test
    public void generatesSameByteArrayForSameSeed() {
        final long initialSeed = RDG.longVal().next();

        seed().next(initialSeed);

        final byte[] first = byteArray().next();
        final byte[] second = byteArray().next();

        seed().next(initialSeed);

        final byte[] third = byteArray().next();

        assertThat(Arrays.hashCode(first)).isEqualTo(Arrays.hashCode(third));
        assertThat(Arrays.hashCode(first)).isNotEqualTo(Arrays.hashCode(second));
    }

    @Test
    public void generatesByte() {
        final int sampleSize = 10;
        final int byteRange = 256; //-128..0..+127

        final Sample<Byte> sample = from(byteVal()).sample(sampleSize * byteRange);

        assertThat(sample.unique().size()).isEqualTo(byteRange);
    }

    @Test
    public void doesNotGenerateNulls() {
        final Sample<Byte> sample = from(byteVal()).sample(1000);

        assertThat(sample.unique()).doesNotContain((Byte) null);
    }

    @Test
    public void generatesSameByteForSameSeed() {
        final long initialSeed = RDG.longVal().next();

        seed().next(initialSeed);

        final Byte first = byteVal().next();
        final Byte second = byteVal().next();

        seed().next(initialSeed);

        final Byte third = byteVal().next();

        assertThat(first).isEqualTo(third);
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void generatesByteWithinRange() {
        final Sample<Byte> sample = from(byteVal(closed(120, 126))).sample(100);

        assertThat(sample.map(toInt()).unique())
                .contains(120, 121, 122, 123, 124, 125, 126);
    }

    @Test
    public void upperBoundMustBeWithinByteBounds() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Upper bound 128 must be within the range -128..127"));

        byteVal(closed((int) Byte.MIN_VALUE, Byte.MAX_VALUE + 1));
    }

    @Test
    public void lowerBoundMustBeWithinByteBounds() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Lower bound -129 must be within the range -128..127"));

        byteVal(closed(Byte.MIN_VALUE - 1, (int) Byte.MAX_VALUE));
    }

    private Function<Byte, Integer> toInt() {
        return b -> (int) b;
    }
}
