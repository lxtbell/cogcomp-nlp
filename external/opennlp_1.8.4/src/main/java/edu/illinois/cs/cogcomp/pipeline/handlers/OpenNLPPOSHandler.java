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
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

/**
 * @author Xiaotian Le
 */
public class OpenNLPPOSHandler extends Annotator {

    public static final String VIEW_NAME = ViewNames.POS;

    private final String modelPath;

    private POSTaggerME pos;

    public OpenNLPPOSHandler(String modelPath) {
        this(modelPath, true);
    }

    public OpenNLPPOSHandler(String modelPath, boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE}, true);

        this.modelPath = modelPath;
        if (!isLazilyInitialized) {
            doInitialize();
        }
    }

    @Override
    public void initialize(ResourceManager rm) {
        try {
            POSModel model = new POSModel(new File(modelPath));
            pos = new POSTaggerME(model);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        TokenLabelView view = new TokenLabelView(VIEW_NAME, OpenNLPPOSHandler.class.getSimpleName(), ta, 1.0);

        for (Constituent sentence : ta.getView(ViewNames.SENTENCE).getConstituents()) {
            String[] tokens = ta.getTokensInSpan(sentence.getStartSpan(), sentence.getEndSpan());
            String[] tags = pos.tag(tokens);
            double[] probs = pos.probs();
            for (int i = 0; i < tokens.length; ++i) {
                view.addTokenLabel(sentence.getStartSpan() + i, tags[i], probs[i]);
            }
        }

        ta.addView(VIEW_NAME, view);
    }
}
