/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment;

import org.apache.commons.io.output.TeeOutputStream;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.core.io.IOUtils;

/**
 * @author Xiaotian Le
 */
public class IOUtil {

    public static String getProjectFolder() {
        return System.getProperty("user.home") + "/projects/nlp-study-2018/";
    }

    public static boolean mkdirsFor(String file) {
        return (new File(file)).getParentFile().mkdirs();
    }

    public static String[] lsSorted(String directory) throws IOException {
        String[] files = IOUtils.ls(directory);
        Arrays.sort(files);
        return files;
    }

    public static String[] lsRecursiveSorted(String directory) throws IOException {
        String[] files = lsFileAbsoluteRecursiveSorted(directory, ".*");
        return Arrays.stream(files)
                .map(file -> relativize(directory, file))
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    public static String[] lsFileAbsoluteRecursiveSorted(String directory, String regex) throws IOException {
        String[] files = IOUtils.lsFilesRecursive(directory, file ->
                file.isDirectory() || file.getAbsolutePath().matches(regex));
        Arrays.sort(files);
        return files;
    }

    public static String createTempFile(String prefix) throws IOException {
        File file = File.createTempFile(prefix + "-", ".txt");
        file.deleteOnExit();
        return file.getAbsolutePath();
    }

    public static PrintStream newTeePrintStream(String path) throws IOException {
        mkdirsFor(path);
        return newTeePrintStream(new BufferedOutputStream(new FileOutputStream(path)));
    }

    public static PrintStream newTeePrintStream(OutputStream stream) {
        return new PrintStream(new ConsoleTeeOutputStream(System.out, stream));
    }

    public static class ConsoleTeeOutputStream extends TeeOutputStream {
        public ConsoleTeeOutputStream(OutputStream console, OutputStream other) {
            super(console, other);
        }

        // Do not close console stream!
        public void close() throws IOException {
            this.branch.close();
        }
    }

    public static class Params {
        public final int numThreads;

        private final Set<Integer> tasks;

        public Params(String[] args) {
            String tasksString = args.length > 0 ? args[0] : "0123456789abcdef";
            tasks = tasksString.chars().map(c -> (c <= '9') ? (c - '0') : (c - 'a' + 10)).boxed().collect(Collectors.toSet());
            numThreads = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        }

        public boolean runTask(int taskId) {
            return tasks.contains(taskId);
        }
    }

    public static String relativize(String ancestor, String descendant) {
        return Paths.get(ancestor).toAbsolutePath().relativize(Paths.get(descendant).toAbsolutePath()).toString();
    }

    public static String resolve(String ancestor, String relative) {
        return Paths.get(ancestor).toAbsolutePath().resolve(relative).toAbsolutePath().toString();
    }

    public static String rebase(String oldAncestor, String newAncestor, String descendant) {
        return resolve(newAncestor, relativize(oldAncestor, descendant));
    }
}
