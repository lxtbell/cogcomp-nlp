/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computation Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.ritter;

import java.util.HashMap;
import java.util.Map;

import edu.illinois.cs.cogcomp.nlp.corpusreaders.conllReader.conll2003.CoNLL2003NERConfigurator;

/**
 * Defines Alan Ritter's twitter_nlp NER tags, and their conversion to CoNLL 2003 tags
 *
 * @author Xiaotian Le
 */
public class RitterNERConfigurator {

    public enum Tags {
        PERSON("person"), GEO_LOC("geo-loc"), COMPANY("company"), PRODUCT("product"), FACILITY("facility"),
        TV_SHOW("tvshow"), MOVIE("movie"), SPORTS_TEAM("sportsteam"), MUSIC_ARTIST("musicartist"), OTHER("other");

        public String label;
        Tags(String label) {
            this.label = label;
        }
    }

    private static final Map<String, String> TO_CONLL_MAPPING_3 = new HashMap<>();
    static {
        TO_CONLL_MAPPING_3.put(Tags.MUSIC_ARTIST.label, CoNLL2003NERConfigurator.Tags.PERSON.label);
        TO_CONLL_MAPPING_3.put(Tags.PERSON.label, CoNLL2003NERConfigurator.Tags.PERSON.label);

        TO_CONLL_MAPPING_3.put(Tags.FACILITY.label, CoNLL2003NERConfigurator.Tags.LOCATION.label);
        TO_CONLL_MAPPING_3.put(Tags.GEO_LOC.label, CoNLL2003NERConfigurator.Tags.LOCATION.label);

        TO_CONLL_MAPPING_3.put(Tags.COMPANY.label, CoNLL2003NERConfigurator.Tags.ORGANIZATION.label);
        TO_CONLL_MAPPING_3.put(Tags.PRODUCT.label, CoNLL2003NERConfigurator.Tags.ORGANIZATION.label);
        TO_CONLL_MAPPING_3.put(Tags.SPORTS_TEAM.label, CoNLL2003NERConfigurator.Tags.ORGANIZATION.label);
    }

    private static final Map<String, String> TO_CONLL_MAPPING_4 = new HashMap<>();
    static {
        TO_CONLL_MAPPING_4.put(Tags.MUSIC_ARTIST.label, CoNLL2003NERConfigurator.Tags.PERSON.label);
        TO_CONLL_MAPPING_4.put(Tags.PERSON.label, CoNLL2003NERConfigurator.Tags.PERSON.label);

        TO_CONLL_MAPPING_4.put(Tags.FACILITY.label, CoNLL2003NERConfigurator.Tags.LOCATION.label);
        TO_CONLL_MAPPING_4.put(Tags.GEO_LOC.label, CoNLL2003NERConfigurator.Tags.LOCATION.label);

        TO_CONLL_MAPPING_4.put(Tags.COMPANY.label, CoNLL2003NERConfigurator.Tags.ORGANIZATION.label);
        TO_CONLL_MAPPING_4.put(Tags.PRODUCT.label, CoNLL2003NERConfigurator.Tags.ORGANIZATION.label);
        TO_CONLL_MAPPING_4.put(Tags.SPORTS_TEAM.label, CoNLL2003NERConfigurator.Tags.ORGANIZATION.label);

        TO_CONLL_MAPPING_4.put(Tags.MOVIE.label, CoNLL2003NERConfigurator.Tags.MISC.label);
        TO_CONLL_MAPPING_4.put(Tags.TV_SHOW.label, CoNLL2003NERConfigurator.Tags.MISC.label);
        TO_CONLL_MAPPING_4.put(Tags.OTHER.label, CoNLL2003NERConfigurator.Tags.MISC.label);
    }

    private static final Map<String, String> FROM_CONLL_MAPPING = new HashMap<>();
    static {
        FROM_CONLL_MAPPING.put(CoNLL2003NERConfigurator.Tags.PERSON.label, Tags.PERSON.label);
        FROM_CONLL_MAPPING.put(CoNLL2003NERConfigurator.Tags.LOCATION.label, Tags.GEO_LOC.label);
        FROM_CONLL_MAPPING.put(CoNLL2003NERConfigurator.Tags.ORGANIZATION.label, Tags.COMPANY.label);
        FROM_CONLL_MAPPING.put(CoNLL2003NERConfigurator.Tags.MISC.label, Tags.OTHER.label);
    }

    public static String convertToCoNLL3(String ner) {
        return TO_CONLL_MAPPING_3.getOrDefault(ner, null);
    }

    public static String convertToCoNLL4(String ner) {
        return TO_CONLL_MAPPING_4.getOrDefault(ner, null);
    }

    public static String convertFromCoNLL(String ner) {
        return FROM_CONLL_MAPPING.getOrDefault(ner, null);
    }
}
