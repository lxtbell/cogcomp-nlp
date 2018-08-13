/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.OracleTokenizer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Xiaotian Le
 */
public abstract class StanfordTokenizationUtil {
    // TODO Move to StanfordAnnotationUtil
    // TODO Test this method
    public static TextAnnotation convertToTextAnnotation(String corpusId, String textId, Annotation document) {
        List<CoreLabel> tokenLabels = document.get(CoreAnnotations.TokensAnnotation.class);

        List<String> tokens = new ArrayList<>();
        List<IntPair> charOffsets = new ArrayList<>();
        for (CoreLabel token : tokenLabels) {
            tokens.add(token.word());
            charOffsets.add(new IntPair(token.beginPosition(), token.endPosition()));
        }

        List<IntPair> sentences = document.get(CoreAnnotations.SentencesAnnotation.class).stream()
                .map(sentence -> new IntPair(sentence.get(CoreAnnotations.TokenBeginAnnotation.class), sentence.get(CoreAnnotations.TokenEndAnnotation.class)))
                .collect(Collectors.toList());
        int[] sentenceEndTokenIndexes = OracleTokenizer.normalizeSentences(sentences, tokenLabels.size());

        return new TextAnnotation(corpusId, textId, document.get(CoreAnnotations.TextAnnotation.class),
                charOffsets.toArray(new IntPair[0]), tokens.toArray(new String[0]), sentenceEndTokenIndexes);
    }

    // TODO Move to StanfordAnnotationUtil
    // TODO Test this method
    public static Annotation convertToStanfordAnnotation(TextAnnotation ta) {
        List<CoreLabel> tokens = new ArrayList<>();
        List<CoreMap> sentences = new ArrayList<>();

        List<Constituent> sentenceConstituents = ta.getView(ViewNames.SENTENCE).getConstituents();

        for (int sentenceId = 0; sentenceId < sentenceConstituents.size(); ++sentenceId) {
            List<CoreLabel> sentenceTokens = new ArrayList<>();

            Constituent sentenceConstituent = sentenceConstituents.get(sentenceId);

            for (int tokenId = sentenceConstituent.getStartSpan(); tokenId < sentenceConstituent.getEndSpan(); ++tokenId) {
                String tokenValue = ta.getToken(tokenId);

                CoreLabel token = new CoreLabel();
                token.setValue(tokenValue);
                token.setWord(tokenValue);
                token.setOriginalText(tokenValue);

                IntPair tokenCharOffset = ta.getTokenCharacterOffset(tokenId);
                token.setBeginPosition(tokenCharOffset.getFirst());
                token.setEndPosition(tokenCharOffset.getSecond());

                int previousTokenEnd = (tokenId > 0) ? ta.getTokenCharacterOffset(tokenId - 1).getSecond() : 0;
                int nextTokenEnd = (tokenId < ta.size() - 1) ? ta.getTokenCharacterOffset(tokenId + 1).getFirst() : ta.text.length();
                token.setBefore(StringUtils.substring(ta.text, previousTokenEnd, tokenCharOffset.getFirst()));
                token.setAfter(StringUtils.substring(ta.text, tokenCharOffset.getSecond(), nextTokenEnd));

                token.setIndex(tokenId);
                token.setSentIndex(tokenId - sentenceConstituent.getStartSpan());

                tokens.add(token);
                sentenceTokens.add(token);
            }

            CoreMap sentence = new ArrayCoreMap();
            sentence.set(CoreAnnotations.TextAnnotation.class, StringUtils.substring(ta.text, sentenceConstituent.getStartCharOffset(), sentenceConstituent.getEndCharOffset()));

            sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, sentenceConstituent.getStartCharOffset());
            sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, sentenceConstituent.getEndCharOffset());

            sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);

            sentence.set(CoreAnnotations.TokenBeginAnnotation.class, sentenceConstituent.getStartSpan());
            sentence.set(CoreAnnotations.TokenEndAnnotation.class, sentenceConstituent.getEndSpan());

            sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, sentenceId);
            sentences.add(sentence);
        }

        Annotation document = new Annotation(ta.text);
        document.set(CoreAnnotations.TokensAnnotation.class, tokens);
        document.set(CoreAnnotations.SentencesAnnotation.class, sentences);

        return document;
    }
}
