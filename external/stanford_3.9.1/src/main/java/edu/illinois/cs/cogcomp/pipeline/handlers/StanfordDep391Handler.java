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
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TreeView;
import edu.illinois.cs.cogcomp.core.datastructures.trees.Tree;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * Based on external/stanford_3.3.1 by James Clarke and Christos Christodoulopoulos
 *
 * @author Xiaotian Le
 */
public class StanfordDep391Handler extends edu.illinois.cs.cogcomp.annotation.Annotator {

    public static final String VIEW_NAME = ViewNames.DEPENDENCY_STANFORD;

    private Annotator annotator;

    public StanfordDep391Handler() {
        this(true);
    }

    public StanfordDep391Handler(boolean isLazilyInitialized) {
        super(VIEW_NAME, new String[]{ViewNames.SENTENCE, ViewNames.POS}, isLazilyInitialized);
    }

    @Override
    public void initialize(ResourceManager rm) {
        annotator = StanfordAnnotationUtil.getDefaultAnnotator(new Properties(), Annotator.STANFORD_DEPENDENCIES);
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

        TreeView treeView = new TreeView(VIEW_NAME, StanfordDep391Handler.class.getSimpleName(), target, 1.0);

        for (int sentenceId = 0; sentenceId < sentences.size(); sentenceId++) {
            CoreMap sentence = sentences.get(sentenceId);
            SemanticGraph depGraph = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            IndexedWord root = depGraph.getFirstRoot();

            int tokenStart = getNodePosition(target, root, sentenceId);
            Pair<String, Integer> nodePair = new Pair<>(root.originalText(), tokenStart);
            Tree<Pair<String, Integer>> tree = new Tree<>(nodePair);
            populateChildren(depGraph, root, tree, target, sentenceId);
            treeView.setDependencyTree(sentenceId, tree);
        }

        return treeView;
    }

    public static void convertFromTextAnnotation(TextAnnotation source, Annotation target) {
        // TODO
    }

    /**
     * Taken from external/stanford_3.3.1
     * Created by James Clarke and Christos Christodoulopoulos
     */
    private static void populateChildren(
            SemanticGraph depGraph, IndexedWord root,
            Tree<Pair<String, Integer>> tree, TextAnnotation ta, int sentId) {
        if (depGraph.getChildren(root).size() == 0)
            return;
        for (IndexedWord child : depGraph.getChildren(root)) {
            int childPosition = getNodePosition(ta, child, sentId);
            Pair<String, Integer> nodePair = new Pair<>(child.originalText(), childPosition);
            Tree<Pair<String, Integer>> childTree = new Tree<>(nodePair);
            tree.addSubtree(childTree, new Pair<>(depGraph.getEdge(root, child).getRelation().toString().toUpperCase(), childPosition));
            populateChildren(depGraph, child, childTree, ta, sentId);
        }
    }

    /**
     * Taken from external/stanford_3.3.1
     * Created by James Clarke and Christos Christodoulopoulos
     *
     * Gets the token index of a Stanford dependency node relative to the current sentence
     *
     * @param ta The TextAnnotation containing the sentences
     * @param node The Stanford Dependency node
     * @param sentId The sentence number
     * @return The token index relative to sentence
     */
    private static int getNodePosition(TextAnnotation ta, IndexedWord node, int sentId) {
        int sentenceStart = ta.getView(ViewNames.SENTENCE).getConstituents().get(sentId).getStartSpan();
        int nodeCharacterOffset = node.beginPosition();
        int tokenStartSpan = ta.getTokenIdFromCharacterOffset(nodeCharacterOffset);
        return tokenStartSpan - sentenceStart;
    }
}
