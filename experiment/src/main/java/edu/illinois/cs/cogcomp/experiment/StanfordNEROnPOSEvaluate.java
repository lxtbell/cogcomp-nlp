package edu.illinois.cs.cogcomp.experiment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.stats.OneVariableStats;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Writer;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.StanfordNEROutputReader;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;

/**
 * @author Xiaotian Le
 */
public class StanfordNEROnPOSEvaluate {

    public static final String PREDICTION_DIRECTORY = IOUtil.getProjectFolder() + "data/predictions/StanfordNEROnPOS/";
    public static final String REPORT_DIRECTORY = IOUtil.getProjectFolder() + "reports/StanfordNEROnPOS/";
    public static final String TEST_FILE_REGEX = ".*\\.test$";

    private static String test(String dataFile, String modelFile, String predictionFile) {
        try {
            String logFile = IOUtil.createTempFile(StanfordNEROnPOSEvaluate.class.getSimpleName());

            Properties logProps = new Properties();
            logProps.setProperty("log.output", "stdout");
            logProps.setProperty("log.file", logFile);
            StanfordRedwoodConfiguration.apply(logProps);

            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(predictionFile))) {
                Properties crfProps = new Properties();
                SeqClassifierFlags flags = new SeqClassifierFlags(crfProps);
                CRFClassifier<CoreLabel> crf = new CRFClassifier<>(flags);
                crf.loadClassifierNoExceptions(modelFile + ".ser.gz", crfProps);
                crf.classifyAndWriteAnswers(dataFile, stream, crf.defaultReaderAndWriter(), true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return logFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static void collectFileResults(PrintStream out, List<String> fileResults) throws IOException {
        List<OneVariableStats> stats = Stream.generate(OneVariableStats::new).limit(6).collect(Collectors.toList());
        for (String file : fileResults) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String totalLine = reader.lines().filter(line -> line.matches("\\s*Totals\\t*.*")).findFirst().orElse("");
                Scanner scanner = new Scanner(totalLine).useDelimiter("\\t").skip("\\s*Totals\\t*");
                stats.stream().forEach(stat -> stat.add(scanner.nextDouble()));
            }
        }

        out.println("P\tR\tF1\tTP\tFP\tFN");
        out.println(String.format("%s\t%s\t%s\t%.3f\t%.3f\t%.3f",
                EvaluateUtil.toPercent(stats.get(0).mean()), EvaluateUtil.toPercent(stats.get(1).mean()), EvaluateUtil.toPercent(stats.get(2).mean()),
                stats.get(3).mean(), stats.get(4).mean(), stats.get(5).mean()));
        out.println(String.format("%s\t%s\t%s\t%.3f\t%.3f\t%.3f",
                EvaluateUtil.toPercent(stats.get(0).std()), EvaluateUtil.toPercent(stats.get(1).std()), EvaluateUtil.toPercent(stats.get(2).std()),
                stats.get(3).std(), stats.get(4).std(), stats.get(5).std()));
        out.println();
        for (String file : fileResults) {
            try (FileInputStream stream = new FileInputStream(file)) {
                IOUtils.pipe(stream, out);
            }
            out.println();
        }
        out.println();
    }

    public static void run(String dataDirectory, String testFileRegex, String modelDirectory, String predictionDirectory, String reportDirectory) throws IOException {
        Map<String, List<String>> results = EvaluateUtil.testDirectory(dataDirectory, testFileRegex, modelDirectory, predictionDirectory, StanfordNEROnPOSEvaluate::test);

        results.forEach((originalFile, fileResults) -> {
            try (PrintStream reporter = IOUtil.newTeePrintStream(IOUtil.resolve(reportDirectory, originalFile.replaceAll("\\\\|/", "__") + "_report.txt"))) {
                String dataFile = IOUtil.resolve(dataDirectory, originalFile);
                String predictionFile = IOUtil.resolve(predictionDirectory, originalFile);

                TextAnnotation taGold = new CoNLL2003Reader(CoNLL2003Reader.Tagset.CONLL).readFromFile(dataFile, "").get(0);
                TextAnnotation taPrediction = new StanfordNEROutputReader().readFromFile(predictionFile, "").get(0);
                taGold.removeView(ViewNames.NER_CONLL);
                taGold.addView(ViewNames.NER_CONLL, taPrediction.getView(ViewNames.NER_CONLL));
                new CoNLL2003Writer(CoNLL2003Reader.Tagset.CONLL).write(taGold, predictionFile.replaceAll("\\.conll$", "_processed.conll"));
                SerializationHelper.serializeTextAnnotationToFile(taGold, predictionFile.replaceAll("\\.conll$", ".json"), true, true);

                collectFileResults(reporter, fileResults);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        StanfordRedwoodConfiguration.setup();
        IOUtil.Params params = new IOUtil.Params(args);

        if (params.runTask(0)) {
            run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "conll2003", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "conll2003",
                    PREDICTION_DIRECTORY + "conll2003",
                    REPORT_DIRECTORY + "conll2003"
            );
        }

        if (params.runTask(2)) {
            run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-newspaper",
                    REPORT_DIRECTORY + "masc-newspaper"
            );
        }

        if (params.runTask(4)) {
            run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "masc-twitter",
                    PREDICTION_DIRECTORY + "masc-twitter",
                    REPORT_DIRECTORY + "masc-twitter"
            );
        }
        if (params.runTask(5)) {
            run(
                    NEROnChunkerPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "masc-twitter",
                    PREDICTION_DIRECTORY + "masc-twitter",
                    REPORT_DIRECTORY + "masc-twitter"
            );
        }

        if (params.runTask(6)) {
            run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "ritter", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "ritter",
                    PREDICTION_DIRECTORY + "ritter",
                    REPORT_DIRECTORY + "ritter-4"
            );
        }

        if (params.runTask(7)) {
            run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "ritter-wnut", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "ritter-wnut",
                    PREDICTION_DIRECTORY + "ritter-wnut",
                    REPORT_DIRECTORY + "ritter-wnut-4"
            );
        }
    }
}
