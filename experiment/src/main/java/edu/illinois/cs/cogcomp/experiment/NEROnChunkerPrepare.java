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
import edu.illinois.cs.cogcomp.chunker.main.ChunkerAnnotator;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.FuncUtils;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Writer;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.MascXCESReader;
import edu.illinois.cs.cogcomp.pipeline.handlers.OpenNLPChunkerHandler;

/**
 * @author Xiaotian Le
 */
public class NEROnChunkerPrepare {

    public static final String OPENNLP_CHUNKER_MODEL = IOUtil.getProjectFolder() + "config/models/open-nlp/en-chunker.bin";

    public static final String CORPUS_DIRECTORY = IOUtil.getProjectFolder() + "data/corpora/";
    public static final String TRAINING_DIRECTORY = IOUtil.getProjectFolder() + "data/training/NEROnChunker/";
    public static final String REPORT_DIRECTORY = IOUtil.getProjectFolder() + "reports/NEROnChunker/";

    protected final Annotator chunker = new ChunkerAnnotator(false);
    protected final Annotator openNLPChunker = new OpenNLPChunkerHandler(OPENNLP_CHUNKER_MODEL, false);

    protected ColumnFormatGenericWriter writer = new CoNLL2003Writer(CoNLL2003Reader.Tagset.CONLL);
    protected ColumnFormatGenericWriter getWriter() {
        return writer;
    }

    public void write(TextAnnotation ta, String folder, String annotatorType) throws IOException {
        String outputFile = Paths.get(folder, annotatorType, ta.getId()).toString();
        IOUtil.mkdirsFor(outputFile);
        getWriter().write(ta, outputFile + ".conll");
        SerializationHelper.serializeTextAnnotationToFile(ta, outputFile + ".json", true, true);
    }

    public String annotate(TextAnnotation ta, String folder, String annotatorType, Annotator annotator) throws IOException, AnnotatorException {
        double time = FuncUtils.measureTime(() -> annotator.getView(ta));
        String result = String.format("%s\t%.3f", annotatorType, time);

        write(ta, folder, annotatorType);

        ta.removeView(annotator.getViewName());

        return result;
    }

    public void combineReports(String sourceFolder, String targetFolder, String targetFilename) throws IOException {
        String[] reportFiles = IOUtil.lsFileAbsoluteRecursiveSorted(sourceFolder, ".*\\.txt$");

        Map<String, Double> times = new TreeMap<>();

        for (String reportFile : reportFiles) {
            try (Stream<String> lines = Files.lines(Paths.get(reportFile))) {
                lines.forEach(line -> {
                    Scanner scanner = new Scanner(line).useDelimiter("\\t");
                    String type = scanner.next();
                    times.put(type, times.getOrDefault(type, 0.) + scanner.nextDouble());
                });
            }
        }

        try (PrintStream reporter = IOUtil.newTeePrintStream(Paths.get(targetFolder, targetFilename + ".txt").toString())) {
            for (String type : times.keySet()) {
                reporter.println(String.format("%s\t%.3f", type, times.get(type)));
            }
        }
    }

    public void prepareColumnFormat(TextAnnotation ta, String processedFolder, String reportFolder) throws IOException, AnnotatorException {
        try (PrintStream reporter = IOUtil.newTeePrintStream(Paths.get(reportFolder, ta.getId() + ".txt").toString())) {
            ta.removeView(ViewNames.SHALLOW_PARSE);
            write(ta, processedFolder, "gold-pos.no-chunk");

            reporter.println(annotate(ta, processedFolder, "gold-pos.cogcomp-chunk", chunker));

            reporter.println(annotate(ta, processedFolder, "gold-pos.opennlp-chunk", openNLPChunker));
        }
    }

    public static void main(String[] args) throws Exception {
        IOUtil.Params params = new IOUtil.Params(args);

        NEROnChunkerPrepare preparer = new NEROnChunkerPrepare();
        ColumnFormatGenericReader reader = new CoNLL2003Reader(CoNLL2003Reader.Tagset.CONLL);

        if (params.runTask(1)) {
            for (TextAnnotation ta : new MascXCESReader("MASC-3.0.0", CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/newspaper/nyt", ".xml")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "masc-nyt", REPORT_DIRECTORY + "masc-nyt");
            }
        }
        if (params.runTask(2)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "masc-nyt", 5);
        }
        if (params.runTask(3)) {
            String[] documents = IOUtil.lsSorted(CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/newspaper/nyt");
            PrepareUtil.combineAllAnnotations(documents, TRAINING_DIRECTORY + "masc-nyt", TRAINING_DIRECTORY + "masc-nyt-combined", "all.xml", reader);
            preparer.combineReports(REPORT_DIRECTORY + "masc-nyt", REPORT_DIRECTORY + "masc-nyt-combined", "all.xml");
        }

        if (params.runTask(4)) {
            for (TextAnnotation ta : new MascXCESReader("MASC-3.0.0", CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/twitter", ".xml")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "masc-twitter", REPORT_DIRECTORY + "masc-twitter");
            }
        }
        if (params.runTask(5)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "masc-twitter", 5);
        }
        if (params.runTask(6)) {
            String[] documents = IOUtil.lsSorted(CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/twitter");
            PrepareUtil.combineAllAnnotations(documents, TRAINING_DIRECTORY + "masc-twitter", TRAINING_DIRECTORY + "masc-twitter-combined", "all.xml", reader);
            preparer.combineReports(REPORT_DIRECTORY + "masc-twitter", REPORT_DIRECTORY + "masc-twitter-combined", "all.xml");
        }
    }
}
