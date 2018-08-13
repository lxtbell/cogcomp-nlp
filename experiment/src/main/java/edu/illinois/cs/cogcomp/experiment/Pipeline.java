package edu.illinois.cs.cogcomp.experiment;

import org.cogcomp.md.MentionAnnotator;
import org.cogcomp.re.RelationAnnotator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.chunker.main.ChunkerAnnotator;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.depparse.DepAnnotator;
import edu.illinois.cs.cogcomp.nlp.lemmatizer.IllinoisLemmatizer;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.pipeline.handlers.CMUArkTweetPOSHandler;
import edu.illinois.cs.cogcomp.pipeline.handlers.StanfordLemmaHandler;
import edu.illinois.cs.cogcomp.pipeline.handlers.StanfordPOSHandler;
import edu.illinois.cs.cogcomp.pos.POSAnnotator;
import edu.illinois.cs.cogcomp.tokenizer.CMUArkTweetTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.tokenizer.StanfordTextAnnotationBuilder;

public class Pipeline {

    public static void main(String[] args) throws IOException, AnnotatorException {
        String text1 =
                "Good afternoon, gentlemen. " +
                "I am a HAL-9000 computer. " +
                "I was born in Urbana, Il. in 1992.";

        String corpus = "2001_ODYSSEY";
        String textId = "001";

        if (false) {
            TextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new StatefulTokenizer(false));
            TextAnnotation ta = tab.createTextAnnotation(corpus, textId, text1);

            List<Annotator> annotators = Arrays.asList(
                    new POSAnnotator(),
                    new IllinoisLemmatizer(),
                    new ChunkerAnnotator(),
                    new DepAnnotator(),
                    new RelationAnnotator(),
                    new MentionAnnotator()
            );
            for (Annotator annotator : annotators) {
                annotator.getView(ta);
            }

            SerializationHelper.serializeTextAnnotationToFile(ta, "E:/Programming/Web/apelles/public/comparison/cogcomp/HAL-9000.json", true, true);
        }

        {
            TextAnnotationBuilder tab = new StanfordTextAnnotationBuilder();
            TextAnnotation ta = tab.createTextAnnotation(corpus, textId, text1);

            List<Annotator> annotators = Arrays.asList(
                    new StanfordPOSHandler(),
                    new StanfordLemmaHandler(),
                    new StanfordCogCompDepAnnotator()
            );
            for (Annotator annotator : annotators) {
                annotator.getView(ta);
            }

            SerializationHelper.serializeTextAnnotationToFile(ta, "E:/Programming/Web/apelles/public/comparison/stanford/HAL-9000.json", true, true);
        }

        {
            TextAnnotationBuilder tab = new CMUArkTweetTextAnnotationBuilder();
            TextAnnotation ta = tab.createTextAnnotation(corpus, textId, text1);

            List<Annotator> annotators = Arrays.asList(
                    new CMUArkTweetPOSHandler(POSPrepare.CMU_ARK_MODEL_FILE)
            );
            for (Annotator annotator : annotators) {
                annotator.getView(ta);
            }

            SerializationHelper.serializeTextAnnotationToFile(ta, "E:/Programming/Web/apelles/public/comparison/cmu/HAL-9000.json", true, true);
        }
    }
}
