package com.fyodor.generators;

import com.fyodor.generators.characters.CharacterFilter;
import com.fyodor.generators.characters.CharacterSetGenerator;
import com.fyodor.range.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.fyodor.internal.Preconditions.checkArgument;
import static com.fyodor.internal.Preconditions.checkArgumentIsNotNull;
import static com.fyodor.random.RandomValuesProvider.randomValues;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class RDG {

    public static Generator<Integer> integer = integer(Integer.MAX_VALUE);

    public static Generator<Integer> integer(Integer max) {
        return new IntegerGenerator(max);
    }

    public static Generator<Integer> integer(Range<Integer> range) {
        return new IntegerGenerator(range);
    }

    public static Generator<String> string = string(30);

    public static Generator<String> string(Integer max) {
        return new StringGenerator(max);
    }

    public static Generator<String> string(Integer max, CharacterFilter filter) {
        return new StringGenerator(max, filter);
    }

    public static Generator<String> string(Integer max, Range<Integer> range) {
        return new StringGenerator(max, range);
    }

    public static Generator<String> string(Integer max, CharacterSetGenerator generator) {
        return new StringGenerator(max, generator);
    }

    public static Generator<String> string(Integer max, Collection<Character> chars) {
        return new StringGenerator(max, chars);
    }

    public static Generator<String> string(Integer max, Character[] chars) {
        return new StringGenerator(max, chars);
    }

    public static <T extends Enum<T>> Generator<T> value(final Class<T> classOfEnumT) {
        checkArgumentIsNotNull(classOfEnumT, "enum class cannot be null");

        final T[] enumConstants = classOfEnumT.getEnumConstants();
        checkArgument(enumConstants.length > 0, format("enum %s does not have any constants", classOfEnumT));

        return value(enumConstants);
    }

    public static <T> Generator<T> value(final T first, T... arrayOfTs) {
        checkArgumentIsNotNull(arrayOfTs, "varargs array of values cannot be null");

        final List<T> listOfTs = new ArrayList<T>(asList(arrayOfTs));
        listOfTs.add(0, first);
        return value(listOfTs);
    }

    public static <T> Generator<T> value(final T[] arrayOfTs) {
        checkArgumentIsNotNull(arrayOfTs, "array of values cannot be null");
        checkArgument(arrayOfTs.length > 0, "array of values cannot be empty");

        return value(asList(arrayOfTs));
    }

    public static <T> Generator<T> value(final Iterable<T> iterableOfT) {
        checkArgumentIsNotNull(iterableOfT, "values cannot be null");
        checkArgument(iterableOfT.iterator().hasNext(), "there must be at-least one value");

        return new ValueGenerator<T>(randomValues(), iterableOfT);
    }
}
