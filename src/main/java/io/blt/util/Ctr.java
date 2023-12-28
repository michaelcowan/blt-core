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

import io.blt.util.functional.ThrowingFunction;
import io.blt.util.functional.ThrowingSupplier;
import java.util.HashMap;
import java.util.Map;

import static io.blt.util.Obj.newInstanceOf;
import static java.util.Objects.nonNull;

/**
 * Static utility methods for operating on implementations of {@code Collection} and {@code Map} i.e. Containers.
 * <p>
 * For methods that return a modification of a passed container, the result will be of the same type if possible.
 * This is accomplished using {@link Obj#newInstanceOf(Object)} and its limitations apply.
 * </p>
 */
public final class Ctr {

    private Ctr() {
        throw new IllegalAccessError("Utility class should be accessed statically and never constructed");
    }

    /**
     * Returns a new {@link Map} containing the entries of {@code source} with {@code transform} applied to the values.
     * <p>
     * If possible, the result is of the same type as the passed {@code source} map.
     * </p>
     * e.g.
     * <pre>{@code
     * var scores = Map.of("Louis", 95, "Greg", 92, "Mike", 71, "Phil", 86);
     * var grades = Ctr.transformValues(scores, score -> {
     *     if (score >= 90) {
     *         return "A";
     *     } else if (score >= 80) {
     *         return "B";
     *     } else if (score >= 70) {
     *         return "C";
     *     } else if (score >= 60) {
     *         return "D";
     *     } else {
     *         return "F";
     *     }
     * });
     * // grades = Map.of("Louis", "A", "Greg", "A", "Mike", "C", "Phil", "B")
     * }</pre>
     *
     * @param source    {@link Map} whose values should be transformed
     * @param transform value transformation function
     * @param <K>       {@code map} key type
     * @param <V>       {@code map} value type
     * @param <R>       returned map value type
     * @param <E>       type of {@code transform} throwable
     * @return a new {@link Map} containing the entries of {@code source} with {@code transform} applied to the values
     * @see Obj#newInstanceOf(Object)
     */
    @SuppressWarnings("unchecked")
    public static <K, V, R, E extends Throwable> Map<K, R> transformValues(
            Map<K, V> source, ThrowingFunction<? super V, R, E> transform) throws E {
        var result = newInstanceOf((Map<K, R>) source).orElse(new DefaultMap<>());

        for (var entry : source.entrySet()) {
            result.put(entry.getKey(), transform.apply(entry.getValue()));
        }

        return result instanceof DefaultMap ? Map.copyOf(result) : result;
    }

    /**
     * For the specified {@code map}, if there is no value for the specified {@code key} then {@code compute} will be
     * called and the result entered into the map. If a value is present, then it is returned.
     * e.g.
     * <pre>{@code
     * private final Map<URL, String> cache = new HashMap<>();
     *
     * public String fetch(URL url) throws IOException {
     *     return Ctr.computeIfAbsent(cache, url, this::get);
     * }
     *
     * private String get(URL url) throws IOException {
     *     try (var stream = url.openStream()) {
     *         return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
     *     }
     * }
     * }</pre>
     *
     * <p>If {@code compute} returns {@code null}, then the map is not modified and {@code null} is returned.
     *
     * <p>If {@code compute} throws, then map is not modified and the exception will bubble up.
     *
     * @param map     {@link Map} whose value is returned and may be computed
     * @param key     key with which the specified value is to be associated
     * @param compute the computation function to use if the value is absent
     * @param <K>     {@code map} key type
     * @param <V>     {@code map} value type
     * @param <E>     type of {@code compute} throwable
     * @return the existing or computed value associated with the key, or null if the computed value is null
     * @throws E if an exception is thrown by the computation function
     * @implNote The implementation is equivalent to the following:
     * <pre> {@code
     * if (map.get(key) == null) {
     *     V value = compute.apply(key);
     *     if (value != null) {
     *         map.put(key, value);
     *     }
     * }
     * return map.get(key);
     * }</pre>
     * @see Ctr#computeIfAbsent(Map, Object, ThrowingSupplier)
     */
    public static <K, V, E extends Throwable> V computeIfAbsent(
            Map<K, V> map, K key, ThrowingFunction<? super K, ? extends V, E> compute) throws E {
        return Obj.orElseGet(map.get(key), () -> {
            var value = compute.apply(key);
            if (nonNull(value)) {
                map.put(key, value);
            }
            return value;
        });
    }

    /**
     * For the specified {@code map}, if there is no value for the specified {@code key} then {@code compute} will be
     * called and the result entered into the map. If a value is present, then it is returned.
     * e.g.
     * <pre>{@code
     * private final Map<URL, String> cache = new HashMap<>();
     *
     * public String fetch(URL url) throws IOException {
     *     return Ctr.computeIfAbsent(cache, url, () -> {
     *         try (var stream = url.openStream()) {
     *             return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
     *         }
     *     });
     * }
     * }</pre>
     *
     * <p>If {@code compute} returns {@code null}, then the map is not modified and {@code null} is returned.
     *
     * <p>If {@code compute} throws, then map is not modified and the exception will bubble up.
     *
     * @param map     {@link Map} whose value is returned and may be computed
     * @param key     key with which the specified value is to be associated
     * @param compute the computation supplier to use if the value is absent
     * @param <K>     {@code map} key type
     * @param <V>     {@code map} value type
     * @param <E>     type of {@code compute} throwable
     * @return the existing or computed value associated with the key, or null if the computed value is null
     * @throws E if an exception is thrown by the computation supplier
     * @implNote The implementation is equivalent to the following:
     * <pre> {@code
     * if (map.get(key) == null) {
     *     V value = compute.apply(key);
     *     if (value != null) {
     *         map.put(key, value);
     *     }
     * }
     * return map.get(key);
     * }</pre>
     * @see Ctr#computeIfAbsent(Map, Object, ThrowingFunction)
     */
    public static <K, V, E extends Throwable> V computeIfAbsent(
            Map<K, V> map, K key, ThrowingSupplier<? extends V, E> compute) throws E {
        return computeIfAbsent(map, key, unused -> compute.get());
    }

    private static final class DefaultMap<K, V> extends HashMap<K, V> {}

}
