/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormat;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;

/**
 * @author Xiaotian Le
 */
public class CoNLL2003Reader extends ColumnFormatGenericReader {

    public static final String CORPUS_ID = "CoNLL 2003";

    public enum Tagset {
        CONLL,  // Output standard CoNLL 2003 tags
        ONTONOTES,  // Unsupported
        ERE  // Unsupported
    }

    private static final int COLUMN_TOKEN = 0;
    private static final int COLUMN_POS = 1;
    private static final int COLUMN_CHUNK = 2;
    private static final int COLUMN_NER = 3;

    public CoNLL2003Reader(Tagset tagset) {
        super(getColumnFormat(tagset), CORPUS_ID);
    }

    public CoNLL2003Reader(Tagset tagset, String corpusDirectory) {
        this(tagset, corpusDirectory, "");
    }

    public CoNLL2003Reader(Tagset tagset, String corpusDirectory, String fileExtension) {
        super(getColumnFormat(tagset), CORPUS_ID, corpusDirectory, fileExtension);
    }

    public static ColumnFormat getColumnFormat(Tagset tagset) {
        ColumnFormat format = new ColumnFormat();

        format.tokenColumn = COLUMN_TOKEN;
        format.tokenLabelColumns.add(new ColumnFormat.Column(COLUMN_POS, ViewNames.POS, "-"));
        format.iobColumns.add(new ColumnFormat.Column(COLUMN_CHUNK, ViewNames.SHALLOW_PARSE, "O"));
        if (tagset == Tagset.CONLL) {
            format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_CONLL, "O"));
        } else {
            throw new IllegalArgumentException("Tagset " + tagset.toString() + " is not supported.");
        }

        format.columnDelimiter = " ";
        format.iobCanStartWithI = true;

        return format;
    }
}
