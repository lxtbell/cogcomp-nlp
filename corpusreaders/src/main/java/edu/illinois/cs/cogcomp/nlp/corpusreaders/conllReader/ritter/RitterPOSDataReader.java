package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormat;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;

/**
 * @author Xiaotian Le
 */
public class RitterPOSDataReader extends ColumnFormatGenericReader {

    public static final String CORPUS_ID = "Ritter et al. 2011. POS";

    private static final int COLUMN_TOKEN = 0;
    private static final int COLUMN_POS = 1;

    public RitterPOSDataReader() {
        super(getColumnFormat(), CORPUS_ID);
    }

    public RitterPOSDataReader(String corpusDirectory) {
        this(corpusDirectory, "pos.txt");
    }

    public RitterPOSDataReader(String corpusDirectory, String fileExtension) {
        super(getColumnFormat(), CORPUS_ID, corpusDirectory, fileExtension);
    }

    public static ColumnFormat getColumnFormat() {
        ColumnFormat format = new ColumnFormat();

        format.tokenColumn = COLUMN_TOKEN;
        format.tokenLabelColumns.add(new ColumnFormat.Column(COLUMN_POS, ViewNames.POS, "-"));

        format.columnDelimiter = " ";
        format.iobCanStartWithI = false;

        return format;
    }
}
