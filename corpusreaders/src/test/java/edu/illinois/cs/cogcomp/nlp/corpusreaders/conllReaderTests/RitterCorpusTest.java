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
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.RitterCorpusReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.RitterCorpusWriter;

/**
 * Please change CORPUS_DIRECTORY if your Ritter corpus (chunk.txt, ner.txt, pos.txt) files are stored elsewhere
 */
public class RitterCorpusTest {

    private static final String CORPUS_DIRECTORY = "src/test/resources/edu/illinois/cs/cogcomp/nlp/corpusreaders/ritter";

    @BeforeClass
    public static void setup() {
        Logger.getLogger(ColumnFormatGenericReader.class).setLevel(Level.INFO);
    }

    @Test
    public void testPOSDataReader() throws Exception {
        AnnotationReader<TextAnnotation> reader = new RitterCorpusReader.RitterPOSDataReader(CORPUS_DIRECTORY);

        Assert.assertTrue(reader.hasNext());
        TextAnnotation ta = reader.next();
        Assert.assertFalse(reader.hasNext());
        System.out.print(reader.generateReport());

        List<Constituent> sentences = ta.getView(ViewNames.SENTENCE).getConstituents();
        Assert.assertEquals(sentences.size(), 5);
        Assert.assertEquals(sentences.get(1).getStartSpan(), 27);
        Assert.assertEquals(sentences.get(1).getEndSpan(), 47);

        List<Constituent> pos = ta.getView(ViewNames.POS).getConstituents();
        Assert.assertEquals(pos.size(), 97);
        Assert.assertEquals(pos.get(1).getLabel(), "PRP");

        File tempFile = File.createTempFile(RitterCorpusTest.class.getSimpleName() + "-", ".txt");
        tempFile.deleteOnExit();
        ColumnFormatGenericWriter writer = new RitterCorpusWriter.RitterPOSDataWriter();
        writer.write(ta, tempFile.toString());

        try (Stream<String> stream = Files.lines(tempFile.toPath())) {
            List<String> lines = stream.limit(50).collect(Collectors.toList());
            Assert.assertEquals(lines.get(1), "It PRP");
            Assert.assertEquals(lines.get(27), "");
            Assert.assertEquals(lines.get(28), "Small JJ");
        }
    }

    @Test
    public void testChunkDataReader() throws Exception {
        AnnotationReader<TextAnnotation> reader = new RitterCorpusReader.RitterChunkDataReader(CORPUS_DIRECTORY);

        Assert.assertTrue(reader.hasNext());
        TextAnnotation ta = reader.next();
        Assert.assertFalse(reader.hasNext());
        System.out.print(reader.generateReport());

        List<Constituent> sentences = ta.getView(ViewNames.SENTENCE).getConstituents();
        Assert.assertEquals(sentences.size(), 5);
        Assert.assertEquals(sentences.get(1).getStartSpan(), 27);
        Assert.assertEquals(sentences.get(1).getEndSpan(), 47);

        List<Constituent> shallowParse = ta.getView(ViewNames.SHALLOW_PARSE).getConstituents();
        Assert.assertEquals(shallowParse.size(), 50);
        Assert.assertEquals(shallowParse.get(1).getStartSpan(), 2);
        Assert.assertEquals(shallowParse.get(1).getEndSpan(), 3);
        Assert.assertEquals(shallowParse.get(1).getLabel(), "VP");

        File tempFile = File.createTempFile(RitterCorpusTest.class.getSimpleName() + "-", ".txt");
        tempFile.deleteOnExit();
        ColumnFormatGenericWriter writer = new RitterCorpusWriter.RitterChunkDataWriter();
        writer.write(ta, tempFile.toString());

        try (Stream<String> stream = Files.lines(tempFile.toPath())) {
            List<String> lines = stream.limit(50).collect(Collectors.toList());
            Assert.assertEquals(lines.get(1), "It B-np");
            Assert.assertEquals(lines.get(27), "");
            Assert.assertEquals(lines.get(28), "Small B-np");
        }
    }

    @Test
    public void testNERDataReader() throws Exception {
        AnnotationReader<TextAnnotation> reader = new RitterCorpusReader.RitterNERDataReader(CORPUS_DIRECTORY);

        Assert.assertTrue(reader.hasNext());
        TextAnnotation ta = reader.next();
        Assert.assertFalse(reader.hasNext());
        System.out.print(reader.generateReport());

        List<Constituent> sentences = ta.getView(ViewNames.SENTENCE).getConstituents();
        Assert.assertEquals(sentences.size(), 6);
        Assert.assertEquals(sentences.get(1).getStartSpan(), 27);
        Assert.assertEquals(sentences.get(1).getEndSpan(), 42);

        List<Constituent> ner = ta.getView(ViewNames.NER_ONTONOTES).getConstituents();
        Assert.assertEquals(ner.size(), 7);
        Assert.assertEquals(ner.get(0).getStartSpan(), 14);
        Assert.assertEquals(ner.get(0).getEndSpan(), 17);
        Assert.assertEquals(ner.get(0).getLabel(), "FACILITY");

        File tempFile = File.createTempFile(RitterCorpusTest.class.getSimpleName() + "-", ".txt");
        tempFile.deleteOnExit();
        ColumnFormatGenericWriter writer = new RitterCorpusWriter.RitterNERDataWriter();
        writer.write(ta, tempFile.toString());

        try (Stream<String> stream = Files.lines(tempFile.toPath())) {
            List<String> lines = stream.limit(50).collect(Collectors.toList());
            Assert.assertEquals(lines.get(18), "ESB\tB-facility");
            Assert.assertEquals(lines.get(27), "");
            Assert.assertEquals(lines.get(32), "AHFA\tB-other");
        }
    }
}
