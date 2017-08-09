package uk.org.fyodor;

import uk.org.fyodor.generators.Generator;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public final class Sampler<T> {

    private final Generator<? extends T> generatorOfT;

    private Sampler(final Generator<? extends T> generatorOfT) {
        this.generatorOfT = generatorOfT;
    }

    public Sample<T> sample(final int sampleSize) {
        final List<T> samples = new LinkedList<>();
        for (int i = 0; i < sampleSize; i++) {
            samples.add(generatorOfT.next());
        }
        return new Sample<>(samples);
    }

    public static <T> Sampler<T> from(final Generator<? extends T> generatorOfT) {
        return new Sampler<>(generatorOfT);
    }

    public static <T extends Comparable<T>> T smallest(final Sample<? extends T> sampleOfT) {
        T smallest = null;
        for (final T t : sampleOfT) {
            if (smallest == null || smallest.compareTo(t) > 0) {
                smallest = t;
            }
        }
        return smallest;
    }

    public static <T extends Comparable<T>> T largest(final Sample<? extends T> sampleOfT) {
        T largest = null;
        for (final T t : sampleOfT) {
            if (largest == null || largest.compareTo(t) < 0) {
                largest = t;
            }
        }
        return largest;
    }

    public static final class Sample<T> implements Iterable<T> {

        private final List<T> listOfT;

        public Sample(final List<T> listOfT) {
            if (listOfT == null) {
                throw new IllegalArgumentException("sample list cannot be null");
            }
            this.listOfT = listOfT;
        }

        public Set<T> unique() {
            return new HashSet<>(listOfT);
        }

        public <R> Sample<R> map(final Function<T, R> transform) {
            return new Sample<>(listOfT.stream().map(transform).collect(toList()));
        }

        public List<T> asList() {
            return listOfT;
        }

        @Override
        public Iterator<T> iterator() {
            return listOfT.iterator();
        }

        @Override
        public int hashCode() {
            return listOfT.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Sample sample = (Sample) o;

            return listOfT.equals(sample.listOfT);
        }

        @Override
        public String toString() {
            return "Sample {" + listOfT + "}";
        }
    }
}
