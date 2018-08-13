package edu.illinois.cs.cogcomp.nlp.corpusreaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.illinois.cs.cogcomp.core.io.IOUtils;

/**
 * A corpus reader that runs all the files (with a specific extension) under a particular corpus directory
 * through an abstract method to generate a TextAnnotation per file.
 *
 * @author Xiaotian Le
 */
public abstract class BasicIncrementalCorpusReader<T> extends AbstractIncrementalCorpusReader<T> {

    protected static Logger logger = LoggerFactory.getLogger(BasicIncrementalCorpusReader.class);

    /**
     * Create a reader without any attached files
     * @param corpusName The name of the corpus
     */
    public BasicIncrementalCorpusReader(String corpusName) {
        this(corpusName, "", "");
    }

    /**
     * @param corpusName The name of the corpus
     * @param corpusDirectory The directory path of the corpus
     * @param fileExtension The file extension of the annotation files, e.g. ".xml"
     */
    public BasicIncrementalCorpusReader(String corpusName, String corpusDirectory, String fileExtension) {
        super(CorpusReaderConfigurator.buildResourceManager(corpusName, "", corpusDirectory, "", fileExtension));
    }

    /**
     * @param stream The InputStream of the annotation file
     * @param textId The textId of the annotation in the corpus
     * @return A list of annotations generated from the annotation file
     */
    public List<T> readFromStream(InputStream stream, String textId) {
        try {
            return readFromStream(corpusName, stream, textId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * @param filename The path of the annotation file
     * @param textId The textId of the annotation in the corpus
     * @return A list of annotations generated from the annotation file
     */
    public List<T> readFromFile(String filename, String textId) {
        try {
            return readFromFile(corpusName, filename, textId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    protected abstract List<T> readFromStream(String corpusName, InputStream stream, String textId) throws IOException, AnnotationParseException;

    protected List<T> readFromFile(String corpusName, String filename, String textId) throws IOException, AnnotationParseException {
        return readFromStream(corpusName, new BufferedInputStream(new FileInputStream(filename)), textId);
    }

    @Override
    public List<List<Path>> getFileListing() throws IOException {
        String corpusDirectory = resourceManager.getString(CorpusReaderConfigurator.ANNOTATION_DIRECTORY.key, null);
        if ("".equals(corpusDirectory)) {
            return Collections.emptyList();
        }

        String fileExtension = resourceManager.getString(CorpusReaderConfigurator.ANNOTATION_EXTENSION.key);
        return Arrays.asList(IOUtils.lsFilesRecursive(corpusDirectory, file ->
                file.isDirectory() || file.getAbsolutePath().endsWith(fileExtension)))
                .stream()
                .map(file -> Collections.singletonList(Paths.get(file)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAnnotationsFromFile(List<Path> corpusFileListEntry) throws IOException, AnnotationParseException {
        String corpusDirectory = resourceManager.getString(CorpusReaderConfigurator.ANNOTATION_DIRECTORY.key);
        String file = corpusFileListEntry.get(0).toString();
        String textId = Paths.get(corpusDirectory).toAbsolutePath().relativize(Paths.get(file).toAbsolutePath()).toString();

        try {
            List<T> results = readFromFile(corpusName, corpusFileListEntry.get(0).toString(), textId);
            logger.info("[" + IOUtils.shortenPath(file) +"] Created TextAnnotation");
            return results;
        } catch (IOException ex) {
            throw new IOException("[" + file + "] Error reading file.", ex);
        } catch (AnnotationParseException ex) {
            throw new AnnotationParseException("[" + file + "] Error parsing file.", ex);
        }
    }

    protected static class AnnotationParseException extends Exception {
        public AnnotationParseException(String message) {
            super(message);
        }

        public AnnotationParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
