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

import java.nio.file.FileSystems;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.Ctr.computeIfAbsent;
import static io.blt.util.Ctr.hasSize;
import static io.blt.util.Ctr.transformValues;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.fail;

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
        var result = transformValues(map, e -> {
            throw new IllegalStateException();
        });

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
                .isThrownBy(() -> transformValues(Map.of("hello", "Worf"), e -> {
                    throw exception;
                }))
                .isEqualTo(exception);
    }

    @Nested
    class UsingFunction {

        @Test
        void computeIfAbsentShouldReturnValueAndNotModifyMapWhenValueIsPresent() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);

            var result = computeIfAbsent(map, "Link", x -> fail("Should not have executed compute function"));

            assertThat(result)
                    .isEqualTo("Master Sword");

            assertThat(map)
                    .containsExactlyEntriesOf(entries);
        }

        @Test
        void computeIfAbsentShouldComputeAndReturnValueAndModifyMapWhenValueIsNotPresent() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);

            var result = computeIfAbsent(map, "Mario", x -> "Super Mushroom");

            assertThat(result)
                    .isEqualTo("Super Mushroom");

            assertThat(map)
                    .hasSize(2)
                    .containsAllEntriesOf(entries)
                    .containsEntry("Mario", "Super Mushroom");
        }

        @Test
        void computeIfAbsentShouldReturnNullAndNotModifyMapWhenComputeReturnsNull() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);

            var result = computeIfAbsent(map, "Mario", x -> null);

            assertThat(result)
                    .isNull();

            assertThat(map)
                    .containsExactlyEntriesOf(entries);
        }

        @Test
        void computeIfAbsentShouldBubbleUpExceptionAndNotModifyMapWhenComputeThrows() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);
            var exception = new Exception("Mock exception");

            assertThatException()
                    .isThrownBy(() -> computeIfAbsent(map, "Mario", x -> {
                        throw exception;
                    }))
                    .isEqualTo(exception);

            assertThat(map)
                    .containsExactlyEntriesOf(entries);
        }

    }

    @Nested
    class UsingSupplier {

        @Test
        void computeIfAbsentShouldReturnValueAndNotModifyMapWhenValueIsPresent() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);

            var result = computeIfAbsent(map, "Link", () -> fail("Should not have executed compute function"));

            assertThat(result)
                    .isEqualTo("Master Sword");

            assertThat(map)
                    .containsExactlyEntriesOf(entries);
        }

        @Test
        void computeIfAbsentShouldComputeAndReturnValueAndModifyMapWhenValueIsNotPresent() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);

            var result = computeIfAbsent(map, "Mario", () -> "Super Mushroom");

            assertThat(result)
                    .isEqualTo("Super Mushroom");

            assertThat(map)
                    .hasSize(2)
                    .containsAllEntriesOf(entries)
                    .containsEntry("Mario", "Super Mushroom");
        }

        @Test
        void computeIfAbsentShouldReturnNullAndNotModifyMapWhenComputeReturnsNull() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);

            var result = computeIfAbsent(map, "Mario", () -> null);

            assertThat(result)
                    .isNull();

            assertThat(map)
                    .containsExactlyEntriesOf(entries);
        }

        @Test
        void computeIfAbsentShouldBubbleUpExceptionAndNotModifyMapWhenComputeThrows() {
            var entries = Map.of("Link", "Master Sword");
            var map = new HashMap<>(entries);
            var exception = new Exception("Mock exception");

            assertThatException()
                    .isThrownBy(() -> computeIfAbsent(map, "Mario", () -> {
                        throw exception;
                    }))
                    .isEqualTo(exception);

            assertThat(map)
                    .containsExactlyEntriesOf(entries);
        }

    }

    static Stream<Collection<String>> collections() {
        return Stream.of(
                List.of(),
                List.of("Greg"),
                List.of("Greg", "Louis"),
                List.of("Greg", "Louis", "Phil"),
                Set.of(),
                Set.of("Greg"),
                Set.of("Greg", "Louis"),
                Set.of("Greg", "Louis", "Phil")
        );
    }

    @ParameterizedTest
    @MethodSource("collections")
    void hasSizeShouldReturnTrueForCorrectCollectionSize(Collection<String> collection) {
        var result = hasSize(collection, collection.size());

        assertThat(result)
                .isTrue();
    }

    @ParameterizedTest
    @MethodSource("collections")
    void hasSizeShouldReturnFalseForIncorrectCollectionSize(Collection<String> collection) {
        var wrongSize = collection.size() - 1;

        var result = hasSize(collection, wrongSize);

        assertThat(result)
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0, 1, 2})
    void hasSizeShouldReturnFalseForNullCollectionAndSize(int size) {
        Collection<?> collection = null;

        var result = hasSize(collection, size);

        assertThat(result)
                .isFalse();
    }

    @Test
    void hasSizeShouldReturnTrueForCorrectIterableSize() {
        var path = FileSystems.getDefault().getPath("/1/2/3");

        var result = hasSize(path, 3);

        assertThat(result)
                .isTrue();
    }

    @Test
    void hasSizeShouldReturnFalseForIncorrectIterableSize() {
        var path = FileSystems.getDefault().getPath("/1/2/3");

        var result = hasSize(path, 5);

        assertThat(result)
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0, 1, 2})
    void hasSizeShouldReturnFalseForNullIterableAndSize(int size) {
        Iterable<?> iterable = null;

        var result = hasSize(iterable, size);

        assertThat(result)
                .isFalse();
    }

    static Stream<Map<String, Month>> maps() {
        return Stream.of(
                Map.of(),
                Map.of("Greg", Month.NOVEMBER),
                Map.of("Greg", Month.NOVEMBER, "Phil", Month.OCTOBER),
                Map.of("Greg", Month.NOVEMBER, "Phil", Month.OCTOBER, "Louis", Month.FEBRUARY)
        );
    }

    @ParameterizedTest
    @MethodSource("maps")
    void hasSizeShouldReturnTrueForCorrectMapSize(Map<String, Month> map) {
        var result = hasSize(map, map.size());

        assertThat(result)
                .isTrue();
    }

    @ParameterizedTest
    @MethodSource("maps")
    void hasSizeShouldReturnFalseForIncorrectMapSize(Map<String, Month> map) {
        var wrongSize = map.size() + 1;

        var result = hasSize(map, wrongSize);

        assertThat(result)
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0, 1, 2})
    void hasSizeShouldReturnFalseForNullMapAndSize(int size) {
        Map<?, ?> map = null;

        var result = hasSize(map, size);

        assertThat(result)
                .isFalse();
    }

    static Stream<Arguments> arrays() {
        return Stream.of(
                Arguments.of((Object) new String[] {}),
                Arguments.of((Object) new String[] {"Greg"}),
                Arguments.of((Object) new String[] {"Greg", "Phil"}),
                Arguments.of((Object) new String[] {"Greg", "Phil", "Louis"}),
                Arguments.of((Object) new Integer[] {}),
                Arguments.of((Object) new Integer[] {1}),
                Arguments.of((Object) new Integer[] {1, 2}),
                Arguments.of((Object) new Integer[] {1, 2, 3})
        );
    }

    @ParameterizedTest
    @MethodSource("arrays")
    void hasSizeShouldReturnTrueForCorrectArraySize(Object[] array) {
        var result = hasSize(array, array.length);

        assertThat(result)
                .isTrue();
    }

    @ParameterizedTest
    @MethodSource("arrays")
    void hasSizeShouldReturnFalseForIncorrectArraySize(Object[] array) {
        var wrongSize = array.length + 1;

        var result = hasSize(array, wrongSize);

        assertThat(result)
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0, 1, 2})
    void hasSizeShouldReturnFalseForNullArrayAndSize(int size) {
        Object[] array = null;

        var result = hasSize(array, size);

        assertThat(result)
                .isFalse();
    }

}
