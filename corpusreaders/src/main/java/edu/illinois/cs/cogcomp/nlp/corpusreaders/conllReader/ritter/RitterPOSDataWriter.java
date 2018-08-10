package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

/**
 * @author Xiaotian Le
 */
public class RitterPOSDataWriter extends ColumnFormatGenericWriter {

    public RitterPOSDataWriter() {
        super(RitterPOSDataReader.getColumnFormat());
    }

    public RitterPOSDataWriter(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
        super(RitterPOSDataReader.getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
    }
}
