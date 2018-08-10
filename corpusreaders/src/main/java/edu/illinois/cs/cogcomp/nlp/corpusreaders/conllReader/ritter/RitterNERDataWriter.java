package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

/**
 * @author Xiaotian Le
 */
public class RitterNERDataWriter extends ColumnFormatGenericWriter {

    public RitterNERDataWriter(RitterNERDataReader.Tagset tagset) {
        super(RitterNERDataReader.getColumnFormat(tagset));
    }

    public RitterNERDataWriter(RitterNERDataReader.Tagset tagset, String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
        super(RitterNERDataReader.getColumnFormat(tagset), columnDelimiter, tokenDelimiter, sentenceDelimiter);
    }
}
