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
import java.util.function.Function;

/**
 * Static utility methods centred around {@code Exception} and {@code Throwable}.
 * <p>Includes raising and handling exceptions.</p>
 */
public final class Ex {

    private Ex() {
        throw new IllegalAccessError("Utility class should be accessed statically and never constructed");
    }

    /**
     * Executes a supplier, transforming any thrown exception using a specified function.
     * e.g., say we have a custom high-level {@code XmlProcessingException} that we want to raise when parsing XML:
     * <pre>{@code
     * public Document parseXml(String pathname) throws XmlProcessingException {
     *     return Ex.transformExceptions(
     *             () -> DocumentBuilderFactory
     *                     .newInstance()
     *                     .newDocumentBuilder()       // throws ParserConfigurationException
     *                     .parse(new File(pathname)), // throws SAXException, IOException
     *             XmlProcessingException::new);
     * }
     * }</pre>
     *
     * @param <R>         The result type of the supplier
     * @param <E>         The type of transformed exception
     * @param supplier    The supplier that may throw an exception
     * @param transformer Function to transform exceptions thrown by the supplier
     * @return The result of the supplier if successful
     * @throws E If the supplier throws an exception, transformed by the provided function
     */
    public static <R, E extends Throwable> R transformExceptions(
            ThrowingSupplier<R, ? extends Exception> supplier, Function<? super Exception, E> transformer) throws E {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw transformer.apply(e);
        }
    }

    /**
     * Executes a runnable, transforming any thrown exception using a specified function
     * e.g.,
     * <pre>{@code
     * public void appendFormattedDateToFile(String date, String fileName) throws LoggingException {
     *     Ex.transformExceptions(
     *             () -> {
     *                 var formatted = new SimpleDateFormat()
     *                         .parse(date)            // ParseException
     *                         .toString();
     *
     *                 Files.write(                    // IOException
     *                         Paths.get(fileName),
     *                         formatted.getBytes(),
     *                         StandardOpenOption.APPEND);
     *             }, LoggingException::new
     *     );
     * }
     * }</pre>
     *
     * @param <E>         The type of transformed exception
     * @param runnable    The runnable that may throw an exception
     * @param transformer Function to transform exceptions thrown by the supplier
     * @throws E If the supplier throws an exception, transformed by the provided function
     */
    public static <E extends Throwable> void transformExceptions(
            ThrowingRunnable<? extends Exception> runnable, Function<? super Exception, E> transformer) throws E {
        transformExceptions(() -> {
            runnable.run();
            return null;
        }, transformer);
    }

}
