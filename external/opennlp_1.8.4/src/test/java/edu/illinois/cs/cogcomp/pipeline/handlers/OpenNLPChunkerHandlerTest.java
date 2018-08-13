/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.io.IOUtils;

/**
 * @author Xiaotian Le
 */
public class OpenNLPChunkerHandlerTest {

    // Please change MODEL_FILE if your model file is stored elsewhere
    public static final String MODEL_FILE = "/shared/experiments/xle2/models/open-nlp/en-chunker.bin";

    public static final List<String[]> RAW_TEXT = Arrays.asList(
            new String[] {"Rockwell", "International", "Corp.", "'s", "Tulsa", "unit", "said", "it", "signed", "a", "tentative", "agreement", "extending", "its", "contract", "with", "Boeing", "Co.", "to", "provide", "structural", "parts", "for", "Boeing", "'s", "747", "jetliners", "."},
            new String[] {"Rockwell", "said", "the", "agreement", "calls", "for", "it", "to", "supply", "200", "additional", "so-called", "shipsets", "for", "the", "planes", "."}
    );
    public static final String[] GOLD_POS = new String[] {
            "NNP", "NNP", "NNP", "POS", "NNP", "NN", "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN", "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS", ".",
            "NNP", "VBD", "DT", "NN", "VBZ", "IN", "PRP", "TO", "VB", "CD", "JJ", "JJ", "NNS", "IN", "DT", "NNS", "."
    };
    public static final String[] EXPECTED_CHUNKS = new String[] {
            "NP", "NP", "VP", "NP", "VP", "NP", "VP", "NP", "PP", "NP", "VP", "NP", "PP", "NP", "NP",
            "NP", "VP", "NP", "VP", "SBAR", "NP", "VP", "NP", "PP", "NP"
    };

    @Test
    public void testGetView() throws AnnotatorException {
        if (IOUtils.exists(MODEL_FILE)) {
            Annotator annotator = new OpenNLPChunkerHandler(MODEL_FILE, false);
            TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(RAW_TEXT);

            TokenLabelView view = new TokenLabelView(ViewNames.POS, "", ta, 1.0);
            for (int i = 0; i < GOLD_POS.length; ++i) {
                view.addTokenLabel(i, GOLD_POS[i], 1.0);
            }
            ta.addView(ViewNames.POS, view);

            annotator.getView(ta);
            Assert.assertTrue(ta.hasView(ViewNames.SHALLOW_PARSE));

            String[] chunks = ta.getView(ViewNames.SHALLOW_PARSE).getConstituents().stream().map(Constituent::getLabel).toArray(String[]::new);
            Assert.assertArrayEquals(EXPECTED_CHUNKS, chunks);
        }
    }
}
