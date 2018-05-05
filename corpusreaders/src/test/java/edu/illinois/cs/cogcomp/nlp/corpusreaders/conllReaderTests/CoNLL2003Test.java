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
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.CoNLL2003Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.CoNLL2003Writer;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;

/**
 * Please change CORPUS_DIRECTORY if your CoNLL 2003 (test.a.eng, test.b.eng, train.eng) files are stored elsewhere
 */
public class CoNLL2003Test {

    private static final String CORPUS_DIRECTORY = "src/test/resources/edu/illinois/cs/cogcomp/nlp/corpusreaders/conll2003";

    @BeforeClass
    public static void setup() {
        Logger.getLogger(ColumnFormatGenericReader.class).setLevel(Level.INFO);
    }

    @Test
    public void testCreateTextAnnotation() throws Exception {
        AnnotationReader<TextAnnotation> cnr = new CoNLL2003Reader(CORPUS_DIRECTORY);

        Assert.assertTrue(cnr.hasNext());
        TextAnnotation ta = cnr.next();
        Assert.assertFalse(cnr.hasNext());
        System.out.print(cnr.generateReport());

        List<Constituent> sentences = ta.getView(ViewNames.SENTENCE).getConstituents();
        Assert.assertEquals(sentences.size(), 3);
        Assert.assertEquals(sentences.get(1).getStartSpan(), 11);
        Assert.assertEquals(sentences.get(1).getEndSpan(), 13);

        List<Constituent> pos = ta.getView(ViewNames.POS).getConstituents();
        Assert.assertEquals(pos.size(), 48);
        Assert.assertEquals(pos.get(2).getLabel(), "NNP");

        List<Constituent> shallowParse = ta.getView(ViewNames.SHALLOW_PARSE).getConstituents();
        Assert.assertEquals(shallowParse.size(), 26);
        Assert.assertEquals(shallowParse.get(1).getStartSpan(), 2);
        Assert.assertEquals(shallowParse.get(1).getEndSpan(), 4);
        Assert.assertEquals(shallowParse.get(1).getLabel(), "NP");

        List<Constituent> ner = ta.getView(ViewNames.NER_CONLL).getConstituents();
        Assert.assertEquals(ner.size(), 6);
        Assert.assertEquals(ner.get(1).getStartSpan(), 11);
        Assert.assertEquals(ner.get(1).getEndSpan(), 12);
        Assert.assertEquals(ner.get(1).getLabel(), "LOC");

        File tempFile = File.createTempFile(CoNLL2003Test.class.getSimpleName() + "-", ".txt");
        tempFile.deleteOnExit();
        ColumnFormatGenericWriter writer = new CoNLL2003Writer();
        writer.write(ta, tempFile.toString());

        try (Stream<String> stream = Files.lines(tempFile.toPath())) {
            List<String> lines = stream.limit(50).collect(Collectors.toList());
            Assert.assertEquals(lines.get(2), "LEICESTERSHIRE NNP B-NP B-ORG");
            Assert.assertEquals(lines.get(11), "");
            Assert.assertEquals(lines.get(12), "LONDON NNP B-NP B-LOC");
        }
    }
}
