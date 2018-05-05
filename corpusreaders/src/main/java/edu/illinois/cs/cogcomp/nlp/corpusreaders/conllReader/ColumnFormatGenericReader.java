/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.BasicIncrementalCorpusReader;

/**
 * This abstract class reads CoNLL column format data, customizable by extending it.
 *
 * @author Xiaotian Le
 */
public abstract class ColumnFormatGenericReader extends BasicIncrementalCorpusReader<TextAnnotation> {

    protected ColumnFormat format;

    /**
     * @param corpusName The name of the corpus
     */
    public ColumnFormatGenericReader(ColumnFormat format, String corpusName) {
        super(corpusName);
        this.format = format;
    }

    /**
     * @param corpusName The name of the corpus
     * @param corpusDirectory The directory path of the corpus
     * @param fileExtension The file extension of the annotation files, e.g. ".xml"
     */
    public ColumnFormatGenericReader(ColumnFormat format, String corpusName, String corpusDirectory, String fileExtension) {
        super(corpusName, corpusDirectory, fileExtension);
        this.format = format;
    }

    private static void logColumn(Logger logger, String filename, String columnName, int labelCount) {
        logger.info("[" + IOUtils.shortenPath(filename) + "] Processed " + labelCount + " " + columnName + " constituents.");
    }

    /**
     * Helper for create a TokenLabelView from a stream of token labels
     */
    public static int createTokenLabelView(
            Stream<Pair<Integer, String>> tokenLabels,
            TextAnnotation ta,
            String viewName) {
        TokenLabelView view = new TokenLabelView(viewName, "GoldStandard", ta, 1.0);
        tokenLabels.forEach(label -> view.addTokenLabel(label.getFirst(), label.getSecond(), 1.0));
        ta.addView(viewName, view);
        return view.count();
    }

    /**
     * Helper for create a SpanLabelView from a stream of span labels
     */
    public static int createSpanLabelView(
            Stream<Pair<IntPair, String>> spans,
            TextAnnotation ta,
            String viewName,
            boolean allowOverlapping) {
        SpanLabelView view = new SpanLabelView(viewName, "GoldStandard", ta, 1.0, allowOverlapping);
        spans.forEach(span -> view.addSpanLabel(
                span.getFirst().getFirst(), span.getFirst().getSecond(), span.getSecond(), 1.0));
        ta.addView(viewName, view);
        return view.count();
    }

    @Override
    protected List<TextAnnotation> readFromStream(String corpusName, InputStream stream, String textId) throws IOException {
        List<String> tokens = new ArrayList<>();
        SpanCollector sentences = new SpanCollector();

        // From viewName to a map from columnNumber to collection of labels
        Map<String, Map<Integer, List<Pair<Integer, String>>>> tokenLabelCollectors = new HashMap<>();
        Map<String, Map<Integer, SpanCollector>> spanCollectors = new HashMap<>();

        sentences.startSpan(0, ViewNames.SENTENCE);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            for (String line; (line = reader.readLine()) != null; ) {
                line = line.trim();

                int currentTokenId = tokens.size();

                if (line.isEmpty()) {
                    sentences.startSpan(currentTokenId, ViewNames.SENTENCE);
                    continue;
                }

                String[] parts = line.split(format.columnDelimiter);

                String token = parts[format.tokenColumn];
                if (token.equals("-DOCSTART-") || token.trim().isEmpty()) {
                    continue;
                }
                tokens.add(token);

                for (ColumnFormat.Column column : format.tokenLabelColumns) {
                    if (parts.length < 2) {
                        System.err.println();
                    }
                    String taLabel = column.readLabel.apply(parts[column.columnNumber]);

                    if (taLabel != null) {
                        tokenLabelCollectors
                                .computeIfAbsent(column.viewName, key -> new HashMap<>())
                                .computeIfAbsent(column.columnNumber, key -> new ArrayList<>())
                                .add(new Pair<>(currentTokenId, taLabel));
                    }
                }

                for (ColumnFormat.Column column : format.iobColumns) {
                    String part = parts[column.columnNumber];
                    String[] hyphenSplit = part.split("-", 2);
                    String controlChar = hyphenSplit[0].toUpperCase();
                    SpanCollector spanCollector = spanCollectors
                            .computeIfAbsent(column.viewName, key -> new HashMap<>())
                            .computeIfAbsent(column.columnNumber, key -> new SpanCollector());

                    if (hyphenSplit.length == 2) {
                        String taLabel = column.readLabel.apply(hyphenSplit[1]);

                        if (controlChar.equals("B") || controlChar.equals("U")) {
                            spanCollector.startSpan(currentTokenId, taLabel);
                        }
                        else if (controlChar.equals("I") && format.iobCanStartWithI) {
                            spanCollector.insideSpan(currentTokenId, taLabel);
                        }
                    } else {
                        if (controlChar.startsWith("O") || controlChar.startsWith("_")) {
                            spanCollector.stopSpan(currentTokenId);
                        }
                    }
                }
            }
        }

        sentences.stopSpan(tokens.size());
        spanCollectors.forEach((viewName, spanCollectorMap) ->
                spanCollectorMap.forEach((columnNumber, spanCollector) ->
                        spanCollector.stopSpan(tokens.size())));

        List<String[]> tokenizedSentences = sentences.stream()
                .map(pair -> tokens.subList(pair.getFirst().getFirst(), pair.getFirst().getSecond()).toArray(new String[0]))
                .collect(Collectors.toList());
        TextAnnotation ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(this.corpusName, textId, tokenizedSentences);

        tokenLabelCollectors.forEach((viewName, tokenLabelCollectorMap) -> {
            int labelCount = createTokenLabelView(tokenLabelCollectorMap.values().stream().flatMap(List::stream), ta, viewName);
            logColumn(logger, textId, viewName, labelCount);
        });

        spanCollectors.forEach((viewName, spanCollectorMap) -> {
            int labelCount = createSpanLabelView(spanCollectorMap.values().stream().flatMap(SpanCollector::stream), ta, viewName, true);
            logColumn(logger, textId, viewName, labelCount);
        });

        return Collections.singletonList(ta);
    }

    /**
     * A helper class for producing a List of span labels when fed with span starts and stops
     * A span label is stored as an IntPair of span start and end positions and a String of the token label
     */
    public static class SpanCollector {

        private int startPosition = Integer.MAX_VALUE;
        private String currentValue = null;
        private List<Pair<IntPair, String>> collectedSpans = new ArrayList<>();

        public void startSpan(int position, String value) {
            stopSpan(position);
            startPosition = position;
            currentValue = value;
        }

        public void insideSpan(int position, String value) {
            if (!value.equals(currentValue)) {
                startSpan(position, value);
            }
        }

        public void stopSpan(int position) {
            if (position > startPosition) {
                collectedSpans.add(new Pair<>(new IntPair(startPosition, position), currentValue));
            }
            startPosition = Integer.MAX_VALUE;
            currentValue = null;
        }

        public List<Pair<IntPair, String>> collect() {
            return collectedSpans;
        }

        public Stream<Pair<IntPair, String>> stream() {
            return collect().stream();
        }
    }
}
