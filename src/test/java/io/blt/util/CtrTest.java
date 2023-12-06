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

import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.Ctr.transformValues;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class CtrTest {

    static final Map<String, Month> MAP = Map.of(
            "Mike", Month.JUNE, "Greg", Month.NOVEMBER, "Phil", Month.OCTOBER, "Louis", Month.FEBRUARY);

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(Ctr.class);
    }

    static Stream<Map<String, Month>> transformValuesShouldReturnNewMapOfSameTypeContainingTransformedValues() {
        return Stream.of(MAP, new HashMap<>(MAP), new LinkedHashMap<>(MAP), new TreeMap<>(MAP));
    }

    @ParameterizedTest
    @MethodSource
    void transformValuesShouldReturnNewMapOfSameTypeContainingTransformedValues(Map<String, Month> map) {
        var result = transformValues(map, e -> e.getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        assertThat(result)
                .isNotEqualTo(map)
                .hasSameClassAs(map)
                .containsOnly(
                        entry("Mike", "June"),
                        entry("Greg", "November"),
                        entry("Phil", "October"),
                        entry("Louis", "February"));
    }

    static Stream<Map<?, ?>> transformValuesShouldReturnEmptyMapOfSameType() {
        return Stream.of(Map.of(), new HashMap<>(), new LinkedHashMap<>(), new TreeMap<>());
    }

    @ParameterizedTest
    @MethodSource
    void transformValuesShouldReturnEmptyMapOfSameType(Map<String, Month> map) {
        var result = transformValues(map, e -> { throw new IllegalStateException(); });

        assertThat(result)
                .hasSameClassAs(map)
                .isEmpty();
    }

    @Test
    void transformValuesShouldThrowWhenMapIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> transformValues(null, e -> e));
    }

    @Test
    void transformValuesShouldBubbleUpExceptionThrownByTransformationFunction() {
        var exception = new Exception();

        assertThatException()
                .isThrownBy(() -> transformValues(Map.of("hello", "Worf"), e -> { throw exception; }))
                .isEqualTo(exception);
    }

}
