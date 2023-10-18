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

import java.util.Objects;

/**
 * Static utility methods for operating on {@code String}.
 */
public final class Str {

    private Str() {
        throw new IllegalAccessError("Utility class should be accessed statically and never constructed");
    }

    /**
     * Checks that the specified {@code string} is not {@code null} or empty (length == 0).
     * <p>
     * If {@code null} a customized {@code NullPointerException} is thrown.
     * If empty a customized {@code IllegalArgumentException} is thrown.
     * <p>
     * Internally uses {@link Objects#requireNonNull(Object, String)} for the {@code null} check.
     * As such, this method can be used in exactly the same way:
     * <pre>{@code
     * public User(String name) {
     *     this.name = Str.requireNotEmpty(name, "'name' must not be empty");
     * }
     * }</pre>
     *
     * @param string the string reference to check for null or empty
     * @param message detail message to be used in the event that an exception is thrown
     * @return {@code string} if not {@code null} or empty
     * @throws NullPointerException if {@code string} is {@code null}
     * @throws IllegalArgumentException if {@code string} is empty
     */
    public static String requireNotEmpty(String string, String message) {
        Objects.requireNonNull(string, message);
        if (string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return string;
    }

}
