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

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.blt.test.AssertUtils.assertValidUtilityClass;
import static io.blt.util.Obj.tap;
import static org.assertj.core.api.Assertions.assertThat;

class ObjTest {

    @Test
    void shouldBeValidUtilityClass() throws NoSuchMethodException {
        assertValidUtilityClass(Obj.class);
    }

    @Nested
    class Instance {

        @Test
        void tapShouldReturnInstance() {
            var instance = new Object();

            var result = tap(instance, c -> {});

            assertThat(result).isEqualTo(instance);
        }

        @Test
        void tapShouldPassInstanceToConsumer() {
            var instance = new Object();
            var reference = new AtomicReference<>();

            tap(instance, reference::set);

            var result = reference.get();
            assertThat(result).isEqualTo(instance);
        }

        @Test
        void tapShouldOperateOnTheInstance() {
            var result = tap(new User(), u -> {
                u.setName("Greg");
                u.setAge(15);
            });

            assertThat(result)
                    .extracting(User::getName, User::getAge)
                    .containsExactly("Greg", 15);
        }

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
