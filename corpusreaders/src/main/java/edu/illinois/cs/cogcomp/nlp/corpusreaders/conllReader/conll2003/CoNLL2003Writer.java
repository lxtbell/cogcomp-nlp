/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

/**
 * @author Xiaotian Le
 */
public class CoNLL2003Writer extends ColumnFormatGenericWriter {

    public CoNLL2003Writer(CoNLL2003Reader.Tagset tagset) {
        super(CoNLL2003Reader.getColumnFormat(tagset));
    }

    public CoNLL2003Writer(CoNLL2003Reader.Tagset tagset, String columnDelimiter, String tokenDelimiter, String sentenceDelimiter) {
        super(CoNLL2003Reader.getColumnFormat(tagset), columnDelimiter, tokenDelimiter, sentenceDelimiter);
    }
}
