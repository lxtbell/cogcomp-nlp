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
public class OpenNLPNERHandlerTest {

    // Please change MODEL_FILE if your model file is stored elsewhere
    public static final String MODEL_FILE = "/shared/experiments/xle2/models/open-nlp/en-ner-person.bin";

    public static final List<String[]> RAW_TEXT = Arrays.asList(
            new String[] {"Pierre", "Vinken", ",", "61", "years", "old", ",", "will", "join", "the", "board", "as", "a", "nonexecutive", "director", "Nov.", "29", "."},
            new String[] {"Mr", ".", "Vinken", "is", "chairman", "of", "Elsevier", "N.V.", ",", "the", "Dutch", "publishing", "group", "."},
            new String[] {"Rudolph", "Agnew", ",", "55", "years", "old", "and", "former", "chairman", "of", "Consolidated", "Gold", "Fields", "PLC", ",", "was", "named", "a", "director", "of", "this", "British", "industrial", "conglomerate", "."}
    );

    @Test
    public void testGetView() throws AnnotatorException {
        if (IOUtils.exists(MODEL_FILE)) {
            Annotator annotator = new OpenNLPNERHandler(MODEL_FILE, false);
            TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(RAW_TEXT);

            annotator.getView(ta);
            Assert.assertTrue(ta.hasView(ViewNames.NER_ONTONOTES));

            List<Constituent> entities = ta.getView(ViewNames.NER_ONTONOTES).getConstituents();
            Assert.assertEquals(entities.size(), 3);
        }
    }
}
