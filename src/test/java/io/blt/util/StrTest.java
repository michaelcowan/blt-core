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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.Str.requireNotEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class StrTest {

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(Str.class);
    }

    @Nested
    class RequireNotEmpty {

        @ParameterizedTest
        @ValueSource(strings = {"test string", " "})
        void shouldReturnStringFor(String string) {
            var result = requireNotEmpty(string, "mock message");

            assertThat(result).isEqualTo(string);
        }

        @Test
        void shouldThrowNullPointerExceptionWithMessageForNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> requireNotEmpty(null, "mock message"))
                    .withMessage("mock message");
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWithMessageForNull() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> requireNotEmpty("", "mock message"))
                    .withMessage("mock message");
        }

    }

}
