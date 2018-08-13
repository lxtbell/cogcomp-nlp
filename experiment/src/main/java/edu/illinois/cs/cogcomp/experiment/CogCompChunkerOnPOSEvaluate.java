package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.chunker.main.ChunkerAnnotator;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.ChunkLabel;
import edu.illinois.cs.cogcomp.chunker.main.lbjava.Chunker;
import edu.illinois.cs.cogcomp.chunker.utils.CoNLL2000Parser;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.stats.OneVariableStats;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.BIOTester;
import edu.illinois.cs.cogcomp.lbjava.parse.ChildrenFromVectors;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Writer;

/**
 * @author Xiaotian Le
 */
public class CogCompChunkerOnPOSEvaluate {

    public static final String PREDICTION_DIRECTORY = IOUtil.getProjectFolder() + "data/predictions/CogCompChunkerOnPOS/";
    public static final String REPORT_DIRECTORY = IOUtil.getProjectFolder() + "reports/CogCompChunkerOnPOS/";
    public static final String TEST_FILE_REGEX = ".*\\.test$";

    private static TestDiscrete test(String dataFile, String modelFile, String predictionFile) {
        ChunkerAnnotator annotator = EvaluateUtil.makeChunker(modelFile + "_best");

        try {
            TextAnnotation ta = new CoNLL2000Reader().readFromFile(dataFile, "").get(0);
            annotator.addView(ta);
            IOUtil.mkdirsFor(predictionFile);
            new CoNLL2000Writer().write(ta, predictionFile);
        } catch (AnnotatorException ex) {
            ex.printStackTrace();
        }

        Chunker chunker = new Chunker(modelFile + "_best.lc", modelFile + "_best.lex");
        Parser parser = new CoNLL2000Parser(dataFile);
        BIOTester tester = new BIOTester(chunker, new ChunkLabel(), new ChildrenFromVectors(parser));
        return tester.test();
    }

    private static void printPerformance(PrintStream out, List<TestDiscrete> results) {
        List<OneVariableStats> stats = Stream.generate(OneVariableStats::new).limit(4).collect(Collectors.toList());
        results.forEach(result -> {
            double[] stat = result.getOverallStats();
            for (int i = 0; i < stats.size(); ++i) stats.get(i).add(stat[i]);
        });

        out.println("P\tR\tF1\tA");
        out.println(String.join("\t", stats.stream().map(var -> EvaluateUtil.toPercent(var.mean())).collect(Collectors.toList())));
        out.println(String.join("\t", stats.stream().map(var -> EvaluateUtil.toPercent(var.std())).collect(Collectors.toList())));
        out.println();
        results.forEach(result -> result.printPerformance(out));
        out.println();
    }

    public static void run(String dataDirectory, String testFileRegex, String modelDirectory, String predictionDirectory, String reportDirectory) throws IOException {
        Map<String, List<TestDiscrete>> results = EvaluateUtil.testDirectory(dataDirectory, testFileRegex, modelDirectory, predictionDirectory, CogCompChunkerOnPOSEvaluate::test);

        results.forEach((originalFile, fileResults) -> {
            try (PrintStream reporter = IOUtil.newTeePrintStream(IOUtil.resolve(reportDirectory, originalFile.replaceAll("\\\\|/", "__") + "_report.txt"))) {
                String predictionFile = IOUtil.resolve(predictionDirectory, originalFile);
                TextAnnotation ta = new CoNLL2000Reader().readFromFile(predictionFile, "").get(0);
                SerializationHelper.serializeTextAnnotationToFile(ta, predictionFile.replaceAll("\\.conll$", ".json"), true, true);

                printPerformance(reporter, fileResults);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        IOUtil.Params params = new IOUtil.Params(args);

        if (params.runTask(0)) {
            run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "conll2000", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "conll2000",
                    PREDICTION_DIRECTORY + "conll2000",
                    REPORT_DIRECTORY + "conll2000"
            );
        }

        if (params.runTask(2)) {
            run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "conll2003", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "conll2003",
                    PREDICTION_DIRECTORY + "conll2003",
                    REPORT_DIRECTORY + "conll2003"
            );
        }

        if (params.runTask(4)) {
            run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-newspaper",
                    REPORT_DIRECTORY + "masc-newspaper"
            );
        }

        if (params.runTask(6)) {
            run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "masc-twitter",
                    PREDICTION_DIRECTORY + "masc-twitter",
                    REPORT_DIRECTORY + "masc-twitter"
            );
        }

        if (params.runTask(8)) {
            run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "ritter", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "ritter",
                    PREDICTION_DIRECTORY + "ritter",
                    REPORT_DIRECTORY + "ritter"
            );
        }
    }
}
