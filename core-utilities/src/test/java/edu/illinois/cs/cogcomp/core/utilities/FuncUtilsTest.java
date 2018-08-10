/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.core.utilities;

import org.junit.Assert;
import org.junit.Test;

public class FuncUtilsTest {

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMeasureTime() {
        Assert.assertTrue(FuncUtils.measureTime(() -> System.out.println("")) >= 0);

        FuncUtils.measureTime(() -> {
            throw new IndexOutOfBoundsException();
        });
    }
}
