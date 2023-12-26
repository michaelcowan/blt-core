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

import io.blt.util.functional.ThrowingConsumer;
import io.blt.util.functional.ThrowingSupplier;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

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
     * var user = Obj.poke(new User(), u -> {
     *     u.setName("Greg");
     *     u.setAge(15);
     * });
     * }</pre>
     * <p>
     * Optionally, the {@code consumer} may throw which will bubble up.
     * </p>
     *
     * @param instance instance to consume and return
     * @param consumer operation to perform on {@code instance}
     * @param <T>      type of {@code instance}
     * @param <E>      type of {@code consumer} throwable
     * @return {@code instance} after accepting side effects via {@code consumer}.
     */
    public static <T, E extends Throwable> T poke(T instance, ThrowingConsumer<T, E> consumer) throws E {
        consumer.accept(instance);
        return instance;
    }

    /**
     * Calls the {@code supplier} to retrieve an instance which is mutated by the {@code consumer} then returned.
     * e.g.
     * <pre>{@code
     * var user = Obj.tap(User::new, u -> {
     *     u.setName("Greg");
     *     u.setAge(15);
     * });
     * }</pre>
     * <p>
     * Optionally, the {@code consumer} may throw which will bubble up.
     * </p>
     *
     * @param supplier supplies an instance to consume and return
     * @param consumer operation to perform on supplied instance
     * @param <T>      type of instance
     * @param <E>      type of {@code consumer} throwable
     * @return Supplied instance after applying side effects via {@code consumer}.
     */
    public static <T, E extends Throwable> T tap(Supplier<T> supplier, ThrowingConsumer<T, E> consumer) throws E {
        return poke(supplier.get(), consumer);
    }

    /**
     * Returns {@code value} if non-null, else invokes and returns the result of {@code supplier}.
     * <p>
     * Optionally, the {@code supplier} may throw which will bubble up.
     * </p>
     * e.g.
     * <pre>{@code
     * private URL homepageOrDefault(URL homepage) throws MalformedURLException {
     *     return Obj.orElseGet(homepage, () -> new URL("https://google.com"));
     * }
     * }</pre>
     *
     * @param value    returned if non-null
     * @param supplier called and returned if {@code value} is null
     * @param <T>      type of the returned value
     * @param <E>      type of {@code supplier} throwable
     * @return {@code value} if non-null, else the result of {@code supplier}
     * @throws E {@code Throwable} that may be thrown if the {@code supplier} is invoked
     */
    public static <T, E extends Throwable> T orElseGet(T value, ThrowingSupplier<T, E> supplier) throws E {
        return nonNull(value) ? value : supplier.get();
    }

    /**
     * Invokes and returns the result of {@code supplier} if no exception is thrown, else returns {@code defaultValue}.
     * e.g.
     * <pre>{@code
     * private InputStream openFileOrResource(String name) {
     *     return Obj.orElseOnException(
     *             () -> new FileInputStream(name),
     *             getClass().getResourceAsStream(name));
     * }
     * }</pre>
     *
     * @param supplier     called and returned if no exception is thrown
     * @param defaultValue returned if an exception is thrown when calling {@code supplier}
     * @param <T>          type of the returned value
     * @param <E>          type of {@code supplier} throwable
     * @return result of {@code supplier} if no exception is thrown, else {@code defaultValue}
     */
    public static <T, E extends Throwable> T orElseOnException(ThrowingSupplier<T, E> supplier, T defaultValue) {
        return supplier.orOnException(defaultValue);
    }

    /**
     * Throws the specified {@code throwable} if the given {@code value} satisfies the provided {@code predicate}.
     * For convenience, {@code value} is returned.
     * e.g.
     * <pre>{@code
     * public Map<String, String> loadProperties() {
     *     return throwIf(Properties.loadFromJson(FILENAME), Map::isEmpty,
     *             () -> new IllegalStateException("Properties must not be empty"));
     * }
     * }</pre>
     *
     * @param value     the value to be checked
     * @param predicate the predicate to be evaluated
     * @param throwable the supplier for the throwable to be thrown
     * @param <T>       the type of the value
     * @param <E>       the type of the throwable
     * @return {@code value}
     * @throws E if the given {@code value} satisfies the provided {@code predicate}
     * @see Obj#throwUnless(Object, Predicate, Supplier)
     */
    public static <T, E extends Throwable> T throwIf(
            T value, Predicate<? super T> predicate, Supplier<? extends E> throwable) throws E {
        if (predicate.test(value)) {
            throw throwable.get();
        }
        return value;
    }

    /**
     * Throws the specified {@code throwable} if the given {@code value} does not satisfy the provided {@code predicate}.
     * For convenience, {@code value} is returned.
     * e.g.
     * <pre>{@code
     * throwUnless(properties, p -> p.containsKey("host"),
     *         () -> new IllegalStateException("Properties must contain a host"));
     * }</pre>
     *
     * @param value     the value to be checked
     * @param predicate the predicate to be evaluated
     * @param throwable the supplier for the throwable to be thrown
     * @param <T>       the type of the value
     * @param <E>       the type of the throwable
     * @return {@code value}
     * @throws E if the given {@code value} does not satisfy the provided {@code predicate}
     * @see Obj#throwIf(Object, Predicate, Supplier)
     */
    public static <T, E extends Throwable> T throwUnless(
            T value, Predicate<? super T> predicate, Supplier<? extends E> throwable) throws E {
        return throwIf(value, predicate.negate(), throwable);
    }

    /**
     * Returns a new instance of the same type as the input object if possible; otherwise, returns empty.
     * Supports only instances of concrete types that have a public zero argument constructor.
     * e.g.
     * <pre>{@code
     * public <K, V> Map<K, V> mapOfSameTypeOrHashMap(Map<K, V> map) {
     *     return Obj.newInstanceOf(map).orElse(new HashMap<>());
     * }
     * }</pre>
     *
     * @param obj object to try and create a new instance of
     * @param <T> type of {@code obj}
     * @return a new instance of the same type as {@code obj} or empty
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> newInstanceOf(T obj) {
        try {
            return Optional.of((T) obj.getClass().getConstructor().newInstance());
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException e) {
            return Optional.empty();
        }
    }

}
