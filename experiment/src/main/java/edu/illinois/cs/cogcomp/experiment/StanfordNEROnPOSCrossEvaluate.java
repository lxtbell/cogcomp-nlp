package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.nio.file.Paths;

import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;

/**
 * @author Xiaotian Le
 */
public class StanfordNEROnPOSCrossEvaluate extends StanfordNEROnPOSEvaluate {

    public static void runCrossModel(String dataDirectory, String testFileRegex, String modelDirectory, String predictionDirectory, String reportDirectory) throws IOException {
        StanfordNEROnPOSEvaluate.run(
                Paths.get(dataDirectory, "stanford-pos.gold-chunk").toString(), testFileRegex,
                Paths.get(modelDirectory, "gold-pos.gold-chunk").toString(),
                Paths.get(predictionDirectory, "gold-model-on-stanford").toString(),
                Paths.get(reportDirectory, "gold-model-on-stanford").toString()
        );
        StanfordNEROnPOSEvaluate.run(
                Paths.get(dataDirectory, "gold-pos.gold-chunk").toString(), testFileRegex,
                Paths.get(modelDirectory, "stanford-pos.gold-chunk").toString(),
                Paths.get(predictionDirectory, "stanford-model-on-gold").toString(),
                Paths.get(reportDirectory, "stanford-model-on-gold").toString()
        );
    }

    public static void main(String[] args) throws IOException {
        StanfordRedwoodConfiguration.setup();
        IOUtil.Params params = new IOUtil.Params(args);

        if (params.runTask(0)) {
            StanfordNEROnPOSEvaluate.run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-twitter-on-newspaper-model",
                    REPORT_DIRECTORY + "masc-twitter-on-newspaper-model"
            );
        }

        if (params.runTask(1)) {
            StanfordNEROnPOSEvaluate.run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "ritter", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "conll2003",
                    PREDICTION_DIRECTORY + "ritter-on-conll2003-model",
                    REPORT_DIRECTORY + "ritter-on-conll2003-model"
            );
        }

        if (params.runTask(2)) {
            StanfordNEROnPOSEvaluate.run(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "ritter-wnut", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "conll2003",
                    PREDICTION_DIRECTORY + "ritter-on-conll2003-model",
                    REPORT_DIRECTORY + "ritter-on-conll2003-model"
            );
        }

        if (params.runTask(3)) {
            runCrossModel(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-newspaper",
                    REPORT_DIRECTORY + "masc-newspaper"
            );
        }

        if (params.runTask(4)) {
            runCrossModel(
                    NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    StanfordNEROnPOSTrain.MODEL_DIRECTORY + "masc-twitter",
                    PREDICTION_DIRECTORY + "masc-twitter",
                    REPORT_DIRECTORY + "masc-twitter"
            );
        }
    }
}
