/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReaderTests;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.MascXCESReader;

public class MascXCESReaderTest {
    private static final String CORPUS_DIRECTORY = "src/test/resources/edu/illinois/cs/cogcomp/nlp/corpusreaders/masc/xces";

    @Test
    public void testCreateTextAnnotation() throws Exception {
        Logger.getLogger(MascXCESReader.class).setLevel(Level.INFO);

        MascXCESReader cnr = new MascXCESReader("", CORPUS_DIRECTORY, ".xml");

        Assert.assertTrue(cnr.hasNext());
        TextAnnotation ta = cnr.next();
        Assert.assertTrue(cnr.hasNext());
        cnr.next();
        Assert.assertFalse(cnr.hasNext());

        List<Constituent> tokens = ta.getView(ViewNames.TOKENS).getConstituents();
        Assert.assertEquals(tokens.size(), 1224);  // tok 1224
        Assert.assertEquals(tokens.get(1).toString(), "setting");

        List<Constituent> lemma = ta.getView(ViewNames.LEMMA).getConstituents();
        Assert.assertEquals(lemma.size(), 1223);  // base= 1223
        Assert.assertEquals(lemma.get(1).getLabel(), "set");

        List<Constituent> pos = ta.getView(ViewNames.POS).getConstituents();
        Assert.assertEquals(pos.size(), 1224);  // msd= 1224
        Assert.assertEquals(pos.get(1).getLabel(), "VBG");

        List<Constituent> sentences = ta.getView(ViewNames.SENTENCE).getConstituents();
        Assert.assertEquals(sentences.size(), 85);  // normalized sentences 85
        Assert.assertEquals(sentences.get(4).getStartSpan(), 39);  // a sentence is created to cover uncovered tokens
        Assert.assertEquals(sentences.get(4).getEndSpan(), 41);

        List<Constituent> sentencesGold = ta.getView(ViewNames.SENTENCE_GOLD).getConstituents();
        Assert.assertEquals(sentencesGold.size(), 71);  // s 71
        Assert.assertEquals(sentencesGold.get(4).getStartSpan(), 41);
        Assert.assertEquals(sentencesGold.get(4).getEndSpan(), 46);

        List<Constituent> shallowParse = ta.getView(ViewNames.SHALLOW_PARSE).getConstituents();
        Assert.assertEquals(shallowParse.size(), 498);  // nchunk 326, vchunk 172
        Assert.assertEquals(shallowParse.get(0).getStartSpan(), 1);
        Assert.assertEquals(shallowParse.get(0).getEndSpan(), 2);
        Assert.assertEquals(shallowParse.get(0).getLabel(), "VP");

        List<Constituent> ner = ta.getView(ViewNames.NER_CONLL).getConstituents();
        Assert.assertEquals(ner.size(), 28);  // location 12, org 11, person 5
        Assert.assertEquals(ner.get(0).getStartSpan(), 379);  // Singapore
        Assert.assertEquals(ner.get(0).getEndSpan(), 380);
        Assert.assertEquals(ner.get(0).getLabel(), "LOC");

        List<Constituent> nerOntonotes = ta.getView(ViewNames.NER_ONTONOTES).getConstituents();
        Assert.assertEquals(nerOntonotes.size(), 40);  // date 12, location 12, org 11, person 5
        Assert.assertEquals(nerOntonotes.get(3).getStartSpan(), 379);  // Singapore
        Assert.assertEquals(nerOntonotes.get(3).getEndSpan(), 380);
        Assert.assertEquals(nerOntonotes.get(3).getLabel(), "LOCATION");
    }
}
