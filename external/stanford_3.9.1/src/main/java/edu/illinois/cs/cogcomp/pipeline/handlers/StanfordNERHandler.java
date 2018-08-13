/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.util.List;
import java.util.Properties;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;

/**
 * @author Xiaotian Le
 */
public class StanfordNERHandler extends edu.illinois.cs.cogcomp.annotation.Annotator {

    public static final String VIEW_NAME = ViewNames.NER_ONTONOTES;
    public static final Class<? extends CoreAnnotation<String>> STANFORD_CLASS_NAME = CoreAnnotations.NamedEntityTagAnnotation.class;

    private Annotator annotator;

    public StanfordNERHandler() {
        this(true);
    }

    public StanfordNERHandler(boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE, ViewNames.POS, ViewNames.LEMMA}, isLazilyInitialized);
    }

    @Override
    public void initialize(ResourceManager rm) {
        Properties props = new Properties();
        props.setProperty("nthreads", String.valueOf(Runtime.getRuntime().availableProcessors()));
        annotator = StanfordAnnotationUtil.getDefaultAnnotator(props, Annotator.STANFORD_NER);
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        Annotation document = StanfordTokenizationUtil.convertToStanfordAnnotation(ta);
        StanfordPOSHandler.convertFromTextAnnotation(ta, document);
        StanfordLemmaHandler.convertFromTextAnnotation(ta, document);

        annotator.annotate(document);

        ta.addView(VIEW_NAME, toTextAnnotationView(document, ta));
    }

    public static SpanLabelView toTextAnnotationView(Annotation source, TextAnnotation target) {
        List<CoreLabel> tokens = source.get(CoreAnnotations.TokensAnnotation.class);

        SpanLabelView view = new SpanLabelView(VIEW_NAME, StanfordNERHandler.class.getSimpleName(), target, 1.0);

        for (int i = 0; i < tokens.size(); ++i) {
            String sourceLabel = tokens.get(i).get(STANFORD_CLASS_NAME);

            if (sourceLabel != null) {
                String label = processStanfordLabel(sourceLabel);

                if (label != null) {
                    view.addSpanLabel(i, i + 1, label, 1.0);
                }
            }
        }

        return view;
    }

    public static void convertFromTextAnnotation(TextAnnotation source, Annotation target) {
        // TODO
    }

    public static String processStanfordLabel(String source) {
        switch (source) {
            case "O":
                return null;
            default:
                return source;
        }
    }
}
