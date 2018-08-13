/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.io.File;
import java.io.IOException;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 * @author Xiaotian Le
 */
public class OpenNLPNERHandler extends Annotator {

    public static final String VIEW_NAME = ViewNames.NER_ONTONOTES;

    private final String modelPath;

    private NameFinderME nameFinder;

    public OpenNLPNERHandler(String modelPath) {
        this(modelPath, true);
    }

    public OpenNLPNERHandler(String modelPath, boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE}, true);

        this.modelPath = modelPath;
        if (!isLazilyInitialized) {
            doInitialize();
        }
    }

    @Override
    public void initialize(ResourceManager rm) {
        try {
            TokenNameFinderModel model = new TokenNameFinderModel(new File(modelPath));
            nameFinder = new NameFinderME(model);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        SpanLabelView view = new SpanLabelView(VIEW_NAME, OpenNLPNERHandler.class.getSimpleName(), ta, 1.0);

        for (Constituent sentence : ta.getView(ViewNames.SENTENCE).getConstituents()) {
            String[] tokens = ta.getTokensInSpan(sentence.getStartSpan(), sentence.getEndSpan());
            Span[] nameSpans = nameFinder.find(tokens);
            for (Span nameSpan : nameSpans) {
                view.addSpanLabel(sentence.getStartSpan() + nameSpan.getStart(), sentence.getStartSpan() + nameSpan.getEnd(), nameSpan.getType(), nameSpan.getProb());
            }
        }

        ta.addView(VIEW_NAME, view);
    }
}
