/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Stream;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.utilities.FuncUtils;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;
import edu.illinois.cs.cogcomp.pipeline.handlers.CMUArkTweetPOSHandler;
import edu.illinois.cs.cogcomp.pipeline.handlers.ExternalPOSHandler;
import edu.illinois.cs.cogcomp.pipeline.handlers.OpenNLPPOSHandler;
import edu.illinois.cs.cogcomp.pipeline.handlers.StanfordPOSHandler;
import edu.illinois.cs.cogcomp.pos.POSAnnotator;

/**
 * @author Xiaotian Le
 */
public abstract class POSPrepare {

    public static final String CMU_ARK_MODEL_FILE = IOUtil.getProjectFolder() + "config/models/cmu-ark-tweet-nlp/model.20120919";
    public static final String NLTK_POS_COMMAND = "python $HOME/coding/twitter_nlp/python/pos_tagger_nltk.py";
    public static final String OPENNLP_POS_MODEL_FILE = IOUtil.getProjectFolder() + "config/models/open-nlp/en-pos-maxent.bin";
    public static final String RITTER_POS_COMMAND = "TWITTER_NLP=$HOME/coding/twitter_nlp python $HOME/coding/twitter_nlp/python/pos_tagger_only.py";

    protected final Annotator cmuArkPOS = new CMUArkTweetPOSHandler(CMU_ARK_MODEL_FILE, false);
    protected final Annotator cogcompPOS = new POSAnnotator(false);
    protected final Annotator nltkPOS = new ExternalPOSHandler(NLTK_POS_COMMAND, false);
    protected final Annotator openNLPPOS = new OpenNLPPOSHandler(OPENNLP_POS_MODEL_FILE, false);
    protected final Annotator ritterPOS = new ExternalPOSHandler(RITTER_POS_COMMAND, false);
    protected final Annotator stanfordPOS = new StanfordPOSHandler(false);

    protected abstract ColumnFormatGenericWriter getWriter();

    public void write(TextAnnotation ta, String folder, String annotatorType) throws IOException {
        String outputFile = Paths.get(folder, annotatorType, ta.getId()).toString();
        IOUtil.mkdirsFor(outputFile);
        getWriter().write(ta, outputFile + ".conll");
        SerializationHelper.serializeTextAnnotationToFile(ta, outputFile + ".json", true, true);
    }

    public String annotate(TextAnnotation ta, String folder, String annotatorType, Annotator annotator, View goldView) throws IOException, AnnotatorException {
        double time = FuncUtils.measureTime(() -> annotator.getView(ta));
        IntPair correctCount = EvaluateUtil.countCorrect(goldView, ta.getView(annotator.getViewName()));
        String result = String.format("%s\t%.3f\t%s", annotatorType, time, EvaluateUtil.toAccuracyReport(correctCount));

        write(ta, folder, annotatorType);

        ta.removeView(annotator.getViewName());

        return result;
    }

    public void combineReports(String sourceFolder, String targetFolder, String targetFilename) throws IOException {
        String[] reportFiles = IOUtil.lsFileAbsoluteRecursiveSorted(sourceFolder, ".*\\.txt$");

        Map<String, Double> times = new TreeMap<>();
        Map<String, IntPair> correctCounts = new TreeMap<>();

        for (String reportFile : reportFiles) {
            try (Stream<String> lines = Files.lines(Paths.get(reportFile))) {
                lines.forEach(line -> {
                    Scanner scanner = new Scanner(line).useDelimiter("\\t");
                    String type = scanner.next();
                    times.put(type, times.getOrDefault(type, 0.) + scanner.nextDouble());
                    IntPair correctCount = correctCounts.computeIfAbsent(type, key -> new IntPair(0, 0));
                    correctCount.setFirst(correctCount.getFirst() + scanner.nextInt());
                    correctCount.setSecond(correctCount.getSecond() + scanner.nextInt());
                });
            }
        }

        try (PrintStream reporter = IOUtil.newTeePrintStream(Paths.get(targetFolder, targetFilename + ".txt").toString())) {
            for (String type : times.keySet()) {
                reporter.println(String.format("%s\t%.3f\t%s", type, times.get(type), EvaluateUtil.toAccuracyReport(correctCounts.get(type))));
            }
        }
    }
}
