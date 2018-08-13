/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.experiment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ColumnFormatGenericReader;

/**
 * @author Xiaotian Le
 */
public class PrepareUtil {

    public static void prepareCrossValidation(String processedFolder, int numFolds) throws IOException {
        for (String processedFile : IOUtil.lsFileAbsoluteRecursiveSorted(processedFolder, ".*\\.conll$")) {
            List<List<String>> sentences = CrossValidationUtil.readColumnFormatSentences(processedFile);

            List<CrossValidationUtil.DataPair<List<String>>> data = CrossValidationUtil.makeCrossValidationData(sentences, numFolds);

            for (int setId = 0; setId < numFolds; ++setId) {
                CrossValidationUtil.writeColumnFormatSentences(processedFile + "_fold" + setId + ".train", data.get(setId).getTrainingData());
                CrossValidationUtil.writeColumnFormatSentences(processedFile + "_fold" + setId + ".test", data.get(setId).getTestingData());
            }
        }
    }

    public static void combineAllAnnotations(String[] documents, String sourceFolder, String targetFolder, String targetFilename, ColumnFormatGenericReader reader) throws IOException {
        String documentPlaceholder = documents[0];
        String[] referenceFiles = IOUtil.lsFileAbsoluteRecursiveSorted(sourceFolder, ".*" + Pattern.quote(documentPlaceholder) + ".*\\.conll(_fold.*)*$");

        for (String referenceFile : referenceFiles) {
            List<String> documentFiles = Arrays.stream(documents)
                    .map(document -> referenceFile.replace(documentPlaceholder, document))
                    .collect(Collectors.toList());
            String targetFile = IOUtil.rebase(sourceFolder, targetFolder, referenceFile.replace(documentPlaceholder, targetFilename));

            CrossValidationUtil.combineColumnFormat(documentFiles, targetFile);

            if (reader != null && !targetFile.matches(".*_fold.*$")) {
                makeJson(targetFile, reader);
            }
        }
    }

    public static void makeJson(String columnFormatFile, ColumnFormatGenericReader reader) throws IOException {
        TextAnnotation ta = reader.readFromFile(columnFormatFile, "").get(0);
        SerializationHelper.serializeTextAnnotationToFile(ta, columnFormatFile.replaceAll("\\.conll$", ".json"), true, true);
    }
}
