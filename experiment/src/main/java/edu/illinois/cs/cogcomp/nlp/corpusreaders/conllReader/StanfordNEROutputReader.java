/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;

/*
 * @author Xiaotian Le
 */
public class StanfordNEROutputReader extends ColumnFormatGenericReader {

    public static final String CORPUS_ID = "Stanford NER Output";

    private static final int COLUMN_TOKEN = 0;
    private static final int COLUMN_NER = 2;

    public StanfordNEROutputReader() {
        super(getColumnFormat(), CORPUS_ID);
    }

    public static ColumnFormat getColumnFormat() {
        ColumnFormat format = new ColumnFormat();

        format.tokenColumn = COLUMN_TOKEN;
        format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_CONLL, "O"));

        format.columnDelimiter = "\t";
        format.iobCanStartWithI = false;

        return format;
    }
}
