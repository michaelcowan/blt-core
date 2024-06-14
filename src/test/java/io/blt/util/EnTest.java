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
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.En.of;
import static io.blt.util.En.ofIgnoreCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class EnTest <E extends Enum<E>> {

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(Obj.class);
    }

    @ParameterizedTest
    @MethodSource("names")
    void ofShouldReturnExpectedEnumWhenValueIsEnumName(Class<E> type, E expected, String value) {
        var result = of(type, value);

        assertThat(result)
                .isEqualTo(Optional.of(expected));
    }

    @ParameterizedTest
    @MethodSource("lowerCaseNames")
    void ofShouldReturnEmptyWhenValueIsChangedCaseEnumName(Class<E> type, E unused, String value) {
        var result = of(type, value);

        assertThat(result)
                .isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void ofShouldReturnEmptyWhenValueIs(String value) {
        var result = of(SimpleEnum.class, value);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void ofShouldThrowWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> of(SimpleEnum.class, null));
    }

    @Test
    void ofShouldThrowWhenTypeIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> of(null, "value"));
    }

    @ParameterizedTest
    @MethodSource("names")
    void ofIgnoreCaseShouldReturnExpectedEnumWhenValueIsEnumName(Class<E> type, E expected, String value) {
        var result = ofIgnoreCase(type, value);

        assertThat(result)
                .isEqualTo(Optional.of(expected));
    }

    @ParameterizedTest
    @MethodSource("lowerCaseNames")
    void ofIgnoreCaseShouldReturnExpectedEnumWhenValueIsChangedCaseEnumName(Class<E> type, E expected, String value) {
        var result = ofIgnoreCase(type, value);

        assertThat(result)
                .isEqualTo(Optional.of(expected));
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void ofIgnoreCaseShouldReturnEmptyWhenValueIs(String value) {
        var result = ofIgnoreCase(SimpleEnum.class, value);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void ofIgnoreCaseShouldThrowWhenValueIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> ofIgnoreCase(SimpleEnum.class, null));
    }

    @Test
    void ofIgnoreCaseShouldThrowWhenTypeIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> ofIgnoreCase(null, "value"));
    }

    enum SimpleEnum {
        VALUE_1, VALUE_2
    }

    enum EnumWithValueOverride {
        VALUE_1("override_value_1"), VALUE_2("override_value_2");

        private final String value;

        EnumWithValueOverride(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    enum EnumWithValueAndToStringOverride {
        VALUE_1("override_value_1"), VALUE_2("override_value_2");

        private final String value;

        EnumWithValueAndToStringOverride(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    static Stream<Arguments> names() {
        return Stream.of(
                namesFor(SimpleEnum.class),
                namesFor(EnumWithValueOverride.class),
                namesFor(EnumWithValueAndToStringOverride.class)
        ).flatMap(Function.identity());
    }

    static Stream<Arguments> lowerCaseNames() {
        return Stream.of(
                lowerCaseNamesFor(SimpleEnum.class),
                lowerCaseNamesFor(EnumWithValueOverride.class),
                lowerCaseNamesFor(EnumWithValueAndToStringOverride.class)
        ).flatMap(Function.identity());
    }

    static Stream<String> invalid() {
        return Stream.of("", "invalid", "INVALID");
    }

    private static <E extends Enum<E>> Stream<Arguments> namesFor(Class<E> type) {
        return Stream.of(type.getEnumConstants())
                .map(value -> Arguments.of(type, value, value.name()));
    }

    private static <E extends Enum<E>> Stream<Arguments> lowerCaseNamesFor(Class<E> type) {
        return Stream.of(type.getEnumConstants())
                .map(value -> Arguments.of(type, value, value.name().toLowerCase()));
    }

}
