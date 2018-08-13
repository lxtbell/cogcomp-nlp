/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.tokenizer;

import java.util.Properties;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.tokenizer.Tokenizer;
import edu.illinois.cs.cogcomp.pipeline.handlers.StanfordTokenizationUtil;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author Xiaotian Le
 */
public class StanfordTokenizer implements TextAnnotationBuilder {

    StanfordCoreNLP pipeline;

    public StanfordTokenizer() {
        Properties props = new Properties();
        props.setProperty("annotators", Annotator.STANFORD_TOKENIZE + "," + Annotator.STANFORD_SSPLIT);
        this.pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public TextAnnotation createTextAnnotation(String text) throws IllegalArgumentException {
        return createTextAnnotation("", "", text);
    }

    @Override
    public TextAnnotation createTextAnnotation(String corpusId, String textId, String text) throws IllegalArgumentException {
        Annotation document = new Annotation(text);

        pipeline.annotate(document);

        return StanfordTokenizationUtil.convertToTextAnnotation(corpusId, textId, document);
    }

    @Override
    public TextAnnotation createTextAnnotation(
            String corpusId, String textId, String text,
            Tokenizer.Tokenization tokenization) throws IllegalArgumentException {
        throw new IllegalArgumentException(
                "Cannot create annotation from Tokenization using StanfordTokenizer");
    }
}
