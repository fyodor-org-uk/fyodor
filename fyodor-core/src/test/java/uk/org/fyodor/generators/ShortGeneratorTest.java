package uk.org.fyodor.generators;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.org.fyodor.Sampler.Sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.generators.RDG.shortVal;
import static uk.org.fyodor.random.RandomSourceProvider.seed;
import static uk.org.fyodor.range.Range.closed;

public class ShortGeneratorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void generatesShort() {
        final int sampleSize = 20;
        final int shortRange = 65536; //-32768..0..32767

        final Sample<Short> sample = from(shortVal()).sample(sampleSize * shortRange);

        assertThat(sample.unique().size()).isEqualTo(shortRange);
    }

    @Test
    public void doesNotGenerateNulls() {
        final Sample<Short> sample = from(shortVal()).sample(1000);

        assertThat(sample.unique()).doesNotContain((Short) null);
    }

    @Test
    public void generatesSameShortForSameSeed() {
        final long initialSeed = RDG.longVal().next();

        seed().next(initialSeed);

        final Short first = shortVal().next();
        final Short second = shortVal().next();

        seed().next(initialSeed);

        final Short third = shortVal().next();

        assertThat(first).isEqualTo(third);
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void generatesShortWithinRange() {
        final Sample<Short> sample = from(shortVal(closed(32765, 32767))).sample(100);

        assertThat(sample.map(Short::intValue).unique())
                .contains(32765, 32766, 32767);
    }

    @Test
    public void upperBoundMustBeWithinShortBounds() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Upper bound 32768 must be within the range -32768..32767"));

        shortVal(closed((int) Short.MIN_VALUE, Short.MAX_VALUE + 1));
    }

    @Test
    public void lowerBoundMustBeWithinShortBounds() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Lower bound -32769 must be within the range -32768..32767"));

        shortVal(closed(Short.MIN_VALUE - 1, (int) Short.MAX_VALUE));
    }
}
