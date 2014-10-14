package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.CharacterFilter;
import uk.org.fyodor.generators.characters.CharacterSetFilter;
import uk.org.fyodor.generators.characters.CharacterSetGenerator;
import uk.org.fyodor.generators.characters.CharacterSetRange;
import uk.org.fyodor.generators.collections.ArrayGenerator;
import uk.org.fyodor.generators.collections.ListGenerator;
import uk.org.fyodor.generators.collections.MapGenerator;
import uk.org.fyodor.generators.collections.SetGenerator;
import uk.org.fyodor.range.Range;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static uk.org.fyodor.generators.Validations.*;
import static uk.org.fyodor.random.RandomValuesProvider.randomValues;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public class RDG {

    public static Generator<Integer> integer() {
        return integer(closed(Integer.MIN_VALUE, Integer.MAX_VALUE));
    }

    public static Generator<Integer> integer(final int maximum) {
        ensure(isNotNegative(maximum), "maximum cannot be negative");

        return integer(closed(0, maximum));
    }

    public static Generator<Integer> integer(final Range<Integer> range) {
        ensure(isNotNull(range), "range cannot be null");

        return new IntegerGenerator(randomValues(), range);
    }

    public static Generator<Boolean> bool() {
        return new BooleanGenerator(randomValues());
    }

    public static Generator<Long> longVal() {
        return longVal(closed(Long.MIN_VALUE, Long.MAX_VALUE));
    }

    public static Generator<Long> longVal(final long maximum) {
        ensure(isNotNegative(maximum), "maximum cannot be negative");

        return longVal(closed(0l, maximum));
    }

    public static Generator<Long> longVal(final Range<Long> range) {
        ensure(isNotNull(range), "range cannot be null");

        return new LongGenerator(randomValues(), range);
    }

    public static Generator<Double> doubleVal() {
        return doubleVal(closed(Double.MIN_VALUE, Double.MAX_VALUE));
    }

    public static Generator<Double> doubleVal(final double maximum) {
        ensure(isNotNegative(maximum), "maximum cannot be negative");

        return doubleVal(closed(0.0, maximum));
    }

    public static Generator<Double> doubleVal(final Range<Double> range) {
        ensure(isNotNull(range), "range cannot be null");

        final double lowerBound = range.lowerBound();
        ensure(isNumber(lowerBound) && isNotInfinite(lowerBound), "lower bound must be a number and cannot be infinite");

        final double upperBound = range.upperBound();
        ensure(isNumber(upperBound) && isNotInfinite(upperBound), "upper bound must be a number and cannot be infinite");

        return new DoubleGenerator(randomValues(), range);
    }

    public static Generator<BigDecimal> bigDecimal() {
        final BigDecimal min = BigDecimal.valueOf(Double.MIN_VALUE);
        final BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);

        return bigDecimal(closed(min, max));
    }

    public static Generator<BigDecimal> bigDecimal(final double val) {
        return bigDecimal(Range.fixed(BigDecimal.valueOf(val)));
    }

    public static Generator<BigDecimal> bigDecimal(final long val) {
        return bigDecimal(Range.fixed(BigDecimal.valueOf(val)));
    }

    public static Generator<BigDecimal> bigDecimal(final BigDecimal val) {
        return bigDecimal(Range.fixed(val));
    }

    public static Generator<BigDecimal> bigDecimal(final Range<BigDecimal> range) {
        return bigDecimal(range, 2);
    }

    public static Generator<BigDecimal> bigDecimal(final Range<BigDecimal> range, final int scale) {
        ensure(isNotNull(range), "range cannot be null");
        ensure(isNotNegative(scale), "scale cannot be negative");

        return new BigDecimalGenerator(randomValues(), range, scale);
    }

    public static Generator<String> string = string(30);

    public static Generator<String> string(Integer max) {
        return new StringGenerator(max);
    }

    public static Generator<String> string(Integer max, String charset) {
        return new StringGenerator(max, charset);
    }

    public static Generator<String> string(Range<Integer> range, String charset) {
        return new StringGenerator(range, charset);
    }

    public static Generator<String> string(Integer max, CharacterFilter filter) {
        return new StringGenerator(max, filter);
    }

    public static Generator<String> string(Range<Integer> range, CharacterFilter filter) {
        return new StringGenerator(range, filter);
    }

    public static Generator<String> string(Integer max, CharacterSetFilter filter) {
        return new StringGenerator(max, filter.getFilter());
    }

    public static Generator<String> string(Integer max, CharacterFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(max, new CharacterSetGenerator(filter, ranges));
    }

    public static Generator<String> string(Integer max, CharacterSetFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(max, new CharacterSetGenerator(filter.getFilter(), ranges));
    }

    public static Generator<String> string(Range<Integer> range, CharacterSetFilter filter) {
        return new StringGenerator(range, filter.getFilter());
    }

    public static Generator<String> string(Range<Integer> range, CharacterSetFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(range, new CharacterSetGenerator(filter.getFilter(), ranges));
    }

    public static Generator<String> string(Range<Integer> range, CharacterFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(range, new CharacterSetGenerator(filter, ranges));
    }

    public static Generator<String> string(Integer max, Range<Integer>... ranges) {
        return new StringGenerator(max, ranges);
    }

    public static Generator<String> string(Integer max, CharacterSetRange... characterSetRanges) {
        return new StringGenerator(max, characterSetRanges);
    }

    public static Generator<String> string(Integer max, char[] chars) {
        return new StringGenerator(max, chars);
    }

    public static Generator<String> string(Range<Integer> range, char[] chars) {
        return new StringGenerator(range, chars);
    }

    public static Generator<String> string(Range<Integer> range) {
        return new StringGenerator(range);
    }

    public static Generator<String> string(Range<Integer> stringLength, Range<Integer>... charSetRanges) {
        return new StringGenerator(stringLength, charSetRanges);
    }

    public static Generator<String> string(Range<Integer> stringLength, CharacterSetRange... charSetRanges) {
        return new StringGenerator(stringLength, charSetRanges);
    }
    public static <T extends Enum<T>> Generator<T> value(final Class<T> classOfEnumT) {
        ensure(isNotNull(classOfEnumT), "enum class cannot be null");

        final T[] enumConstants = classOfEnumT.getEnumConstants();
        ensure(enumConstants.length > 0, format("enum %s does not have any constants", classOfEnumT));

        return value(enumConstants);
    }

    public static <T> Generator<T> value(final T first, T... arrayOfTs) {
        ensure(isNotNull(arrayOfTs), "varargs array of values cannot be null");

        final List<T> listOfTs = new ArrayList<T>(asList(arrayOfTs));
        listOfTs.add(0, first);
        return value(listOfTs);
    }

    public static <T> Generator<T> value(final T[] arrayOfTs) {
        ensure(isNotNull(arrayOfTs), "array of values cannot be null");
        ensure(arrayOfTs.length > 0, "array of values cannot be empty");

        return value(asList(arrayOfTs));
    }

    public static <T> Generator<T> value(final Iterable<T> iterableOfT) {
        ensure(isNotNull(iterableOfT), "values cannot be null");
        ensure(iterableOfT.iterator().hasNext(), "there must be at-least one value");

        return new ValueGenerator<T>(randomValues(), iterableOfT);
    }

    public static Generator<URI> uri() {return new UriGenerator();}

    public static Generator<String> domainSuffix() {
        return new SuffixGenerator();
    }

    public static Generator<String> emailAddress() {
        return new EmailAddressGenerator();
    }

    public static Generator<String> domain() {
        return new DomainGenerator();
    }

    public static Generator<String> domain(Range<Integer> range) {
        return new DomainGenerator(range);
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT) {
        return list(generatorOfT, 15);
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT, final int size) {
        return list(generatorOfT, fixed(size));
    }

    public static <T> Generator<List<T>> list(final Generator<? extends T> generatorOfT,
                                              final Range<Integer> sizeRange) {
        ensure(isNotNull(generatorOfT), "generator cannot be null");
        ensure(isNotNull(sizeRange), "size range cannot be null");

        return new ListGenerator<T>(randomValues(), generatorOfT, sizeRange);
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT,
                                           final Generator<? extends T> generatorOfT) {
        return array(classOfT, generatorOfT, 15);
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT,
                                           final Generator<? extends T> generatorOfT,
                                           final int size) {
        return array(classOfT, generatorOfT, fixed(size));
    }

    public static <T> Generator<T[]> array(final Class<? extends T> classOfT,
                                           final Generator<? extends T> generatorOfT,
                                           final Range<Integer> sizeRange) {
        ensure(isNotNull(classOfT), "type of array elements cannot be null");
        ensure(isNotNull(generatorOfT), "generator cannot be null");
        ensure(isNotNull(sizeRange), "size range cannot be null");

        return new ArrayGenerator<T>(randomValues(), classOfT, generatorOfT, sizeRange);
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT) {
        return set(generatorOfT, 15);
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT, final int size) {
        return set(generatorOfT, fixed(size));
    }

    public static <T> Generator<Set<T>> set(final Generator<? extends T> generatorOfT,
                                            final Range<Integer> sizeRange) {
        ensure(isNotNull(generatorOfT), "generator cannot be null");
        ensure(isNotNull(sizeRange), "size range cannot be null");

        return new SetGenerator<T>(randomValues(), generatorOfT, sizeRange);
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK,
                                                  final Generator<? extends V> generatorOfV) {
        return map(generatorOfK, generatorOfV, 15);
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK,
                                                  final Generator<? extends V> generatorOfV,
                                                  final int size) {
        return map(generatorOfK, generatorOfV, fixed(size));
    }

    public static <K, V> Generator<Map<K, V>> map(final Generator<? extends K> generatorOfK,
                                                  final Generator<? extends V> generatorOfV,
                                                  final Range<Integer> sizeRange) {
        ensure(isNotNull(generatorOfK), "key generator cannot be null");
        ensure(isNotNull(generatorOfV), "value generator cannot be null");
        ensure(isNotNull(sizeRange), "size range cannot be null");

        return new MapGenerator<K, V>(randomValues(), generatorOfK, generatorOfV, sizeRange);
    }

    public static Generator<String> niNumber() {
        return new NINumberGenerator();
    }

    public static Generator<String> postcode() {
        return new PostcodeGenerator();
    }

    public static Generator<Boolean> percentageChanceOf(int chance) {
        return new PercentageChanceGenerator(chance);
    }

    public static Generator<Currency> currency() {
        return new CurrencyGenerator();
    }

    public static Generator<Locale> locale() {
        return new LocaleGenerator();
    }

    public static Generator<String> iso3Country() {
        return new Iso3CountryGenerator();
    }
}
