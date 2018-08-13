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
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

/**
 * @author Xiaotian Le
 */
public class OpenNLPChunkerHandler extends Annotator {

    public static final String VIEW_NAME = ViewNames.SHALLOW_PARSE;

    private final String modelPath;

    private ChunkerME chunker;

    public OpenNLPChunkerHandler(String modelPath) {
        this(modelPath, true);
    }

    public OpenNLPChunkerHandler(String modelPath, boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE, ViewNames.POS}, true);

        this.modelPath = modelPath;
        if (!isLazilyInitialized) {
            doInitialize();
        }
    }

    @Override
    public void initialize(ResourceManager rm) {
        try {
            ChunkerModel model = new ChunkerModel(new File(modelPath));
            chunker = new ChunkerME(model);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        TokenLabelView pos = (TokenLabelView) ta.getView(ViewNames.POS);
        SpanLabelView view = new SpanLabelView(VIEW_NAME, OpenNLPChunkerHandler.class.getSimpleName(), ta, 1.0);

        for (Constituent sentence : ta.getView(ViewNames.SENTENCE).getConstituents()) {
            String[] tokens = ta.getTokensInSpan(sentence.getStartSpan(), sentence.getEndSpan());
            String[] tags = pos.getLabelsCoveringSpan(sentence.getStartSpan(), sentence.getEndSpan()).toArray(new String[0]);
            Span[] chunks = chunker.chunkAsSpans(tokens, tags);
            for (Span chunk : chunks) {
                view.addSpanLabel(sentence.getStartSpan() + chunk.getStart(), sentence.getStartSpan() + chunk.getEnd(), chunk.getType(), chunk.getProb());
            }
        }

        ta.addView(VIEW_NAME, view);
    }
}
