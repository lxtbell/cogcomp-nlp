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
import java.util.function.Function;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.AnnotatorImplementations;
import edu.stanford.nlp.pipeline.AnnotatorPool;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author Xiaotian Le
 */
public abstract class StanfordAnnotationUtil {

    // TODO Convert across different tokenizations
    public static TokenLabelView toTokenLabelView(
            Annotation source, Class<? extends CoreAnnotation<String>> className,
            TextAnnotation target, String viewName,
            Function<String, String> processor) {
        List<CoreLabel> tokens = source.get(CoreAnnotations.TokensAnnotation.class);

        TokenLabelView view = new TokenLabelView(viewName, "StanfordAnnotationUtil", target, 1.0);

        for (int i = 0; i < tokens.size(); ++i) {
            String sourceLabel = tokens.get(i).get(className);

            if (sourceLabel != null) {
                String label = processor.apply(sourceLabel);

                if (label != null) {
                    view.addTokenLabel(i, label, 1.0);
                }
            }
        }

        return view;
    }

    // TODO Convert across different tokenizations
    public static void convertTokenLabel(
            TextAnnotation source, String viewName,
            Annotation target, Class<? extends CoreAnnotation<String>> className,
            Function<String, String> processor) {
        View view = source.getView(viewName);

        List<CoreLabel> tokens = target.get(CoreAnnotations.TokensAnnotation.class);
        for (int i = 0; i < tokens.size(); ++i) {
            List<String> sourceLabels = view.getLabelsCoveringToken(i);

            if (sourceLabels.size() == 1) {
                String label = processor.apply(sourceLabels.get(0));

                if (label != null) {
                    tokens.get(i).set(className, label);
                }
            }
        }
    }

    public static Annotator getDefaultAnnotator(Properties props, String annotator) {
        AnnotatorPool pool = StanfordCoreNLP.getDefaultAnnotatorPool(props, new AnnotatorImplementations());
        return pool.get(annotator);
    }
}
