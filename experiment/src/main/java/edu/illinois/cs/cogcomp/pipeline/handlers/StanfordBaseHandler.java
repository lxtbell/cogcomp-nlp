/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.nio.file.Paths;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.MascXCESReader;

// TODO Change to an converter interface
/**
 * @author Xiaotian Le
 */
public abstract class StanfordBaseHandler  {

    public static void main(String[] args) throws Exception {
        String corpusDirectory = "/shared/corpora/corporaWeb/written/eng/MASC-3.0.0/xces-fixed-small/written/technical";
        String outputDirectory = "E:\\Files\\Lectures\\CS499\\MASC\\JSON\\original_gold";
        String stanfordOutputDirectory = "E:\\Files\\Lectures\\CS499\\MASC\\JSON\\original_stanford";
        MascXCESReader reader = new MascXCESReader("MASC-3.0.0", corpusDirectory, ".xml");

        StanfordPOSHandler pos = new StanfordPOSHandler();

        StanfordLemmaHandler lemma = new StanfordLemmaHandler();

        StanfordNERHandler ner = new StanfordNERHandler();

        StanfordParse391Handler parse = new StanfordParse391Handler();

        StanfordShallowParseHandler shallow = new StanfordShallowParseHandler();

        for (TextAnnotation ta : reader) {
            SerializationHelper.serializeTextAnnotationToFile(ta, Paths.get(outputDirectory, ta.getId() + ".json").toString(), true, true);

            ta.removeView(ViewNames.POS);
            ta.removeView(ViewNames.LEMMA);
            ta.removeView(ViewNames.NER_CONLL);
            ta.removeView(ViewNames.NER_ONTONOTES);
            ta.removeView(ViewNames.SHALLOW_PARSE);

            pos.getView(ta);
            lemma.getView(ta);
            ner.getView(ta);
            parse.getView(ta);
            shallow.getView(ta);

            SerializationHelper.serializeTextAnnotationToFile(ta, Paths.get(stanfordOutputDirectory, ta.getId() + ".json").toString(), true, true);
        }
    }
}
