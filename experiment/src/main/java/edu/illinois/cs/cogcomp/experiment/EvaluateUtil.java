/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import edu.illinois.cs.cogcomp.chunker.main.ChunkerAnnotator;
import edu.illinois.cs.cogcomp.chunker.main.ChunkerConfigurator;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.FuncUtils;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * @author Xiaotian Le
 */
public class EvaluateUtil {

    public static IntPair countCorrect(View viewA, View viewB) {
        if (viewA instanceof TokenLabelView && viewB instanceof TokenLabelView) {
            TokenLabelView tokenA = (TokenLabelView)viewA;
            TokenLabelView tokenB = (TokenLabelView)viewB;

            int totalLabel = Math.min(viewA.count(), viewB.count());
            int correctLabel = (int)IntStream.range(0, totalLabel)
                    .filter(tokenId -> tokenA.getLabel(tokenId).equals(tokenB.getLabel(tokenId)))
                    .count();

            return new IntPair(correctLabel, totalLabel);
        } else {
            // TODO
        }
        return new IntPair(0, 0);
    }

    public static String toPercent(double number) {
        return String.format("%.3f", number * 100);
    }

    public static String toAccuracyReport(IntPair correctCount) {
        double accuracy = ((double) correctCount.getFirst()) / ((double) correctCount.getSecond());
        return String.format("%d\t%d\t%s", correctCount.getFirst(), correctCount.getSecond(), toPercent(accuracy));
    }

    public static void trainDirectory(
            String dataDirectory, String trainingFileRegex, String modelDirectory, int numThreads, BiConsumer<String, String> trainer) throws IOException, InterruptedException {
        String[] dataFiles = IOUtil.lsFileAbsoluteRecursiveSorted(dataDirectory, trainingFileRegex);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (String dataFile : dataFiles) {
            String modelFile = IOUtil.rebase(dataDirectory, modelDirectory, FilenameUtils.removeExtension(dataFile));
            executor.submit(() -> trainer.accept(dataFile, modelFile));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public static <T> Map<String, List<T>> testDirectory(
            String dataDirectory, String testingFileRegex, String modelDirectory, String predictionDirectory, FuncUtils.TriFunction<String, String, String, T> tester) throws IOException {
        Map<String, List<T>> results = new HashMap<>();
        Map<String, List<String>> predictions = new HashMap<>();

        String[] dataFiles = IOUtil.lsFileAbsoluteRecursiveSorted(dataDirectory, testingFileRegex);

        for (String dataFile : dataFiles) {
            String foldFile = FilenameUtils.removeExtension(IOUtil.relativize(dataDirectory, dataFile));
            String modelFile = IOUtil.resolve(modelDirectory, foldFile);
            String predictionFile = IOUtil.createTempFile(EvaluateUtil.class.getSimpleName());

            T result = tester.apply(dataFile, modelFile, predictionFile);
            if (result == null) continue;

            String originalFile = foldFile.replaceAll("_fold.*$", "");
            results.computeIfAbsent(originalFile, key -> new ArrayList<>()).add(result);
            predictions.computeIfAbsent(originalFile, key -> new ArrayList<>()).add(predictionFile);
        }

        predictions.forEach((originalFile, prediction) -> {
            try {
                String predictionFile = IOUtil.resolve(predictionDirectory, originalFile);
                CrossValidationUtil.combineColumnFormat(prediction, predictionFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return results;
    }

    public static ChunkerAnnotator makeChunker(String modelFile) {
        Properties props = new Properties();
        props.setProperty(ChunkerConfigurator.MODEL_PATH.key, modelFile + ".lc");
        props.setProperty(ChunkerConfigurator.MODEL_LEX_PATH.key, modelFile + ".lex");
        ResourceManager rm = new ResourceManager(props);
        return new ChunkerAnnotator(false, rm);
    }
}
