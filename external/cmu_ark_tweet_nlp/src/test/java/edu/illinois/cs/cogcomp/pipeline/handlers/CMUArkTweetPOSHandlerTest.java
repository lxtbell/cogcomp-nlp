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

import java.util.Collections;
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
public class CMUArkTweetPOSHandlerTest {

    // Please change MODEL_FILE if your model file is stored elsewhere
    public static final String MODEL_FILE = "/shared/experiments/xle2/models/cmu-ark-tweet-nlp/model.20120919";

    public static final List<String[]> RAW_TEXT = Collections.singletonList(new String[] {
            "ikr", "smh", "he", "asked", "fir", "yo", "last", "name", "so", "he", "can", "add", "u", "on", "fb", "lololol"
    });
    public static final String[] EXPECTED_POS = new String[] {
            "!", "G", "O", "V", "P", "D", "A", "N", "P", "O", "V", "V", "O", "P", "^", "!"
    };

    @Test
    public void testGetView() throws AnnotatorException {
        if (IOUtils.exists(MODEL_FILE)) {
            Annotator annotator = new CMUArkTweetPOSHandler(MODEL_FILE, false);
            TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(RAW_TEXT);

            annotator.getView(ta);
            Assert.assertTrue(ta.hasView(ViewNames.POS));

            String[] pos = ta.getView(ViewNames.POS).getConstituents().stream().map(Constituent::getLabel).toArray(String[]::new);
            Assert.assertArrayEquals(EXPECTED_POS, pos);
        }
    }
}
