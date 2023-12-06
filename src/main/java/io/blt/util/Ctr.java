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
import java.util.HashMap;
import java.util.Map;

import static io.blt.util.Obj.newInstanceOf;

/**
 * Static utility methods for operating on implementations of {@code Collection} i.e. containers.
 * <p>
 * For methods that return a modification of a passed collection, the result will be of the same type if possible.
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
     * var grades = transformValues(scores, score -> {
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

    private static final class DefaultMap<K, V> extends HashMap<K, V> {}

}
