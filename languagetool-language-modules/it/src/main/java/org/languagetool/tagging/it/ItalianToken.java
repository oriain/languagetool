package org.languagetool.tagging.it;

import org.apache.commons.lang.StringUtils;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.PartOfSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by littl on 5/21/2016.
 */
public class ItalianToken extends AnalyzedTokenReadings {
    public ItalianReading[] readings;
    public ItalianToken head;
    public DependencyRelation dependencyRelation;
    private ArrayList<ItalianToken> children = new ArrayList<>();
    private int tokenNumber;

    private ItalianToken(AnalyzedTokenReadings token) {
        // It would be nice if a copy constructor were provided.
        // Stole this code from the copy() method of AnalyzedSentence.
        super(token.getReadings(), token.getStartPos());
        this.setHistoricalAnnotations(token.getHistoricalAnnotations());
        this.setChunkTags(token.getChunkTags());
        if (token.isImmunized()) this.immunize();
        if (token.isIgnoredBySpeller()) this.ignoreSpelling();
        this.setWhitespaceBefore(token.isWhitespaceBefore());
    }

    public static ItalianToken create(AnalyzedTokenReadings token, int tokenNumber) {
        ItalianToken italianToken = new ItalianToken(token);
        italianToken.tokenNumber = tokenNumber;
        italianToken.initializeItalianReadings();
        return italianToken;
    }

    private void initializeItalianReadings() {
        // Convert the readings to Italian readings
        ArrayList<ItalianReading> italianReadings = new ArrayList<>();
        List<AnalyzedToken> readings = this.getReadings();
        for (AnalyzedToken reading : readings) {
            ItalianReading italianReading = new ItalianReading(reading);
            italianReadings.add(italianReading);
        }
        this.readings = italianReadings.toArray(new ItalianReading[italianReadings.size()]);
    }

    public void addChild(ItalianToken child) {
        children.add(child);
    }

    public ItalianToken[] getChildren() {
        return this.children.toArray(new ItalianToken[this.children.size()]);
    }

    // Process each reading for information required by the CoNLL-X format.
    // The columns are as follows: ID, Form, Lemma, C-POS, F-POS, Features, Head, DepRel, P-Head, P-DepRel
    String toCoNLL(boolean useRoot) {

        // A single word may have multiple interpretations.
        // We need to extract the Lemma, Pos and features for each.
        TreeMap<Integer, String> lemmas = new TreeMap<>();
        TreeMap<Integer, String> pos = new TreeMap<>();
        TreeMap<Integer, String> features = new TreeMap<>();

        // Loop over all possible readings to extract the relevant info.
        for (int i=0; i<this.readings.length; i++) {
            ItalianReading reading = this.readings[i];

            // Reading ID.
            int readingId = i+1;

            // Lemma
            String lemma = reading.getLemma();
            if (lemma == null || lemma.isEmpty()) lemma = "_";
            lemmas.put(readingId, lemma);

            // C-POS and F-POS
            pos.put(readingId, reading.getPosString());

            // features
            String featuresString = reading.getFeaturesString();
            features.put(readingId, featuresString);
        }

        if (lemmas.size() < 1)   lemmas.put(1, "_");
        if (pos.size() < 1)      pos.put(1, "_");
        if (features.size() < 1) features.put(1, "_");

        // Grab all the parts for output in CoNLL format.
        // Token ID is not known at this level.
        String wordform = this.getToken();
        String lemma = lemmas.size() > 1 ? lemmas.toString() : lemmas.get(1);
        String partOfSpeech = pos.size() > 1 ? pos.toString() : pos.get(1);
        partOfSpeech = partOfSpeech.replace(" ", "");
        String feat = features.size() > 1 ? features.toString() : features.get(1);
        String underscore = "_";

        // Fill in dependency information.
        String head = underscore;
        if (this.head != null) {
            head = this.head.tokenNumber+"";
        }
        else if (useRoot) {
            head = 0 + "";
        }
        String depRel = underscore;
        if (this.dependencyRelation != null) {
            depRel = this.dependencyRelation.toString().replace("_", "+");
        }


        // Add the info to the appropriate column.
        ArrayList<String> columns = new ArrayList<>();
        columns.add(wordform);      // Wordform
        columns.add(lemma);         // Lemma
        columns.add(partOfSpeech);  // C-POS
        columns.add(partOfSpeech);  // F-POS
        columns.add(feat);          // features
        columns.add(head);    // Head
        columns.add(depRel);    // DepRel
        columns.add(underscore);    // Expansions 1
        columns.add(underscore);    // Expansion 2

        // Return a tab separated string of all the CoNLL values.
        return StringUtils.join(columns, "\t");
    }

    public String toCoNLL() {
        return toCoNLL(false);
    }

    public boolean agreesWith(ItalianToken other) {

        // Loop through all readings for this token.
        for (ItalianReading reading : this.readings) {

            // Compare it against all possible readings of the other token.
            for (ItalianReading otherReading : other.readings) {

                // If the readings agree, return true, otherwise keep checking.
                boolean readingsAgree = reading.agreesWith(otherReading);
                if (readingsAgree) return true;
            }
        }

        // If no readings agreed, then return false;
        return false;
    }

    // Checks to see if the token has AVERE as a lemma.
    public boolean isAvere() {
        for(ItalianReading reading : this.readings) {
            if (reading.getLemma().equalsIgnoreCase("AVERE")) {
                return true;
            }
        }
        return false;
    }

    public boolean isModal() {
        for (ItalianReading reading : this.readings) {
            if (reading.pos == PartOfSpeech.MOD) return true;
        }
        return false;
    }
}
