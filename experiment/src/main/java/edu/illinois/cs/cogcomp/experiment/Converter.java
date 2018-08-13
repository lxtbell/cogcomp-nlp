/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Reader;

/**
 * @author Xiaotian Le
 */
public class Converter {

    public static final String PREDICTION_DIRECTORY = IOUtil.getProjectFolder() + "data/predictions/";

    public static void main(String[] args) throws IOException {
        for (String file : IOUtil.lsFileAbsoluteRecursiveSorted(PREDICTION_DIRECTORY + "CogCompChunkerOnPOS", ".*display\\.conll")) {
            PrepareUtil.makeJson(file, new CoNLL2000Reader());
        }

        for (String file : IOUtil.lsFileAbsoluteRecursiveSorted(PREDICTION_DIRECTORY + "StanfordNEROnPOS", ".*display\\.conll")) {
            PrepareUtil.makeJson(file, new CoNLL2003Reader(CoNLL2003Reader.Tagset.CONLL));
        }
    }
}
