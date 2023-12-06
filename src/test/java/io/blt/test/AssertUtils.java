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

package io.blt.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public final class AssertUtils {

    private AssertUtils() {}

    public static void assertValidUtilityClass(Class<?> clazz) throws NoSuchMethodException {
        assertThat(clazz)
                .withFailMessage("Utility class must be final")
                .isFinal();

        assertThat(clazz.getDeclaredConstructors())
                .withFailMessage("Utility class must only have a zero argument constructor")
                .hasSize(1)
                .extracting(Constructor::getParameterCount)
                .hasSize(1);

        var constructor = clazz.getDeclaredConstructor();

        assertThat(constructor.getModifiers())
                .withFailMessage("Constructor must be private")
                .matches(Modifier::isPrivate);

        constructor.setAccessible(true);

        assertThatExceptionOfType(InvocationTargetException.class)
                .describedAs("Utility class should throw if constructed")
                .isThrownBy(constructor::newInstance)
                .havingCause()
                .isInstanceOf(IllegalAccessError.class)
                .withMessage("Utility class should be accessed statically and never constructed");
    }

    public static void assertValidExposedPrivateImplementationClass(Class<?> clazz) {
        assertThat(clazz)
                .isFinal()
                .isPublic();

        assertThat(clazz.getConstructors())
                .describedAs("All constructors must be private")
                .extracting(Constructor::getModifiers)
                .allMatch(Modifier::isPrivate);
    }

}
