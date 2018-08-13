package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

import edu.illinois.cs.cogcomp.chunker.main.lbjava.ChunkLabel;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.Chunker;
import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000Parser;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.BIOTester;
import edu.illinois.cs.cogcomp.lbjava.parse.ChildrenFromVectors;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

/**
 * @author Xiaotian Le
 */
public class CogCompChunkerOnPOSTrain {

    public static final String MODEL_DIRECTORY = IOUtil.getProjectFolder() + "data/models/illinois-chunker/";
    public static final String TRAIN_FILE_REGEX = ".*\\.train$";

    private static void train(
            String dataFile, String modelFile,
            int iterations, double validationRatio, int saveInterval) {
        IOUtil.mkdirsFor(modelFile);

        try (PrintStream reporter = IOUtil.newTeePrintStream(modelFile + "_report.txt")) {
            Chunker chunker = new Chunker(modelFile + ".lc", modelFile + ".lex");
            Parser parser = new CoNLL2000Parser(dataFile);

            int totalSentences = 0;
            while (parser.next() != null) {
                totalSentences++;
            }
            parser.reset();
            long trainingSentences = Math.round(totalSentences * (1. - validationRatio));

            Map<Integer, Double> f1Scores = new TreeMap<>();

            Chunker.isTraining = true;

            for (int i = 1; i <= iterations; i++) {
                LinkedVector ex;
                for (int sentenceIndex = 0; sentenceIndex < trainingSentences && (ex = (LinkedVector) parser.next()) != null; ++sentenceIndex) {
                    for (int j = 0; j < ex.size(); j++) {
                        chunker.learn(ex.get(j));
                    }
                }
                chunker.doneWithRound();

                if (i % saveInterval == 0) {
                    String currentFile = modelFile + "_epoch" + i;

                    chunker.write(currentFile + ".lc", currentFile + ".lex");
                    BIOTester tester = new BIOTester(new Chunker(currentFile + ".lc", currentFile + ".lex"), new ChunkLabel(), new ChildrenFromVectors(parser));
                    double[] result = tester.test().getOverallStats();
                    double f1Score = result[2];

                    reporter.println("Iteration #" + i + " finished. F1 = " + EvaluateUtil.toPercent(f1Score) + ".");
                    f1Scores.put(i, f1Score);
                } else {
                    reporter.println("Iteration #" + i + " finished.");
                }

                parser.reset();
            }

            chunker.doneLearning();

            int bestRound = IntStream.rangeClosed(1, iterations).boxed()
                    .filter(f1Scores::containsKey)
                    .max(Comparator.comparing(f1Scores::get))
                    .orElse(0);
            try {
                Files.copy(Paths.get(modelFile + "_epoch" + bestRound + ".lc"), Paths.get(modelFile + "_best.lc"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Paths.get(modelFile + "_epoch" + bestRound + ".lex"), Paths.get(modelFile + "_best.lex"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            reporter.println("Best iteration = #" + bestRound);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        IOUtil.Params params = new IOUtil.Params(args);
        int numThreads = params.numThreads;

        if (params.runTask(0)) {
            EvaluateUtil.trainDirectory(ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "conll2000", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "conll2000", numThreads,
                    (dataFile, modelFile) -> train(dataFile, modelFile, 30, 0.2, 5));
        }

        if (params.runTask(2)) {
            EvaluateUtil.trainDirectory(ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "conll2003", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "conll2003", numThreads,
                    (dataFile, modelFile) -> train(dataFile, modelFile, 30, 0.2, 5));
        }

        if (params.runTask(4)) {
            EvaluateUtil.trainDirectory(ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "masc-newspaper", numThreads,
                    (dataFile, modelFile) -> train(dataFile, modelFile, 50, 0.2, 5));
        }

        if (params.runTask(6)) {
            EvaluateUtil.trainDirectory(ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "masc-twitter", numThreads,
                    (dataFile, modelFile) -> train(dataFile, modelFile, 50, 0.2, 5));
        }

        if (params.runTask(8)) {
            EvaluateUtil.trainDirectory(ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "ritter", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "ritter", numThreads,
                    (dataFile, modelFile) -> train(dataFile, modelFile, 50, 0.2, 5));
        }
    }
}
