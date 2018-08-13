/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.pipeline.handlers;

import java.util.List;
import java.util.Properties;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TreeView;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * Based on external/stanford_3.3.1 by James Clarke and Christos Christodoulopoulos
 *
 * @author Xiaotian Le
 */
public class StanfordParse391Handler extends edu.illinois.cs.cogcomp.annotation.Annotator {

    public static final String VIEW_NAME = ViewNames.PARSE_STANFORD;
    public static final Class<? extends CoreAnnotation<edu.stanford.nlp.trees.Tree>> STANFORD_CLASS_NAME = TreeCoreAnnotations.TreeAnnotation.class;

    private Annotator annotator;

    public StanfordParse391Handler() {
        this(true);
    }

    public StanfordParse391Handler(boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE, ViewNames.POS}, isLazilyInitialized);
    }

    @Override
    public void initialize(ResourceManager rm) {
        annotator = StanfordAnnotationUtil.getDefaultAnnotator(new Properties(), Annotator.STANFORD_PARSE);
    }

    @Override
    protected void addView(TextAnnotation ta) throws AnnotatorException {
        Annotation document = StanfordTokenizationUtil.convertToStanfordAnnotation(ta);
        StanfordPOSHandler.convertFromTextAnnotation(ta, document);

        annotator.annotate(document);

        ta.addView(VIEW_NAME, toTextAnnotationView(document, ta));
    }

    public static TreeView toTextAnnotationView(Annotation source, TextAnnotation target) {
        List<CoreMap> sentences = source.get(CoreAnnotations.SentencesAnnotation.class);

        TreeView treeView = new TreeView(VIEW_NAME, StanfordParse391Handler.class.getSimpleName(), target, 1.0);

        for (int sentenceId = 0; sentenceId < sentences.size(); sentenceId++) {
            CoreMap sentence = sentences.get(sentenceId);
            edu.stanford.nlp.trees.Tree stanfordTree = sentence.get(STANFORD_CLASS_NAME);
            Tree<String> tree = generateNode(stanfordTree);
            treeView.setParseTree(sentenceId, tree);
        }

        return treeView;
    }

    public static void convertFromTextAnnotation(TextAnnotation source, Annotation target) {
        // TODO
    }

    /**
     * Taken from external/stanford_3.3.1
     * Created by James Clarke and Christos Christodoulopoulos
     *
     * Takes a Stanford Tree and Curator Tree and recursively populates the Curator Tree to match
     * the Stanford Tree. Returns the root Node of the tree.
     *
     * @param parse Stanford Tree
     * @return root Node of the Tree
     */
    public static Tree<String> generateNode(edu.stanford.nlp.trees.Tree parse) {
        Tree<String> node = new Tree<>(parse.value());

        for (edu.stanford.nlp.trees.Tree pt : parse.getChildrenAsList()) {
            if (pt.isLeaf()) {
                node.addLeaf(pt.nodeString());
            } else {
                node.addSubtree(generateNode(pt));
            }
        }

        return node;
    }
}
