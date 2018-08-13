package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2000.CoNLL2000Writer;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter.RitterChunkDataReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter.RitterPOSDataReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.MascXCESReader;

/**
 * @author Xiaotian Le
 */
public class ChunkerOnPOSPrepare extends POSPrepare {

    public static final String CORPUS_DIRECTORY = IOUtil.getProjectFolder() + "data/corpora/";
    public static final String TRAINING_DIRECTORY = IOUtil.getProjectFolder() + "data/training/ChunkerOnPOS/";
    public static final String REPORT_DIRECTORY = IOUtil.getProjectFolder() + "reports/ChunkerOnPOS/";

    protected ColumnFormatGenericWriter writer = new CoNLL2000Writer();
    protected ColumnFormatGenericWriter getWriter() {
        return writer;
    }

    public void prepareColumnFormat(TextAnnotation ta, String processedFolder, String reportFolder) throws IOException, AnnotatorException {
        try (PrintStream reporter = IOUtil.newTeePrintStream(Paths.get(reportFolder, ta.getId() + ".txt").toString())) {
            View goldPos = ta.getView(ViewNames.POS);

            write(ta, processedFolder, "gold-pos");

            ta.removeView(ViewNames.POS);
            write(ta, processedFolder, "no-pos");

            reporter.println(annotate(ta, processedFolder, "cmu-pos", cmuArkPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "cogcomp-pos", cogcompPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "nltk-pos", nltkPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "opennlp-pos", openNLPPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "ritter-pos", ritterPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "stanford-pos", stanfordPOS, goldPos));
        }
    }

    public static void main(String[] args) throws IOException, AnnotatorException {
        IOUtil.Params params = new IOUtil.Params(args);

        ChunkerOnPOSPrepare preparer = new ChunkerOnPOSPrepare();
        ColumnFormatGenericReader reader = new CoNLL2000Reader();

        if (params.runTask(0)) {
            for (TextAnnotation ta : new CoNLL2000Reader(CORPUS_DIRECTORY + "conll2000", "all.txt")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "conll2000", REPORT_DIRECTORY + "conll2000");
            }
        }
        if (params.runTask(1)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "conll2000", 5);
        }

        if (params.runTask(2)) {
            for (TextAnnotation ta : new CoNLL2003Reader(CoNLL2003Reader.Tagset.CONLL, CORPUS_DIRECTORY + "conll2003", "all.txt")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "conll2003", REPORT_DIRECTORY + "conll2003");
            }
        }
        if (params.runTask(3)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "conll2003", 5);
        }

        if (params.runTask(4)) {
            for (TextAnnotation ta : new MascXCESReader("MASC-3.0.0", CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/newspaper", ".xml")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "masc-newspaper", REPORT_DIRECTORY + "masc-newspaper");
            }
        }
        if (params.runTask(5)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "masc-newspaper", 5);

            String[] documents = IOUtil.lsRecursiveSorted(CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/newspaper");
            PrepareUtil.combineAllAnnotations(documents, TRAINING_DIRECTORY + "masc-newspaper", TRAINING_DIRECTORY + "masc-newspaper-combined", "all.xml", reader);
            preparer.combineReports(REPORT_DIRECTORY + "masc-newspaper", REPORT_DIRECTORY + "masc-newspaper-combined", "all.xml");
        }

        if (params.runTask(6)) {
            for (TextAnnotation ta : new MascXCESReader("MASC-3.0.0", CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/twitter", ".xml")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "masc-twitter", REPORT_DIRECTORY + "masc-twitter");
            }
        }
        if (params.runTask(7)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "masc-twitter", 5);

            String[] documents = IOUtil.lsSorted(CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/twitter");
            PrepareUtil.combineAllAnnotations(documents, TRAINING_DIRECTORY + "masc-twitter", TRAINING_DIRECTORY + "masc-twitter-combined", "all.xml", reader);
            preparer.combineReports(REPORT_DIRECTORY + "masc-twitter", REPORT_DIRECTORY + "masc-twitter-combined", "all.xml");
        }

        if (params.runTask(8)) {
            for (ColumnFormatGenericReader posReader = new RitterPOSDataReader(CORPUS_DIRECTORY + "ritter"), chunkReader = new RitterChunkDataReader(CORPUS_DIRECTORY + "ritter");
                 posReader.hasNext() && chunkReader.hasNext(); ) {
                TextAnnotation ta = chunkReader.next();
                ta.addView(ViewNames.POS, posReader.next().getView(ViewNames.POS));
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "ritter", REPORT_DIRECTORY + "ritter");
            }
        }
        if (params.runTask(9)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "ritter", 5);
        }
    }
}
