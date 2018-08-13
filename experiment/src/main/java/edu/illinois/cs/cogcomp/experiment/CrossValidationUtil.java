/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;

/**
 * @author Xiaotian Le
 */
public class CrossValidationUtil {

    public static void combineColumnFormat(List<String> sourceFiles, String targetFile) throws IOException {
        List<List<String>> sentences = new ArrayList<>();

        for (String file : sourceFiles) {
            sentences.addAll(readColumnFormatSentences(file));
        }

        writeColumnFormatSentences(targetFile, sentences);
    }

    public static List<IntPair> makeFoldIndices(int count, int numFolds) {
        List<Integer> splitPoints = IntStream.rangeClosed(0, numFolds).boxed()
                .map(fold -> count * fold / numFolds)
                .collect(Collectors.toList());
        return IntStream.range(0, numFolds).boxed()
                .map(fold -> new IntPair(splitPoints.get(fold), splitPoints.get(fold + 1)))
                .collect(Collectors.toList());
    }

    public static <T> List<List<T>> makeFolds(List<T> elements, int numFolds) {
        return makeFoldIndices(elements.size(), numFolds).stream()
                .map(indices -> elements.subList(indices.getFirst(), indices.getSecond()))
                .collect(Collectors.toList());
    }

    public static <T> List<DataPair<T>> makeCrossValidationData(List<T> data, int numFolds) {
        List<List<T>> dataFolds = makeFolds(data, numFolds);

        List<DataPair<T>> results = new ArrayList<>();
        for (int dataPairId = 0; dataPairId < numFolds; ++dataPairId) {
            DataPair<T> resultData = new DataPair<>();
            for (int fold = 0; fold < numFolds; ++fold) {
                if (dataPairId != fold) {
                    resultData.getTrainingData().addAll(dataFolds.get(fold));
                } else {
                    resultData.getTestingData().addAll(dataFolds.get(fold));
                }
            }
            results.add(resultData);
        }
        return results;
    }

    public static class DataPair<T> extends Pair<List<T>, List<T>> {

        public DataPair() {
            super(new ArrayList<T>(), new ArrayList<T>());
        }

        public List<T> getTrainingData() {
            return getFirst();
        }

        public List<T> getTestingData() {
            return getSecond();
        }
    }

    public static List<List<String>> readColumnFormatSentences(String file) throws IOException {
        List<List<String>> sentences = new ArrayList<>();
        List<String> sentence = new ArrayList<>();

        try (Stream<String> reader = Files.lines(Paths.get(file))) {
            reader.forEach(line -> {
                if (line.isEmpty()) {
                    sentences.add(new ArrayList<>(sentence));
                    sentence.clear();
                } else {
                    sentence.add(line);
                }
            });
        }

        if (!sentence.isEmpty()) {
            sentences.add(sentence);
        }
        return sentences;
    }

    public static void writeColumnFormatSentences(String file, List<List<String>> sentences) {
        IOUtil.mkdirsFor(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String output = sentences.stream()
                    .map(sentence -> String.join("\n", sentence))
                    .collect(Collectors.joining("\n\n", "", "\n\n"));
            writer.write(output);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
