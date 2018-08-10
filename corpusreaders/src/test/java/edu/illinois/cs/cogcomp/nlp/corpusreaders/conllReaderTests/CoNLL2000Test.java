/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReaderTests;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.AnnotationReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Writer;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

public class CoNLL2000Test {

    private static final String CORPUS_DIRECTORY = "src/test/resources/edu/illinois/cs/cogcomp/nlp/corpusreaders/conll2000";

    @BeforeClass
    public static void setup() {
        Logger.getLogger(ColumnFormatGenericReader.class).setLevel(Level.INFO);
    }

    @Test
    public void testCreateTextAnnotation() throws Exception {
        AnnotationReader<TextAnnotation> reader = new CoNLL2000Reader(CORPUS_DIRECTORY);

        Assert.assertTrue(reader.hasNext());
        TextAnnotation ta = reader.next();
        Assert.assertFalse(reader.hasNext());
        System.out.print(reader.generateReport());

        List<Constituent> sentences = ta.getView(ViewNames.SENTENCE).getConstituents();
        Assert.assertEquals(sentences.size(), 2);
        Assert.assertEquals(sentences.get(1).getStartSpan(), 28);
        Assert.assertEquals(sentences.get(1).getEndSpan(), 45);

        List<Constituent> pos = ta.getView(ViewNames.POS).getConstituents();
        Assert.assertEquals(pos.size(), 45);
        Assert.assertEquals(pos.get(1).getLabel(), "NNP");

        List<Constituent> shallowParse = ta.getView(ViewNames.SHALLOW_PARSE).getConstituents();
        Assert.assertEquals(shallowParse.size(), 25);
        Assert.assertEquals(shallowParse.get(1).getStartSpan(), 3);
        Assert.assertEquals(shallowParse.get(1).getEndSpan(), 6);
        Assert.assertEquals(shallowParse.get(1).getLabel(), "NP");

        File tempFile = File.createTempFile(CoNLL2000Test.class.getSimpleName() + "-", ".txt");
        tempFile.deleteOnExit();
        ColumnFormatGenericWriter writer = new CoNLL2000Writer();
        writer.write(ta, tempFile.toString());

        try (Stream<String> stream = Files.lines(tempFile.toPath())) {
            List<String> lines = stream.limit(50).collect(Collectors.toList());
            Assert.assertEquals(lines.get(1), "International NNP I-NP");
            Assert.assertEquals(lines.get(28), "");
            Assert.assertEquals(lines.get(29), "Rockwell NNP B-NP");
        }
    }
}
