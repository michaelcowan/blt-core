/*
 * MIT License
 *
 * Copyright (c) 2022 Michael Cowan
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

package io.blt.util.stream;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class SingletonCollectorsTest {

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(SingletonCollectors.class);
    }

    @Nested
    class ToOptional {

        @Test
        void empty() {
            var result = Stream.empty()
                               .collect(SingletonCollectors.toOptional());

            assertThat(result).isEmpty();
        }

        @Test
        void containsSingleElement() {
            var result = Stream.of("one")
                               .collect(SingletonCollectors.toOptional());

            assertThat(result).contains("one");
        }

        @ParameterizedTest
        @ValueSource(strings = {"one", "two", "three"})
        void containsSingleFilteredElement(String filter) {
            var result = Stream.of("one", "two", "three")
                               .filter(filter::equals)
                               .collect(SingletonCollectors.toOptional());

            assertThat(result).contains(filter);
        }

        @ParameterizedTest
        @MethodSource("io.blt.util.stream.SingletonCollectorsTest#moreThanOneElement")
        void throwsOnMoreThanOneElement(List<String> elements) {
            assertThatIllegalStateException()
                    .isThrownBy(() -> elements.stream().collect(SingletonCollectors.toOptional()))
                    .withMessage("Expected stream to contain exactly 0 or 1 elements");
        }

    }

    @Nested
    class ToNullable {

        @Test
        void empty() {
            var result = Stream.empty()
                               .collect(SingletonCollectors.toNullable());

            assertThat(result).isNull();
        }

        @Test
        void containsSingleElement() {
            var result = Stream.of("one")
                               .collect(SingletonCollectors.toNullable());

            assertThat(result).contains("one");
        }

        @ParameterizedTest
        @ValueSource(strings = {"one", "two", "three"})
        void containsSingleFilteredElement(String filter) {
            var result = Stream.of("one", "two", "three")
                               .filter(filter::equals)
                               .collect(SingletonCollectors.toNullable());

            assertThat(result).contains(filter);
        }

        @ParameterizedTest
        @MethodSource("io.blt.util.stream.SingletonCollectorsTest#moreThanOneElement")
        void throwsOnMoreThanOneElement(List<String> elements) {
            assertThatIllegalStateException()
                    .isThrownBy(() -> elements.stream().collect(SingletonCollectors.toNullable()))
                    .withMessage("Expected stream to contain exactly 0 or 1 elements");
        }

    }

    private static Stream<List<String>> moreThanOneElement() {
        return Stream.of(
                List.of("one", "two"),
                List.of("one", "two", "three"));
    }
}
