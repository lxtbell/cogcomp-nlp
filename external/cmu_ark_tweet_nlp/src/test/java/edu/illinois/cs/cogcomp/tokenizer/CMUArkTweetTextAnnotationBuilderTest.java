package edu.illinois.cs.cogcomp.tokenizer;

import junit.framework.Assert;

import org.junit.Test;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * @author Xiaotian Le
 */
public class CMUArkTweetTextAnnotationBuilderTest {

    public static final String RAW_TEXT = ":o :/ :'( >:o (: :) >.< XD -__- o.O ;D :-) @_@ :P 8D :1 >:( :D =| \") :> ....";

    @Test
    public void testCreateTextAnnotation() {
        TextAnnotation ta = new CMUArkTweetTextAnnotationBuilder().createTextAnnotation(RAW_TEXT);
        Assert.assertEquals(ta.getTokens().length, 25);
        Assert.assertEquals(ta.getToken(0), ":o");
    }
}
