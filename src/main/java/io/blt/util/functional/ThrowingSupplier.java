/*
 * MIT License
 *
 * Copyright (c) 2023 Michael Cowan
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

package io.blt.util.functional;

/**
 * Represents a supplier of results that may throw.
 *
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of {@code Throwable} that may be thrown by this supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    /**
     * Returns a result.
     *
     * @return a result
     * @throws E {@code Throwable} that may be thrown
     */
    T get() throws E;

    /**
     * Returns the result of {@link ThrowingSupplier#get()} if no exception is thrown, else returns {@code value}.
     *
     * @param value returned if an exception is thrown when calling {@link ThrowingSupplier#get()}
     * @return result of {@link ThrowingSupplier#get()} if no exception is thrown, else {@code value}
     */
    default T orOnException(T value) {
        try {
            return get();
        } catch (Throwable e) {
            return value;
        }
    }
}
