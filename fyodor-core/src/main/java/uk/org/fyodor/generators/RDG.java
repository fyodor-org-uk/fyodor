package uk.org.fyodor.generators;

import uk.org.fyodor.generators.characters.CharacterFilter;
import uk.org.fyodor.generators.characters.CharacterSetFilter;
import uk.org.fyodor.generators.characters.CharacterSetGenerator;
import uk.org.fyodor.generators.characters.CharacterSetRange;
import uk.org.fyodor.generators.collections.ArrayGenerator;
import uk.org.fyodor.generators.collections.ListGenerator;
import uk.org.fyodor.generators.collections.MapGenerator;
import uk.org.fyodor.generators.collections.SetGenerator;
import uk.org.fyodor.generators.time.LocalDateGenerator;
import uk.org.fyodor.generators.time.LocalDateRange;
import uk.org.fyodor.generators.time.LocalTimeGenerator;
import uk.org.fyodor.generators.time.LocalTimeRange;
import uk.org.fyodor.range.Range;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static uk.org.fyodor.generators.Validations.*;
import static uk.org.fyodor.random.RandomSourceProvider.sourceOfRandomness;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;

public class RDG {

    private static final Generator<String> STRING_GENERATOR = string(30);
    private static final Generator<Integer> INTEGER_GENERATOR = integer(closed(Integer.MIN_VALUE, Integer.MAX_VALUE));
    private static final BooleanGenerator BOOLEAN_GENERATOR = new BooleanGenerator(sourceOfRandomness());
    private static final Generator<Long> LONG_GENERATOR = longVal(closed(Long.MIN_VALUE, Long.MAX_VALUE));
    private static final Generator<Double> DOUBLE_GENERATOR = doubleVal(closed(Double.MIN_VALUE, Double.MAX_VALUE));
    private static final Generator<BigDecimal> BIG_DECIMAL_GENERATOR = bigDecimal(closed(BigDecimal.valueOf(Double.MIN_VALUE), BigDecimal.valueOf(Double.MAX_VALUE)));
    private static final DomainGenerator DOMAIN_GENERATOR = new DomainGenerator();
    private static final SuffixGenerator SUFFIX_GENERATOR = new SuffixGenerator();
    private static final UriGenerator URI_GENERATOR = new UriGenerator();
    private static final EmailAddressGenerator EMAIL_ADDRESS_GENERATOR = new EmailAddressGenerator();
    private static final NINumberGenerator NI_NUMBER_GENERATOR = new NINumberGenerator();
    private static final PostcodeGenerator POSTCODE_GENERATOR = new PostcodeGenerator();
    private static final CurrencyGenerator CURRENCY_GENERATOR = new CurrencyGenerator();
    private static final LocaleGenerator LOCALE_GENERATOR = new LocaleGenerator();
    private static final Iso3CountryGenerator ISO_3_COUNTRY_GENERATOR = new Iso3CountryGenerator();

    public static Generator<LocalTime> localTime() {
        return localTime(LocalTimeRange.all());
    }

    public static Generator<LocalTime> localTime(final LocalTimeRange range) {
        return localTime((Range<LocalTime>) range);
    }

    public static Generator<LocalTime> localTime(final Range<LocalTime> range) {
        ensure(isNotNull(range), "time range cannot be null");

        return new LocalTimeGenerator(sourceOfRandomness(), range);
    }

    public static Generator<LocalDate> localDate() {
        return localDate(LocalDateRange.all());
    }

    public static Generator<LocalDate> localDate(final LocalDateRange range) {
        return localDate((Range<LocalDate>) range);
    }

    public static Generator<LocalDate> localDate(final Range<LocalDate> range) {
        ensure(isNotNull(range), "date range cannot be null");

        return new LocalDateGenerator(sourceOfRandomness(), range);
    }

    public static Generator<Boolean> bool() {
        return BOOLEAN_GENERATOR;
    }

    public static Generator<Integer> integer() {
        return INTEGER_GENERATOR;
    }

    public static Generator<Integer> integer(final int maximum) {
        ensure(isNotNegative(maximum), "maximum cannot be negative");

        return integer(closed(0, maximum));
    }

    public static Generator<Integer> integer(final Range<Integer> range) {
        ensure(isNotNull(range), "range cannot be null");

        return new IntegerGenerator(sourceOfRandomness(), range);
    }

    public static Generator<Long> longVal() {
        return LONG_GENERATOR;
    }

