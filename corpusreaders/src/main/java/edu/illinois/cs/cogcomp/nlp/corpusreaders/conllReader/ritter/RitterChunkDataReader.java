package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormat;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;

/**
 * @author Xiaotian Le
 */
public class RitterChunkDataReader extends ColumnFormatGenericReader {

    public static final String CORPUS_ID = "Ritter et al. 2011. Chunk";

    private static final int COLUMN_TOKEN = 0;
    private static final int COLUMN_CHUNK = 1;

    public RitterChunkDataReader() {
        super(getColumnFormat(), CORPUS_ID);
    }

    public RitterChunkDataReader(String corpusDirectory) {
        this(corpusDirectory, "chunk.txt");
    }

    public RitterChunkDataReader(String corpusDirectory, String fileExtension) {
        super(getColumnFormat(), CORPUS_ID, corpusDirectory, fileExtension);
    }

    public static ColumnFormat getColumnFormat() {
        ColumnFormat format = new ColumnFormat();

        format.tokenColumn = COLUMN_TOKEN;
        format.iobColumns.add(new ColumnFormat.Column(COLUMN_CHUNK, ViewNames.SHALLOW_PARSE, "O", String::toUpperCase, String::toLowerCase));

        format.columnDelimiter = " ";
        format.iobCanStartWithI = false;

        return format;
    }
}
