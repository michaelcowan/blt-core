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

package io.blt.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Static utility methods for operating on {@code Object}.
 */
public final class Obj {

    private Obj() {
        throw new IllegalAccessError("Utility class should be accessed statically and never constructed");
    }

    /**
     * Passes the {@code instance} to the {@code consumer}, then returns the {@code instance}.
     * e.g.
     * <pre>{@code
     * var user = Obj.tap(new User(), u -> {
     *     u.setName("Greg");
     *     u.setAge(15);
     * });
     * }</pre>
     *
     * @param instance instance to consume and return
     * @param consumer operation to perform on {@code instance}
     * @param <T>      type of {@code instance}
     * @return {@code instance} after accepting side effects via {@code consumer}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T tap(T instance, Consumer<? extends T> consumer) {
        // Unchecked cast is required to allow for <? extends T> which is a workaround to an ambiguity issue
        // https://stackoverflow.com/a/48388275
        ((Consumer<T>) consumer).accept(instance);
        return instance;
    }

    /**
     * Calls the {@code supplier} to retrieve an instance which is passed to the {@code consumer} then returned.
     * e.g.
     * <pre>{@code
     * var user = Obj.tap(User::new, u -> {
     *     u.setName("Greg");
     *     u.setAge(15);
     * });
     * }</pre>
     *
     * @param supplier Supplies an instance to consume and return
     * @param consumer Operation to perform on supplied instance
     * @param <T>      type of instance
     * @return Supplied instance after applying side effects via {@code consumer}.
     */
    public static <T> T tap(Supplier<T> supplier, Consumer<? extends T> consumer) {
        return tap(supplier.get(), consumer);
    }

}
