package uk.org.fyodor.jodatime.generators;

import uk.org.fyodor.generators.Generator;

import java.util.*;

final class Sampler<T> {

    private final Generator<? extends T> generatorOfT;

    private Sampler(final Generator<? extends T> generatorOfT) {
        this.generatorOfT = generatorOfT;
    }

    Sample<T> sample(final int sampleSize) {
        final List<T> samples = new LinkedList<T>();
        for (int i = 0; i < sampleSize; i++) {
            samples.add(generatorOfT.next());
        }
        return new Sample<T>(samples);
    }

    static <T> Sampler<T> from(final Generator<? extends T> generatorOfT) {
        return new Sampler<T>(generatorOfT);
    }

    static final class Sample<T> implements Iterable<T> {

        private final List<T> listOfT;

        Sample(final List<T> listOfT) {
            if (listOfT == null) {
                throw new IllegalArgumentException("sample list cannot be null");
            }
            this.listOfT = listOfT;
        }

        public Set<T> unique() {
            return new HashSet<T>(listOfT);
        }

        @Override
        public Iterator<T> iterator() {
            return listOfT.iterator();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Sample sample = (Sample) o;

            return listOfT.equals(sample.listOfT);
        }

        @Override
        public int hashCode() {
            return listOfT.hashCode();
        }

        @Override
        public String toString() {
            return "Sample {" + listOfT + "}";
        }
    }
}
