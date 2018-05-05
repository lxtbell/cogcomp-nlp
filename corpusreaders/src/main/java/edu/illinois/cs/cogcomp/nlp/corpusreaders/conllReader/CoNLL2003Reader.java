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
public class CoNLL2003Reader extends ColumnFormatGenericReader {

    public static final String CORPUS_ID = "CoNLL 2003";

    private static final int COLUMN_TOKEN = 0;
    private static final int COLUMN_POS = 1;
    private static final int COLUMN_CHUNK = 2;
    private static final int COLUMN_NER = 3;

    public CoNLL2003Reader() {
        super(getColumnFormat(), CORPUS_ID);
    }

    /**
     * @param corpusDirectory where test.a.eng, test.b.eng, train.eng are stored
     */
    public CoNLL2003Reader(String corpusDirectory) {
        super(getColumnFormat(), CORPUS_ID, corpusDirectory, ".eng");
    }

    public static ColumnFormat getColumnFormat() {
        ColumnFormat format = new ColumnFormat();

        format.tokenColumn = COLUMN_TOKEN;
        format.tokenLabelColumns.add(new ColumnFormat.Column(COLUMN_POS, ViewNames.POS, "-"));
        format.iobColumns.add(new ColumnFormat.Column(COLUMN_CHUNK, ViewNames.SHALLOW_PARSE, "O"));
        format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_CONLL, "O"));

        format.columnDelimiter = " ";
        format.iobCanStartWithI = true;

        return format;
    }
}
