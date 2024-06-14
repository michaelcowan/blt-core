/*
 * MIT License
 *
 * Copyright (c) 2024 Michael Cowan
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

import java.util.Optional;

/**
 * Static utility methods for operating on {@code Enum}.
 */
public final class En {

    private En() {
        throw new IllegalAccessError("Utility class should be accessed statically and never constructed");
    }

    /**
     * Returns the {@code enum} constant matching {@code name} as an {@link Optional}; otherwise, returns empty.
     * The {@code name} must match exactly.
     * <p>
     * This method will not throw for an invalid enum name and instead will return empty.
     * </p>
     * e.g.,
     * <pre>{@code
     * of(DayOfWeek.class, "FRIDAY");    // Optional.of(DayOfWeek.FRIDAY)
     * of(DayOfWeek.class, "friday");    // Optional.empty()
     * of(DayOfWeek.class, "Worf");      // Optional.empty()
     * of(DayOfWeek.class, "");          // Optional.empty()
     * }</pre>
     *
     * @param type The {@link Class} object of the enum class
     * @param name The name of the constant to return
     * @param <E>  The type of the {@code Enum}
     * @return an {@code Optional} containing the {@code enum} constant if found
     * @throws NullPointerException if {@code type} or {@code name} is {@code null}
     */
    public static <E extends Enum<E>> Optional<E> of(Class<E> type, String name) {
        try {
            return Optional.of(Enum.valueOf(type, name));
        } catch (IllegalArgumentException ignore) {
            return Optional.empty();
        }
    }

    /**
     * Returns the {@code enum} constant matching {@code name} as an {@link Optional}; otherwise, returns empty.
     * The {@code name} comparison is case-insensitive.
     * <p>
     * This method will not throw for an invalid enum name and instead will return empty.
     * </p>
     * <pre>{@code
     * ofIgnoreCase(DayOfWeek.class, "FRIDAY");    // Optional.of(DayOfWeek.FRIDAY)
     * ofIgnoreCase(DayOfWeek.class, "friday");    // Optional.of(DayOfWeek.FRIDAY)
     * ofIgnoreCase(DayOfWeek.class, "Worf");      // Optional.empty()
     * ofIgnoreCase(DayOfWeek.class, "");          // Optional.empty()
     * }</pre>
     *
     * @param type The {@link Class} object of the enum class
     * @param name The name of the constant to return
     * @param <E>  The type of the {@code Enum}
     * @return an {@code Optional} containing the {@code enum} constant if found
     * @throws NullPointerException if {@code type} or {@code name} is {@code null}
     */
    public static <E extends Enum<E>> Optional<E> ofIgnoreCase(Class<E> type, String name) {
        for (var element : type.getEnumConstants()) {
            if (name.equalsIgnoreCase(element.name())) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }

}
