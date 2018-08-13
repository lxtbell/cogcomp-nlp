/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cmu.arktweetnlp.impl.Model;
import cmu.arktweetnlp.impl.ModelSentence;
import cmu.arktweetnlp.impl.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * @author Xiaotian Le
 */
public class CMUArkTweetPOSHandler extends Annotator {

    public static final String VIEW_NAME = ViewNames.POS;

    private final String modelPath;

    private Model model;
    private FeatureExtractor featureExtractor;

    public CMUArkTweetPOSHandler(String modelPath) {
        this(modelPath, true);
    }

    public CMUArkTweetPOSHandler(String modelPath, boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE}, true);

        this.modelPath = modelPath;
        if (!isLazilyInitialized) {
            doInitialize();
        }
    }

    @Override
    public void initialize(ResourceManager rm) {
        try {
            model = Model.loadModelFromText(modelPath);
            featureExtractor = new FeatureExtractor(model, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        TokenLabelView view = new TokenLabelView(VIEW_NAME, CMUArkTweetPOSHandler.class.getSimpleName(), ta, 1.0);

        for (Constituent sentence : ta.getView(ViewNames.SENTENCE).getConstituents()) {
            List<String> words = Arrays.asList(ta.getTokensInSpan(sentence.getStartSpan(), sentence.getEndSpan()));
            List<String> tags = tagSentence(words);

            for (int i = 0; i < tags.size(); ++i) {
                view.addTokenLabel(sentence.getStartSpan() + i, tags.get(i), 1.0);
            }
        }

        ta.addView(VIEW_NAME, view);
    }

    private List<String> tagSentence(List<String> tokens) {
        cmu.arktweetnlp.impl.Sentence sentence = new cmu.arktweetnlp.impl.Sentence();
        sentence.tokens = tokens;

        ModelSentence ms = new ModelSentence(sentence.T());
        featureExtractor.computeFeatures(sentence, ms);
        model.greedyDecode(ms, false);

        return Arrays.stream(ms.labels)
                .mapToObj(label -> model.labelVocab.name(label))
                .collect(Collectors.toList());
    }
}
