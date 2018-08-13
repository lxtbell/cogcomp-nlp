package edu.illinois.cs.cogcomp.tokenizer;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.nlp.tokenizer.Tokenizer;

/**
 * @author Xiaotian Le
 */
public class CMUArkTweetTextAnnotationBuilder implements TextAnnotationBuilder {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public TextAnnotation createTextAnnotation(String text) throws IllegalArgumentException {
        return createTextAnnotation("", "", text);
    }

    @Override
    public TextAnnotation createTextAnnotation(String corpusId, String textId, String text) throws IllegalArgumentException {
        CMUArkTweetTokenizer tokenizer = new CMUArkTweetTokenizer();
        Tokenizer.Tokenization tokenization = tokenizer.tokenizeTextSpan(text);
        return new TextAnnotation(corpusId, textId, text, tokenization.getCharacterOffsets(), tokenization.getTokens(), tokenization.getSentenceEndTokenIndexes());
    }

    @Override
    public TextAnnotation createTextAnnotation(String corpusId, String textId, String text, Tokenizer.Tokenization tokenization) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot create annotation from Tokenization using " + getName());
    }
}