    public static Generator<Long> longVal(final long maximum) {
        ensure(isNotNegative(maximum), "maximum cannot be negative");

        return longVal(closed(0L, maximum));
    }

    public static Generator<Long> longVal(final Range<Long> range) {
        ensure(isNotNull(range), "range cannot be null");

        return new LongGenerator(sourceOfRandomness(), range);
    }

    public static Generator<Double> doubleVal() {
        return DOUBLE_GENERATOR;
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

        return new DoubleGenerator(sourceOfRandomness(), range);
    }

    public static Generator<BigDecimal> bigDecimal() {
        return BIG_DECIMAL_GENERATOR;
    }

    public static Generator<BigDecimal> bigDecimal(final double val) {
        return bigDecimal(closed(BigDecimal.ZERO, BigDecimal.valueOf(val)));
    }

    public static Generator<BigDecimal> bigDecimal(final long val) {
        return bigDecimal(closed(BigDecimal.ZERO, BigDecimal.valueOf(val)));
    }

    public static Generator<BigDecimal> bigDecimal(final BigDecimal val) {
        return bigDecimal(closed(BigDecimal.ZERO, val));
    }

    public static Generator<BigDecimal> bigDecimal(final Range<BigDecimal> range) {
        return bigDecimal(range, 2);
    }

    public static Generator<BigDecimal> bigDecimal(final Range<BigDecimal> range, final int scale) {
        ensure(isNotNull(range), "range cannot be null");
        ensure(isNotNegative(scale), "scale cannot be negative");

        return new BigDecimalGenerator(sourceOfRandomness(), range, scale);
    }

    public static Generator<String> string() {
        return STRING_GENERATOR;
    }

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

    @SafeVarargs
    public static Generator<String> string(Integer max, CharacterFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(max, new CharacterSetGenerator(filter, ranges));
    }

    @SafeVarargs
    public static Generator<String> string(Integer max, CharacterSetFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(max, new CharacterSetGenerator(filter.getFilter(), ranges));
    }

    public static Generator<String> string(Range<Integer> range, CharacterSetFilter filter) {
        return new StringGenerator(range, filter.getFilter());
    }

    @SafeVarargs
    public static Generator<String> string(Range<Integer> range, CharacterSetFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(range, new CharacterSetGenerator(filter.getFilter(), ranges));
    }

    @SafeVarargs
    public static Generator<String> string(Range<Integer> range, CharacterFilter filter, Range<Integer>... ranges) {
        return new StringGenerator(range, new CharacterSetGenerator(filter, ranges));
    }

    @SafeVarargs
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

    @SafeVarargs
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

    @SafeVarargs
    public static <T> Generator<T> value(final T first, T... arrayOfTs) {
        ensure(isNotNull(arrayOfTs), "varargs array of values cannot be null");

        final List<T> listOfTs = new ArrayList<>(asList(arrayOfTs));
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

        return new ValueGenerator<>(sourceOfRandomness(), iterableOfT);
    }

    public static Generator<URI> uri() {
        return URI_GENERATOR;
    }

    public static Generator<String> domainSuffix() {
        return SUFFIX_GENERATOR;
    }

    public static Generator<String> emailAddress() {
        return EMAIL_ADDRESS_GENERATOR;
    }

    public static Generator<String> domain() {
        return DOMAIN_GENERATOR;
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

        return new ListGenerator<>(sourceOfRandomness(), generatorOfT, sizeRange);
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

        return new ArrayGenerator<>(sourceOfRandomness(), classOfT, generatorOfT, sizeRange);
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

        return new SetGenerator<>(sourceOfRandomness(), generatorOfT, sizeRange);
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

        return new MapGenerator<>(sourceOfRandomness(), generatorOfK, generatorOfV, sizeRange);
    }

    public static Generator<String> niNumber() {
        return NI_NUMBER_GENERATOR;
    }

    public static Generator<String> postcode() {
        return POSTCODE_GENERATOR;
    }

    public static Generator<Boolean> percentageChanceOf(int chance) {
        return new PercentageChanceGenerator(chance);
    }

    public static Generator<Currency> currency() {
        return CURRENCY_GENERATOR;
    }

    public static Generator<Locale> locale() {
        return LOCALE_GENERATOR;
    }

    public static Generator<String> iso3Country() {
        return ISO_3_COUNTRY_GENERATOR;
    }

}
