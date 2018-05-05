/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader;

/**
 * @author Xiaotian Le
 */
public class CoNLL2000Writer extends ColumnFormatGenericWriter {

    public CoNLL2000Writer() {
        super(CoNLL2000Reader.getColumnFormat());
    }

    public CoNLL2000Writer(String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
        super(CoNLL2000Reader.getColumnFormat(), columnDelimiter, tokenDelimiter, sentenceDelimiter);
    }
}
