/*
 * MIT License
 *
 * Copyright (c) 2023-2024 Michael Cowan
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

import io.blt.util.Obj;

/**
 * Represents a supplier of results that may throw.
 * <p>Like {@code Supplier} but able to throw an exception</p>
 *
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of {@code Throwable} that may be thrown by this supplier
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    /**
     * Returns a result.
     *
     * @return a result
     * @throws E {@code Throwable} that may be thrown
     * @see java.util.function.Supplier#get()
     */
    T get() throws E;

    /**
     * Returns the result of {@link ThrowingSupplier#get()} if no exception is thrown, else returns {@code value}.
     *
     * @param value returned if an exception is thrown when calling {@link ThrowingSupplier#get()}
     * @return result of {@link ThrowingSupplier#get()} if no exception is thrown, else {@code value}
     * @deprecated
     * The use of a default method here is clearly wrong and should never have been released.
     * <p>Use {@link Obj#orElseOnException(ThrowingSupplier, Object)} instead.</p>
     */
    @Deprecated(since = "1.0.6", forRemoval = true)
    default T orOnException(T value) {
        try {
            return get();
        } catch (Throwable e) {
            return value;
        }
    }
}
