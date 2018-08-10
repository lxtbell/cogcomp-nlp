package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormat;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;

/**
 * @author Xiaotian Le
 */
public class RitterNERDataReader extends ColumnFormatGenericReader {

    public static final String CORPUS_ID = "Ritter et al. 2011. NER";

    private static final int COLUMN_TOKEN = 0;
    private static final int COLUMN_NER = 1;

    public enum Tagset {
        CONLL_3,  // Convert Ritter tags to the broad CoNLL 2003 tags without MISC
        CONLL_4,  // Convert Ritter tags to the broad CoNLL 2003 tags with MISC
        CONLL_RAW,  // Output NER_CONLL view without converting Ritter tags
        ONTONOTES,  // Unsupported
        ERE  // Unsupported
    }

    public RitterNERDataReader(Tagset tagset) {
        super(getColumnFormat(tagset), CORPUS_ID);
    }

    public RitterNERDataReader(Tagset tagset, String corpusDirectory) {
        this(tagset, corpusDirectory, "ner.txt");
    }

    public RitterNERDataReader(Tagset tagset, String corpusDirectory, String fileExtension) {
        super(getColumnFormat(tagset), CORPUS_ID, corpusDirectory, fileExtension);
    }

    public static ColumnFormat getColumnFormat(Tagset tagset) {
        ColumnFormat format = new ColumnFormat();

        format.tokenColumn = COLUMN_TOKEN;
        if (tagset == Tagset.CONLL_3) {
            format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_CONLL, "O", RitterNERConfigurator::convertToCoNLL3, RitterNERConfigurator::convertFromCoNLL));
        } else if (tagset == Tagset.CONLL_4) {
            format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_CONLL, "O", RitterNERConfigurator::convertToCoNLL4, RitterNERConfigurator::convertFromCoNLL));
        } else if (tagset == Tagset.CONLL_RAW) {
            format.iobColumns.add(new ColumnFormat.Column(COLUMN_NER, ViewNames.NER_CONLL, "O", String::toUpperCase, String::toLowerCase));
        } else {
            throw new IllegalArgumentException("Tagset " + tagset.toString() + " is not supported.");
        }

        format.columnDelimiter = "\t";
        format.iobCanStartWithI = false;

        return format;
    }
}
