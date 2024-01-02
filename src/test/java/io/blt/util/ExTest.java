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

package io.blt.util;

import io.blt.util.functional.ThrowingRunnable;
import io.blt.util.functional.ThrowingSupplier;
import java.io.IOException;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.Ex.transformExceptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Fail.fail;

class ExTest {

    IOException mockException = new IOException("mock exception");

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(Ex.class);
    }

    @Nested
    class UsingThrowingSupplier {

        ThrowingSupplier<String, IOException> supplier = () -> "hello Worf";

        ThrowingSupplier<String, IOException> throwingSupplier = () -> {
            throw mockException;
        };

        @Test
        void transformExceptionsShouldReturnResultWhenNoExceptionIsThrown() throws Throwable {
            var result = transformExceptions(supplier, r ->
                    fail("Should not have executed transformer function"));

            assertThat(result)
                    .isEqualTo("hello Worf");
        }

        @Test
        void transformExceptionsShouldCallAndThrowTransformerWhenExceptionIsThrown() {
            var expected = new Exception("transformed exception");

            var transformer = new Function<Exception, Throwable>() {
                @Override
                public Throwable apply(Exception e) {
                    assertThat(e).isEqualTo(mockException);
                    return expected;
                }
            };

            assertThatException()
                    .isThrownBy(() -> transformExceptions(throwingSupplier, transformer))
                    .isEqualTo(expected);
        }

    }

    @Nested
    class UsingThrowingRunnable {

        ThrowingRunnable<IOException> runnable = () -> {};

        ThrowingRunnable<IOException> throwingRunnable = () -> {
            throw mockException;
        };

        @Test
        void transformExceptionsShouldNotThrowWhenNoExceptionIsThrown() {
            assertThatNoException()
                    .isThrownBy(() -> transformExceptions(runnable, r ->
                            fail("Should not have executed transformer function")));
        }

        @Test
        void transformExceptionsShouldCallAndThrowTransformerWhenExceptionIsThrown() {
            var expected = new Exception("transformed exception");

            var transformer = new Function<Exception, Throwable>() {
                @Override
                public Throwable apply(Exception e) {
                    assertThat(e).isEqualTo(mockException);
                    return expected;
                }
            };

            assertThatException()
                    .isThrownBy(() -> transformExceptions(throwingRunnable, transformer))
                    .isEqualTo(expected);
        }

    }

}
