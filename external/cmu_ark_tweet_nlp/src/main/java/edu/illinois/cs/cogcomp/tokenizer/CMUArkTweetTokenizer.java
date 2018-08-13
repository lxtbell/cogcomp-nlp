package edu.illinois.cs.cogcomp.tokenizer;

import java.util.Collections;
import java.util.List;

import cmu.arktweetnlp.Twokenize;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.OracleTokenizer;
import edu.illinois.cs.cogcomp.nlp.tokenizer.Tokenizer;

/**
 * @author Xiaotian Le
 */
public class CMUArkTweetTokenizer implements Tokenizer {

    @Override
    public Pair<String[], IntPair[]> tokenizeSentence(String sentence) {
        Tokenization tokenization = tokenizeTextSpan(sentence);
        return new Pair<>(tokenization.getTokens(), tokenization.getCharacterOffsets());
    }

    @Override
    public Tokenization tokenizeTextSpan(String textSpan) {
        List<String> tokens = Twokenize.tokenize(textSpan);
        OracleTokenizer oracleTokenizer = new OracleTokenizer();
        return oracleTokenizer.tokenize(textSpan, tokens, Collections.singletonList(new IntPair(0, tokens.size())));
    }
}
