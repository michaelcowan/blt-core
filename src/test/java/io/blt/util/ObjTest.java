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

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.Obj.newInstanceOf;
import static io.blt.util.Obj.orElseGet;
import static io.blt.util.Obj.orElseOnException;
import static io.blt.util.Obj.poke;
import static io.blt.util.Obj.tap;
import static io.blt.util.Obj.throwIf;
import static io.blt.util.Obj.throwUnless;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ObjTest {

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(Obj.class);
    }

    @Nested
    class Instance {

        @Test
        void pokeShouldReturnInstance() {
            var instance = new Object();

            var result = poke(instance, c -> {});

            assertThat(result).isEqualTo(instance);
        }

        @Test
        void pokeShouldPassInstanceToConsumer() {
            var instance = new Object();
            var reference = new AtomicReference<>();

            poke(instance, reference::set);

            var result = reference.get();
            assertThat(result).isEqualTo(instance);
        }

        @Test
        void pokeShouldOperateOnTheInstance() {
            var result = poke(new User(), u -> {
                u.setName("Greg");
                u.setAge(15);
            });

            assertThat(result)
                    .extracting(User::getName, User::getAge)
                    .containsExactly("Greg", 15);
        }

        @Test
        void pokeShouldBubbleUpAnyExceptionsThrownByConsumer() {
            var exception = new IOException("mock checked exception");

            assertThatException()
                    .isThrownBy(() -> poke(new User(), u -> {throw exception;}))
                    .isEqualTo(exception);
        }

    }

    @Nested
    class Supplied {

        @Test
        void tapShouldReturnSuppliedInstance() {
            var instance = new Object();

            var result = tap(() -> instance, s -> {});

            result = tap(() -> instance, s -> {});

            assertThat(result).isEqualTo(instance);
        }

        @Test
        void tapShouldPassSuppliedInstanceToConsumer() {
            var instance = new Object();
            var reference = new AtomicReference<>();

            tap(() -> instance, reference::set);

            var result = reference.get();
            assertThat(result).isEqualTo(instance);
        }

        @Test
        void tapShouldOperateOnTheSuppliedInstance() {
            var result = tap(User::new, u -> {
                u.setName("Greg");
                u.setAge(15);
            });

            assertThat(result)
                    .extracting(User::getName, User::getAge)
                    .containsExactly("Greg", 15);
        }

        @Test
        void tapShouldBubbleUpAnyExceptionsThrownByConsumer() {
            var exception = new IOException("mock checked exception");

            assertThatException()
                    .isThrownBy(() -> tap(User::new, u -> {throw exception;}))
                    .isEqualTo(exception);
        }

    }

    @Test
    void orElseGetShouldReturnValueIfNonNull() {
        var value = "Phil";

        var result = orElseGet(value, () -> null);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void orElseGetShouldReturnSupplierResultIfValueIsNull() {
        var supplierResult = "Louis";

        var result = orElseGet(null, () -> supplierResult);

        assertThat(result).isEqualTo(supplierResult);
    }

    @Test
    void orElseGetShouldBubbleUpSupplierThrowable() {
        var exception = new Exception("mock exception");

        assertThatException()
                .isThrownBy(() -> orElseGet(null, () -> {throw exception;}))
                .isEqualTo(exception);
    }

    @Test
    void orElseOnExceptionShouldReturnSupplierResultIfNoExceptionIsThrown() {
        var supplierResult = "Greg";

        var result = orElseOnException(() -> supplierResult, null);

        assertThat(result).isEqualTo(supplierResult);
    }

    @Test
    void orElseOnExceptionShouldReturnValueIfExceptionIsThrown() {
        var value = "Sven";

        var result = orElseOnException(() -> {throw new Exception("mock exception");}, value);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void throwIfShouldThrowWhenPredicateIsTrue() {
        var exception = new Exception("mock exception");

        assertThatException()
                .isThrownBy(() -> throwIf(null, v -> true, () -> exception))
                .isEqualTo(exception);
    }

    @Test
    void throwIfShouldReturnValueWhenPredicateIsFalse() throws Exception {
        var value = "mock value";

        var result = throwIf(value, v -> false, () -> new Exception("mock exception"));

        assertThat(result)
                .isEqualTo(value);
    }

    @Test
    void throwUnlessShouldThrowWhenPredicateIsFalse() {
        var exception = new Exception("mock exception");

        assertThatException()
                .isThrownBy(() -> throwUnless(null, v -> false, () -> exception))
                .isEqualTo(exception);
    }

    @Test
    void throwUnlessShouldReturnValueWhenPredicateIsTrue() throws Exception {
        var value = "mock value";

        var result = throwUnless(value, v -> true, () -> new Exception("mock exception"));

        assertThat(result)
                .isEqualTo(value);
    }

    static Stream<Arguments> newInstanceOfShouldReturnNewInstanceOf() {
        return Stream.of(
                Arguments.of(String.class, "hello Worf"),
                Arguments.of(HashMap.class, new HashMap<>(Map.of("hello", "Worf"))),
                Arguments.of(LinkedList.class, new LinkedList<>(List.of("hello", "Worf"))));
    }

    @ParameterizedTest
    @MethodSource
    void newInstanceOfShouldReturnNewInstanceOf(Class<?> type, Object instance) {
        var result = newInstanceOf(instance);

        assertThat(result)
                .isNotEmpty()
                .get()
                .isInstanceOf(type)
                .isNotEqualTo(instance);
    }

    static Stream<Object> newInstanceOfShouldReturnEmptyWhenObjectTypeHasNoZeroArgumentConstructor() {
        return Stream.of(
                Map.of("hello", "Worf"),
                List.of("hello", "Worf"),
                URI.create("https://sttng/worf"));
    }

    @ParameterizedTest
    @MethodSource
    void newInstanceOfShouldReturnEmptyWhenObjectTypeHasNoZeroArgumentConstructor(Object object) {
        var result = newInstanceOf(object);

        assertThat(result).isEmpty();
    }

    @Test
    void newInstanceOfShouldThrowWhenArgumentIsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> newInstanceOf(null));
    }

    public static class User {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

}
