/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader;

/*
 * @author Xiaotian Le
 */
public class RitterCorpusWriter {

    public static class RitterPOSDataWriter extends ColumnFormatGenericWriter {

        public RitterPOSDataWriter() {
            super(RitterCorpusReader.RitterPOSDataReader.getColumnFormat());
        }

        public RitterPOSDataWriter(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
            super(RitterCorpusReader.RitterPOSDataReader.getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
        }
    }

    public static class RitterChunkDataWriter extends ColumnFormatGenericWriter {

        public RitterChunkDataWriter() {
            super(RitterCorpusReader.RitterChunkDataReader.getColumnFormat());
        }

        public RitterChunkDataWriter(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
            super(RitterCorpusReader.RitterChunkDataReader.getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
        }
    }

    public static class RitterNERDataWriter extends ColumnFormatGenericWriter {

        public RitterNERDataWriter() {
            super(RitterCorpusReader.RitterNERDataReader.getColumnFormat());
        }

        public RitterNERDataWriter(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
            super(RitterCorpusReader.RitterNERDataReader.getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
        }
    }
}
