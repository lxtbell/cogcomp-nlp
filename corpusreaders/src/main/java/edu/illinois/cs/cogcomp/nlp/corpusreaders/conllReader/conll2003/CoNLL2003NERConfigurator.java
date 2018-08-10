/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003;

/**
 * Defines the CoNLL 2003 NER tags
 *
 * @author Xiaotian Le
 */
public class CoNLL2003NERConfigurator {

    public enum Tags {
        PERSON("PER"), LOCATION("LOC"), ORGANIZATION("ORG"), MISC("MISC");

        public String label;
        Tags(String label) {
            this.label = label;
        }
    }
}
