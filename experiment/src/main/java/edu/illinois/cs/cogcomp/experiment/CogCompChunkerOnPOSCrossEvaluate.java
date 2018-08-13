package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Xiaotian Le
 */
public class CogCompChunkerOnPOSCrossEvaluate extends CogCompChunkerOnPOSEvaluate {

    public static void runCrossModel(String dataDirectory, String testFileRegex, String modelDirectory, String predictionDirectory, String reportDirectory) throws IOException {
        CogCompChunkerOnPOSEvaluate.run(
                Paths.get(dataDirectory, "stanford-pos").toString(), testFileRegex,
                Paths.get(modelDirectory, "gold-pos").toString(),
                Paths.get(predictionDirectory, "gold-model-on-stanford").toString(),
                Paths.get(reportDirectory, "gold-model-on-stanford").toString()
        );
        CogCompChunkerOnPOSEvaluate.run(
                Paths.get(dataDirectory, "gold-pos").toString(), testFileRegex,
                Paths.get(modelDirectory, "stanford-pos").toString(),
                Paths.get(predictionDirectory, "stanford-model-on-gold").toString(),
                Paths.get(reportDirectory, "stanford-model-on-gold").toString()
        );
    }

    public static void main(String[] args) throws IOException {
        IOUtil.Params params = new IOUtil.Params(args);

        if (params.runTask(0)) {
            CogCompChunkerOnPOSEvaluate.run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-twitter-on-newspaper-model",
                    REPORT_DIRECTORY + "masc-twitter-on-newspaper-model"
            );
        }

        if (params.runTask(1)) {
            CogCompChunkerOnPOSEvaluate.run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "ritter", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "conll2000",
                    PREDICTION_DIRECTORY + "ritter-on-conll2000-model",
                    REPORT_DIRECTORY + "ritter-on-conll2000-model"
            );
        }

        if (params.runTask(2)) {
            CogCompChunkerOnPOSEvaluate.run(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "ritter", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "conll2003",
                    PREDICTION_DIRECTORY + "ritter-on-conll2003-model",
                    REPORT_DIRECTORY + "ritter-on-conll2003-model"
            );
        }

        if (params.runTask(3)) {
            runCrossModel(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "masc-newspaper",
                    PREDICTION_DIRECTORY + "masc-newspaper",
                    REPORT_DIRECTORY + "masc-newspaper"
            );
        }

        if (params.runTask(4)) {
            runCrossModel(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "masc-twitter",
                    PREDICTION_DIRECTORY + "masc-twitter",
                    REPORT_DIRECTORY + "masc-twitter"
            );
        }

        if (params.runTask(5)) {
            runCrossModel(
                    ChunkerOnPOSPrepare.TRAINING_DIRECTORY + "ritter", TEST_FILE_REGEX,
                    CogCompChunkerOnPOSTrain.MODEL_DIRECTORY + "ritter",
                    PREDICTION_DIRECTORY + "ritter",
                    REPORT_DIRECTORY + "ritter"
            );
        }
    }
}
