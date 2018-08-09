/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.core.io;

import org.junit.Test;

import java.io.FileNotFoundException;

public class IOUtilsTest {

    // A random UUID
    public static final String NON_EXISTENT_DIRECTORY = "/tmp/3f00a85a-f2e6-4084-a73a-832c8c858b8e";

    @Test(expected = FileNotFoundException.class)
    public void testLsFilesDirectoryNotFound() throws Exception {
        IOUtils.lsFiles(NON_EXISTENT_DIRECTORY, (dir, name) -> true);
    }

    @Test(expected = FileNotFoundException.class)
    public void testLsFilesRecursiveFilenameFilterDirectoryNotFound() throws Exception {
        IOUtils.lsFilesRecursive(NON_EXISTENT_DIRECTORY, (dir, name) -> true);
    }

    @Test(expected = FileNotFoundException.class)
    public void testLsFilesRecursiveFileFilterDirectoryNotFound() throws Exception {
        IOUtils.lsFilesRecursive(NON_EXISTENT_DIRECTORY, file -> true);
    }
}
