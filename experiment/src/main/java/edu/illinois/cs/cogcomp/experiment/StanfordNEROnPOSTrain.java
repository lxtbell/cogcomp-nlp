package edu.illinois.cs.cogcomp.experiment;

import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;

/**
 * @author Xiaotian Le
 */
public class StanfordNEROnPOSTrain {

    public static final String STANFORD_NER_PROPERTIES = IOUtil.getProjectFolder() + "config/stanford-ner.properties";
    public static final String STANFORD_NER_ONLY_PROPERTIES = IOUtil.getProjectFolder() + "config/stanford-ner-only.properties";

    public static final String MODEL_DIRECTORY = IOUtil.getProjectFolder() + "data/models/stanford-ner/";
    public static final String TRAIN_FILE_REGEX = ".*\\.train$";

    private static void train(String dataFile, String modelFile) {
        IOUtil.mkdirsFor(modelFile);

        String propertyFile = dataFile.contains("ritter") ? STANFORD_NER_ONLY_PROPERTIES : STANFORD_NER_PROPERTIES;

        try {
            CRFClassifier.main(new String[]{
                    "-prop",
                    propertyFile,
                    "-trainFile",
                    dataFile,
                    "-serializeTo",
                    modelFile + ".ser.gz"
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.setProperty("log.output", "stdout");
        StanfordRedwoodConfiguration.apply(props);

        IOUtil.Params params = new IOUtil.Params(args);
        int numThreads = params.numThreads;

        System.out.println("Started with -Xmx" + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "m");

        if (params.runTask(0)) {
            EvaluateUtil.trainDirectory(NEROnPOSPrepare.TRAINING_DIRECTORY + "conll2003", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "conll2003", numThreads, StanfordNEROnPOSTrain::train);
        }

        if (params.runTask(2)) {
            EvaluateUtil.trainDirectory(NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "masc-newspaper", numThreads, StanfordNEROnPOSTrain::train);
        }

        if (params.runTask(4)) {
            EvaluateUtil.trainDirectory(NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "masc-twitter", numThreads, StanfordNEROnPOSTrain::train);
        }
        if (params.runTask(5)) {
            EvaluateUtil.trainDirectory(NEROnChunkerPrepare.TRAINING_DIRECTORY + "masc-twitter-combined", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "masc-twitter", numThreads, StanfordNEROnPOSTrain::train);
        }

        if (params.runTask(6)) {
            EvaluateUtil.trainDirectory(NEROnPOSPrepare.TRAINING_DIRECTORY + "ritter", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "ritter", numThreads, StanfordNEROnPOSTrain::train);
        }

        if (params.runTask(8)) {
            EvaluateUtil.trainDirectory(NEROnPOSPrepare.TRAINING_DIRECTORY + "ritter-wnut", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "ritter-wnut", numThreads, StanfordNEROnPOSTrain::train);
        }
    }
}
