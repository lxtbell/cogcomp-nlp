package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Reader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003Writer;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericWriter;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter.RitterNERDataReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.mascReader.MascXCESReader;

/**
 * @author Xiaotian Le
 */
public class NEROnPOSPrepare extends POSPrepare {

    public static final String CORPUS_DIRECTORY = IOUtil.getProjectFolder() + "data/corpora/";
    public static final String TRAINING_DIRECTORY = IOUtil.getProjectFolder() + "data/training/NEROnPOS/";
    public static final String REPORT_DIRECTORY = IOUtil.getProjectFolder() + "reports/NEROnPOS/";

    protected ColumnFormatGenericWriter writer = new CoNLL2003Writer(CoNLL2003Reader.Tagset.CONLL);
    protected ColumnFormatGenericWriter getWriter() {
        return writer;
    }

    public void prepareColumnFormat(TextAnnotation ta, String processedFolder, String reportFolder) throws IOException, AnnotatorException {
        try (PrintStream reporter = IOUtil.newTeePrintStream(Paths.get(reportFolder, ta.getId() + ".txt").toString())) {
            View goldPos = ta.getView(ViewNames.POS);

            write(ta, processedFolder, "gold-pos.gold-chunk");

            ta.removeView(ViewNames.POS);
            write(ta, processedFolder, "no-pos.gold-chunk");

            reporter.println(annotate(ta, processedFolder, "cmu-pos.gold-chunk", cmuArkPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "cogcomp-pos.gold-chunk", cogcompPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "nltk-pos.gold-chunk", nltkPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "opennlp-pos.gold-chunk", openNLPPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "ritter-pos.gold-chunk", ritterPOS, goldPos));

            reporter.println(annotate(ta, processedFolder, "stanford-pos.gold-chunk", stanfordPOS, goldPos));
        }
    }

    public static void main(String[] args) throws Exception {
        IOUtil.Params params = new IOUtil.Params(args);

        NEROnPOSPrepare preparer = new NEROnPOSPrepare();
        ColumnFormatGenericReader reader = new CoNLL2003Reader(CoNLL2003Reader.Tagset.CONLL);

        if (params.runTask(0)) {
            for (TextAnnotation ta : new CoNLL2003Reader(CoNLL2003Reader.Tagset.CONLL, CORPUS_DIRECTORY + "conll2003", "all.txt")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "conll2003", REPORT_DIRECTORY + "conll2003");
            }
        }
        if (params.runTask(1)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "conll2003", 5);
        }

        if (params.runTask(2)) {
            for (TextAnnotation ta : new MascXCESReader("MASC-3.0.0", CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/newspaper", ".xml")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "masc-newspaper", REPORT_DIRECTORY + "masc-newspaper");
            }
        }
        if (params.runTask(3)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "masc-newspaper", 5);

            String[] documents = IOUtil.lsRecursiveSorted(CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/newspaper");
            PrepareUtil.combineAllAnnotations(documents, TRAINING_DIRECTORY + "masc-newspaper", TRAINING_DIRECTORY + "masc-newspaper-combined", "all.xml", reader);
            preparer.combineReports(REPORT_DIRECTORY + "masc-newspaper", REPORT_DIRECTORY + "masc-newspaper-combined", "all.xml");
        }

        if (params.runTask(4)) {
            for (TextAnnotation ta : new MascXCESReader("MASC-3.0.0", CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/twitter", ".xml")) {
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "masc-twitter", REPORT_DIRECTORY + "masc-twitter");
            }
        }
        if (params.runTask(5)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "masc-twitter", 5);

            String[] documents = IOUtil.lsSorted(CORPUS_DIRECTORY + "MASC-3.0.0/xces/written/twitter");
            PrepareUtil.combineAllAnnotations(documents, TRAINING_DIRECTORY + "masc-twitter", TRAINING_DIRECTORY + "masc-twitter-combined", "all.xml", reader);
            preparer.combineReports(REPORT_DIRECTORY + "masc-twitter", REPORT_DIRECTORY + "masc-twitter-combined", "all.xml");
        }

        if (params.runTask(6)) {
            for (TextAnnotation ta : new RitterNERDataReader(RitterNERDataReader.Tagset.CONLL_4, CORPUS_DIRECTORY + "ritter")) {
                ta.addView(ViewNames.POS, new TokenLabelView(ViewNames.POS, "", ta, 1.0));
                ta.addView(ViewNames.SHALLOW_PARSE, new SpanLabelView(ViewNames.SHALLOW_PARSE, "", ta, 1.0));
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "ritter", REPORT_DIRECTORY + "ritter");
            }
        }
        if (params.runTask(7)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "ritter", 5);
        }

        if (params.runTask(8)) {
            for (TextAnnotation ta : new RitterNERDataReader(RitterNERDataReader.Tagset.CONLL_4, CORPUS_DIRECTORY + "ritter", "ner_wnut_unique.txt")) {
                ta.addView(ViewNames.POS, new TokenLabelView(ViewNames.POS, "", ta, 1.0));
                ta.addView(ViewNames.SHALLOW_PARSE, new SpanLabelView(ViewNames.SHALLOW_PARSE, "", ta, 1.0));
                preparer.prepareColumnFormat(ta, TRAINING_DIRECTORY + "ritter-wnut", REPORT_DIRECTORY + "ritter-wnut");
            }
        }
        if (params.runTask(9)) {
            PrepareUtil.prepareCrossValidation(TRAINING_DIRECTORY + "ritter-wnut", 5);
        }
    }
}
