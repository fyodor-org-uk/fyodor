package com.fyodor.generators;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

final class Sampler<T> {

    private final Generator<? extends T> generatorOfT;

    private Sampler(final Generator<? extends T> generatorOfT) {
        this.generatorOfT = generatorOfT;
    }

    public Sample<T> sample(final int sampleSize) {
        final List<T> samples = new LinkedList<T>();
        for (int i = 0; i < sampleSize; i++) {
            samples.add(generatorOfT.next());
        }
        return new Sample(samples);
    }

    public static <T> Sampler<T> from(final Generator<? extends T> generatorOfT) {
        return new Sampler<T>(generatorOfT);
    }

    static final class Sample<T> implements Iterable<T> {

        private final List<T> listOfT;

        Sample(final List<T> listOfT) {
            this.listOfT = listOfT;
        }

        Set<T> unique() {
            return newHashSet(listOfT);
        }

        @Override
        public Iterator<T> iterator() {
            return listOfT.iterator();
        }
    }
}
