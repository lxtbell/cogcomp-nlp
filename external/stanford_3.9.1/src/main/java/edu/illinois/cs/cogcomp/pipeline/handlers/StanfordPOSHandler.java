/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.util.Properties;
import java.util.function.Function;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;

/**
 * @author Xiaotian Le
 */
public class StanfordPOSHandler extends edu.illinois.cs.cogcomp.annotation.Annotator {

    public static final String VIEW_NAME = ViewNames.POS;
    public static final Class<? extends CoreAnnotation<String>> STANFORD_CLASS_NAME = CoreAnnotations.PartOfSpeechAnnotation.class;

    private Annotator annotator;

    public StanfordPOSHandler() {
        this(true);
    }

    public StanfordPOSHandler(boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE}, isLazilyInitialized);
    }

    @Override
    public void initialize(ResourceManager rm) {
        annotator = StanfordAnnotationUtil.getDefaultAnnotator(new Properties(), Annotator.STANFORD_POS);
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        Annotation document = StanfordTokenizationUtil.convertToStanfordAnnotation(ta);

        annotator.annotate(document);

        ta.addView(VIEW_NAME, toTextAnnotationView(document, ta));
    }

    public static TokenLabelView toTextAnnotationView(Annotation source, TextAnnotation target) {
        return StanfordAnnotationUtil.toTokenLabelView(
                source, STANFORD_CLASS_NAME,
                target, VIEW_NAME,
                Function.identity());
    }

    public static void convertFromTextAnnotation(TextAnnotation source, Annotation target) {
        StanfordAnnotationUtil.convertTokenLabel(
                source, VIEW_NAME,
                target, STANFORD_CLASS_NAME,
                Function.identity());
    }
}
