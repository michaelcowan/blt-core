/*
 * MIT License
 *
 * Copyright (c) 2022 Michael Cowan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.blt.util.stream;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;

/**
 * Implementations of {@link Collector} that reduce to exactly one or zero elements.
 * e.g.,
 * <pre>{@code
 * var maybeActiveStep = sequentialSteps.stream()
 *         .filter(SequentialStep::isActive)
 *         .collect(SingletonCollectors.toOptional());
 * }</pre>
 * <p>
 * If more than one element is present, then {@code IllegalArgumentException} is thrown.
 */
public final class SingletonCollectors {

    private SingletonCollectors() {
    }

    /**
     * Returns a {@code Collector} that accumulates the only element, if any, into an {@code Optional}.
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} that accumulates the only element, if any, into an {@code Optional}.
     * @throws IllegalArgumentException if more than one element is present
     */
    public static <T> SingletonCollector<T, Optional<T>> toOptional() {
        return new SingletonCollector<>(Container::getOptional);
    }

    private static class SingletonCollector<T, R> implements Collector<T, Container<T>, R> {

        private final Function<Container<T>, R> finisher;

        public SingletonCollector(Function<Container<T>, R> finisher) {
            this.finisher = finisher;
        }

        @Override
        public Supplier<Container<T>> supplier() {
            return Container::new;
        }

        @Override
        public BiConsumer<Container<T>, T> accumulator() {
            return Container::setValue;
        }

        @Override
        public BinaryOperator<Container<T>> combiner() {
            return null; // Only used in a parallel stream, which this collector doesn't support
        }

        @Override
        public Function<Container<T>, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return emptySet();
        }
    }

    private static final class Container<T> {

        private T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            if (nonNull(this.value)) {
                throw new IllegalStateException("Expected stream to contain exactly 0 or 1 elements");
            }
            this.value = value;
        }

        public Optional<T> getOptional() {
            return Optional.ofNullable(getValue());
        }
    }

}
