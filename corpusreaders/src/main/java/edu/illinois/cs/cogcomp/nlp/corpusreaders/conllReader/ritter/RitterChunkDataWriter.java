package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

/**
 * @author Xiaotian Le
 */
public class RitterChunkDataWriter extends ColumnFormatGenericWriter {

    public RitterChunkDataWriter() {
        super(RitterChunkDataReader.getColumnFormat());
    }

    public RitterChunkDataWriter(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
        super(RitterChunkDataReader.getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
    }
}
