/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.core.func;

/**
 * Applies to a runnable with no arguments and no return value, either throwing exceptions or not
 *
 * @author Xiaotian Le
 */
@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {
    void run() throws T;
}
