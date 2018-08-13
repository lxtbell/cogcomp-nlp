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
import edu.illinois.cs.cogcomp.core.io.IOUtils;

/**
 * @author Xiaotian Le
 */
public class OpenNLPPOSHandlerTest {

    // Please change MODEL_FILE if your model file is stored elsewhere
    public static final String MODEL_FILE = "/shared/experiments/xle2/models/open-nlp/en-pos-maxent.bin";

    public static final List<String[]> RAW_TEXT = Arrays.asList(
            new String[] {"Pierre", "Vinken", ",", "61", "years", "old", ",", "will", "join", "the", "board", "as", "a", "nonexecutive", "director", "Nov.", "29", "."},
            new String[] {"Mr.", "Vinken", "is", "chairman", "of", "Elsevier", "N.V.", ",", "the", "Dutch", "publishing", "group", "."}
    );
    public static final String[] EXPECTED_POS = new String[] {
            "NNP", "NNP", ",", "CD", "NNS", "JJ", ",", "MD", "VB", "DT", "NN", "IN", "DT", "JJ", "NN", "NNP", "CD", ".",
            "NNP", "NNP", "VBZ", "NN", "IN", "NNP", "NNP", ",", "DT", "JJ", "NN", "NN", "."
    };

    @Test
    public void testGetView() throws AnnotatorException {
        if (IOUtils.exists(MODEL_FILE)) {
            Annotator annotator = new OpenNLPPOSHandler(MODEL_FILE, false);
            TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(RAW_TEXT);

            annotator.getView(ta);
            Assert.assertTrue(ta.hasView(ViewNames.POS));

            String[] pos = ta.getView(ViewNames.POS).getConstituents().stream().map(Constituent::getLabel).toArray(String[]::new);
            Assert.assertArrayEquals(EXPECTED_POS, pos);
        }
    }
}
