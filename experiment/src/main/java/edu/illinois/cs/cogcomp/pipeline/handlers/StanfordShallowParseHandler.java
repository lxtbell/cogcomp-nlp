/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TreeView;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;

/**
 * @author Xiaotian Le
 */
public class StanfordShallowParseHandler extends edu.illinois.cs.cogcomp.annotation.Annotator {

    public static final String VIEW_NAME = ViewNames.SHALLOW_PARSE;

    public static final String VALID_LABELS_KEY = "StanfordShallowParseHandler.validLabels";
    public static final String VALID_LABELS_DEFAULT = "NP,VP,PP,ADVP,SBAR,ADJP,PRT,CONJP,INTJ,LST,UCP";

    private edu.stanford.nlp.pipeline.Annotator annotator;
    private Set<String> validLabels;

    public StanfordShallowParseHandler() {
        this(true);
    }

    public StanfordShallowParseHandler(boolean isLazilyInitialized) {
        this(isLazilyInitialized, new ResourceManager(new Properties()));
    }

    public StanfordShallowParseHandler(boolean isLazilyInitialized, ResourceManager rm) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE, ViewNames.POS}, isLazilyInitialized, rm);
    }

    @Override
    public void initialize(ResourceManager rm) {
        annotator = StanfordAnnotationUtil.getDefaultAnnotator(new Properties(), Annotator.STANFORD_PARSE);
        validLabels = Arrays.stream(rm.getString(VALID_LABELS_KEY, VALID_LABELS_DEFAULT).split(",")).collect(Collectors.toSet());
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        Annotation document = StanfordTokenizationUtil.convertToStanfordAnnotation(ta);
        StanfordPOSHandler.convertFromTextAnnotation(ta, document);

        annotator.annotate(document);

        ta.addView(VIEW_NAME, convertToTextAnnotationView(document, ta, validLabels));
    }

    public static SpanLabelView convertToTextAnnotationView(Annotation source, TextAnnotation target, Set<String> validLabels) {
        TreeView treeView = StanfordParse391Handler.toTextAnnotationView(source, target);

        SpanLabelView spanLabelView = treeView.toSpanLabelView("", "", target, 1.0);

        SpanLabelView spanLabelViewFiltered = new SpanLabelView(VIEW_NAME, StanfordShallowParseHandler.class.getSimpleName(), target, 1.0, true);

        spanLabelView.getConstituents().stream()
                .filter(constituent -> validLabels.contains(constituent.getLabel()))
                .forEach(constituent ->
                        spanLabelViewFiltered.addSpanLabel(
                                constituent.getStartSpan(),
                                constituent.getEndSpan(),
                                constituent.getLabel(),
                                constituent.getConstituentScore()
                        )
                );

        return spanLabelViewFiltered;
    }

    public static void convertFromTextAnnotation(TextAnnotation source, Annotation target) {
        // TODO
    }
}
