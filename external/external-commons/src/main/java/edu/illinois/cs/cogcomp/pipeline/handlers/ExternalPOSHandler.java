/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormat;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

/**
 * @author Xiaotian Le
 */
public class ExternalPOSHandler extends Annotator {

    public static final String VIEW_NAME = ViewNames.POS;

    protected String commandLine;

    public ExternalPOSHandler(String commandLine) {
        this(commandLine, true);
    }

    public ExternalPOSHandler(String commandLine, boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{}, isLazilyInitialized);

        this.commandLine = commandLine;
    }

    @Override
    public void initialize(ResourceManager rm) {}

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        ColumnFormatGenericWriter writer = new SimpleTokenWriter();
        ColumnFormatGenericReader reader = new SimplePOSReader();
        List<TextAnnotation> results = new ArrayList<>();

        int exitValue = IOUtils.exec(new String[] {"bash", "-c", commandLine}, null, null,
                stdin -> writer.write(ta, stdin),
                stdout -> results.addAll(reader.readFromStream(stdout, "")),
                stderr -> IOUtils.pipe(stderr, System.err));

        if (exitValue != 0) {
            throw new AnnotatorException("Command line exited with code " + exitValue);
        }

        ta.addView(VIEW_NAME, results.get(0).getView(VIEW_NAME));
    }

    public static class SimplePOSReader extends ColumnFormatGenericReader {

        public static final String CORPUS_ID = "";

        private static final int COLUMN_TOKEN = 0;
        private static final int COLUMN_POS = 1;

        public SimplePOSReader() {
            super(getColumnFormat(), CORPUS_ID);
        }

        public SimplePOSReader(String corpusDirectory, String fileExtension) {
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

    public static class SimpleTokenWriter extends ColumnFormatGenericWriter {

        private static final int COLUMN_TOKEN = 0;

        public SimpleTokenWriter() {
            super(getColumnFormat());
        }

        public SimpleTokenWriter(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
            super(getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
        }

        public static ColumnFormat getColumnFormat() {
            ColumnFormat format = new ColumnFormat();

            format.tokenColumn = COLUMN_TOKEN;

            format.columnDelimiter = " ";
            format.iobCanStartWithI = false;

            return format;
        }
    }
}
