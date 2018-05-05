/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A helper class for storing the format
 * Defines the column mapping
 *
 * @author Xiaotian Le
 */
public class ColumnFormat {
    public int tokenColumn = 0;
    public List<Column> tokenLabelColumns = new ArrayList<>();
    public List<Column> iobColumns = new ArrayList<>();

    public String columnDelimiter = " ";  // Regex expression for splitting the columns, e.g. "\t" " "
    public boolean iobCanStartWithI = false;  // If I-label without a previous B-label should also start a span

    public static class Column {
        public int columnNumber; public String viewName; public String id;
        public String defaultLabel; public Function<String, String> readLabel, writeLabel;

        public Column(int columnNumber, String viewName, String defaultLabel) {
            this(columnNumber, viewName, defaultLabel, Function.identity(), Function.identity());
        }

        public Column(
                int columnNumber, String viewName,
                String defaultLabel, Function<String, String> readLabel, Function<String, String> writeLabel) {
            this.columnNumber = columnNumber; this.viewName = viewName; this.id = viewName + "|" + columnNumber;
            this.defaultLabel = defaultLabel; this.readLabel = readLabel; this.writeLabel = writeLabel;
        }
    }
}
