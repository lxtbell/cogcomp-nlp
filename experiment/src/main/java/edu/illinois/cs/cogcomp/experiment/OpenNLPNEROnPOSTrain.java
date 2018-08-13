package edu.illinois.cs.cogcomp.experiment;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import opennlp.tools.formats.Conll03NameSampleStream;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * @author Xiaotian Le
 */
public class OpenNLPNEROnPOSTrain {

    public static final String MODEL_DIRECTORY = IOUtil.getProjectFolder() + "data/models/opennlp-ner/";
    public static final String TRAIN_FILE_REGEX = ".*\\.train$";

    public static ObjectStream<NameSample> getStream(String file) throws IOException {
        return new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, new PlainTextByLineStream(() -> new FileInputStream(file), StandardCharsets.UTF_8), 0xFF);
    }

    private static void train(String dataFile, String modelFile)  {
        IOUtil.mkdirsFor(modelFile);

        try (ObjectStream<NameSample> sampleStream = getStream(dataFile);
             OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile + ".bin"))) {
            TokenNameFinderFactory nameFinderFactory = new TokenNameFinderFactory();
            TokenNameFinderModel model = NameFinderME.train("en", "person", sampleStream, TrainingParameters.defaultParams(), nameFinderFactory);
            model.serialize(modelOut);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        IOUtil.Params params = new IOUtil.Params(args);
        int numThreads = params.numThreads;

        if (params.runTask(0)) {
            EvaluateUtil.trainDirectory(NEROnPOSPrepare.TRAINING_DIRECTORY + "masc-newspaper-combined", TRAIN_FILE_REGEX, MODEL_DIRECTORY + "masc-newspaper", numThreads, OpenNLPNEROnPOSTrain::train);
        }
    }
}
