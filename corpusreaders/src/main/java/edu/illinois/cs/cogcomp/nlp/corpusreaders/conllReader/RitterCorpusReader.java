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
public class RitterCorpusReader {

    public static class RitterPOSDataReader extends ColumnFormatGenericReader {

        public static final String CORPUS_ID = "Ritter et al. 2011. POS";

        private static final int COLUMN_TOKEN = 0;
        private static final int COLUMN_POS = 1;

        public RitterPOSDataReader() {
            super(getColumnFormat(), CORPUS_ID);
        }

        public RitterPOSDataReader(String corpusDirectory) {
            super(getColumnFormat(), CORPUS_ID, corpusDirectory, "pos.txt");
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

    public static class RitterChunkDataReader extends ColumnFormatGenericReader {

        public static final String CORPUS_ID = "Ritter et al. 2011. Chunk";

        private static final int COLUMN_TOKEN = 0;
        private static final int COLUMN_CHUNK = 1;

        public RitterChunkDataReader() {
            super(getColumnFormat(), CORPUS_ID);
        }

        public RitterChunkDataReader(String corpusDirectory) {
            super(getColumnFormat(), CORPUS_ID, corpusDirectory, "chunk.txt");
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

    public static class RitterNERDataReader extends ColumnFormatGenericReader {

        public static final String CORPUS_ID = "Ritter et al. 2011. NER";

        private static final int COLUMN_TOKEN = 0;
        private static final int COLUMN_NER = 1;

        public RitterNERDataReader() {
            super(getColumnFormat(), CORPUS_ID);
        }

        public RitterNERDataReader(String corpusDirectory) {
            super(getColumnFormat(), CORPUS_ID, corpusDirectory, "ner.txt");
        }

        public static ColumnFormat getColumnFormat() {
            ColumnFormat format = new ColumnFormat();

            format.tokenColumn = COLUMN_TOKEN;
            format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_ONTONOTES, "O", String::toUpperCase, String::toLowerCase));

            format.columnDelimiter = "\t";
            format.iobCanStartWithI = false;

            return format;
        }
    }
}
