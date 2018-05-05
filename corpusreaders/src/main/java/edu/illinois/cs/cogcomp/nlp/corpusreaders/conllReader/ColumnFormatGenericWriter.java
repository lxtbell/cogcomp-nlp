/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * @author Xiaotian Le
 */
public class ColumnFormatGenericWriter {

    protected ColumnFormat format;
    protected String columnDelimiter;
    protected String tokenDelimiter;
    protected String sentenceDelimiter;

    public ColumnFormatGenericWriter(ColumnFormat format) {
        this(format, format.columnDelimiter, "\n", "\n\n");
    }

    public ColumnFormatGenericWriter(ColumnFormat format, String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
        this.format = format;
        this.columnDelimiter = columnDelimiter;
        this.tokenDelimiter = tokenDelimiter;
        this.sentenceDelimiter = sentenceDelimiter;
    }

    public void write(TextAnnotation ta, String outputFile) {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            write(ta, stream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(TextAnnotation ta, OutputStream outputStream) {
        write(ta, new BufferedWriter(new OutputStreamWriter(outputStream)));
    }

    public void write(TextAnnotation ta, Writer outputWriter) {
        List<String[]> tokensWithLabel = new ArrayList<>();
        for (String token : ta.getTokens()) {
            String[] tokenWithLabel = new String[format.tokenLabelColumns.size() + format.iobColumns.size() + 1];

            tokenWithLabel[format.tokenColumn] = token;
            for (ColumnFormat.Column column : format.tokenLabelColumns) {
                tokenWithLabel[column.columnNumber] = column.defaultLabel;
            }
            for (ColumnFormat.Column column : format.iobColumns) {
                tokenWithLabel[column.columnNumber] = column.defaultLabel;
            }

            tokensWithLabel.add(tokenWithLabel);
        }

        for (ColumnFormat.Column column : format.tokenLabelColumns) {
            if (ta.hasView(column.viewName)) {
                for (Constituent constituent : ta.getView(column.viewName)) {
                    String fileLabel = column.writeLabel.apply(constituent.getLabel());

                    if (fileLabel != null) {
                        tokensWithLabel.get(constituent.getStartSpan())[column.columnNumber] = fileLabel;
                    }
                }
            }

        }
        for (ColumnFormat.Column column : format.iobColumns) {
            if (ta.hasView(column.viewName)) {
                for (Constituent constituent : ta.getView(column.viewName)) {
                    String fileLabel = column.writeLabel.apply(constituent.getLabel());

                    if (fileLabel != null) {
                        tokensWithLabel.get(constituent.getStartSpan())[column.columnNumber] = "B-" + fileLabel;

                        for (int tokenId = constituent.getStartSpan() + 1; tokenId < constituent.getEndSpan(); ++tokenId) {
                            tokensWithLabel.get(tokenId)[column.columnNumber] = "I-" + fileLabel;
                        }
                    }
                }

            }
        }

        try {
            for (Constituent sentence : ta.getView(ViewNames.SENTENCE)) {
                List<String> sentenceTokens = new ArrayList<>();
                for (int tokenId = sentence.getStartSpan(); tokenId < sentence.getEndSpan(); ++tokenId) {
                    sentenceTokens.add(String.join(columnDelimiter, tokensWithLabel.get(tokenId)));
                }

                String sentenceOutput = String.join(tokenDelimiter, sentenceTokens) + sentenceDelimiter;
                outputWriter.write(sentenceOutput);
            }
            outputWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
