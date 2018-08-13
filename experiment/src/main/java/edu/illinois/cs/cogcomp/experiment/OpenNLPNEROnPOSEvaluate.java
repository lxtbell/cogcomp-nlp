package edu.illinois.cs.cogcomp.experiment;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.core.stats.OneVariableStats;
import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.eval.FMeasure;

/**
 * @author Xiaotian Le
 */
public class OpenNLPNEROnPOSEvaluate {

    public static final String PREDICTION_DIRECTORY = IOUtil.getProjectFolder() + "data/predictions/OpenNLPNEROnPOS/";
    public static final String REPORT_DIRECTORY = IOUtil.getProjectFolder() + "reports/OpenNLPNEROnPOS/";
    public static final String TEST_FILE_REGEX = ".*\\.test$";

    private static FMeasure test(String dataFile, String modelFile, String predictionFile) {
        try (ObjectStream<NameSample> sampleStream = OpenNLPNEROnPOSTrain.getStream(dataFile);
             InputStream modelIn = new FileInputStream(modelFile + ".bin")) {
            Files.touch(new File(predictionFile));

            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(new NameFinderME(model));
            evaluator.evaluate(sampleStream);
            return evaluator.getFMeasure();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void printPerformance(PrintStream out, List<FMeasure> results) {
        List<OneVariableStats> stats = Stream.generate(OneVariableStats::new).limit(3).collect(Collectors.toList());
        results.forEach(result -> {
            stats.get(0).add(result.getPrecisionScore());
            stats.get(1).add(result.getRecallScore());
            stats.get(2).add(result.getFMeasure());
        });

        out.println("P\tR\tF1");
        out.println(String.join("\t", stats.stream().map(var -> EvaluateUtil.toPercent(var.mean())).collect(Collectors.toList())));
        out.println(String.join("\t", stats.stream().map(var -> EvaluateUtil.toPercent(var.std())).collect(Collectors.toList())));
        out.println();
        results.forEach(result -> {
            out.println(result.toString());
            out.println();
        });
        out.println();
    }

    private static void run(String dataDirectory, String testFileRegex, String modelDirectory, String predictionDirectory, String reportDirectory) throws IOException {
        Map<String, List<FMeasure>> results = EvaluateUtil.testDirectory(dataDirectory, testFileRegex, modelDirectory, predictionDirectory, OpenNLPNEROnPOSEvaluate::test);

        results.forEach((originalFile, fileResults) -> {
            try (PrintStream out = IOUtil.newTeePrintStream(IOUtil.resolve(reportDirectory, originalFile.replaceAll("\\\\|/", "__") + "_report.txt"))) {
                printPerformance(out, fileResults);
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
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TEST_FILE_REGEX,
                    OpenNLPNEROnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-newspaper",
                    REPORT_DIRECTORY + "masc-newspaper"
            );
        }
    }
}
