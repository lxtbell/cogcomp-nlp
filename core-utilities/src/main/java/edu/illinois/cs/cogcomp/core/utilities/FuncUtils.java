/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.core.utilities;

/**
 * @author Xiaotian Le
 */
public class FuncUtils {

    @FunctionalInterface
    public interface ThrowingRunnable<T extends Throwable> {
        void run() throws T;
    }

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T one, U two, V three);
    }

    public static <T extends Throwable> double measureTime(ThrowingRunnable<T> runnable) throws T {
        long time = System.currentTimeMillis();
        runnable.run();
        return ((double) (System.currentTimeMillis() - time)) / 1000;
    }
}
