package org.languagetool.tagging.it;

import org.apache.commons.lang.StringUtils;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by littl on 5/21/2016.
 */
class ItalianToken {
    private AnalyzedTokenReadings source;
    private ItalianReading[] readings;

    ItalianToken(AnalyzedTokenReadings token) {
        this.source = token;

        // Convert the readings to Italian readings
        ArrayList<ItalianReading> italianReadings = new ArrayList<>();
        List<AnalyzedToken> readings = this.source.getReadings();
        for (AnalyzedToken reading : readings) {
            ItalianReading italianReading = new ItalianReading(reading);
            italianReadings.add(italianReading);
        }
        this.readings = italianReadings.toArray(new ItalianReading[italianReadings.size()]);
    }

    // Process each reading for information required by the CoNLL-X format.
    // The columns are as follows: ID, Form, Lemma, C-POS, F-POS, Features, Head, DepRel, P-Head, P-DepRel
    String toCoNLL() {

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
            lemmas.put(readingId, lemma);

            // C-POS and F-POS
            pos.put(readingId, reading.getPosString());

            // features
            String featuresString = reading.getFeaturesString();
            features.put(readingId, featuresString);
        }

        // Grab all the parts for output in CoNLL format.
        // Token ID is not known at this level.
        String wordform = this.source.getToken();
        String lemma = lemmas.size() > 1 ? lemmas.toString() : lemmas.get(1);
        String partOfSpeech = pos.size() > 1 ? pos.toString() : pos.get(1);
        String feat = features.size() > 1 ? features.toString() : features.get(1);
        String underscore = "_";

        // Add the info to the appropriate column.
        ArrayList<String> columns = new ArrayList<>();
        columns.add(wordform);      // Wordform
        columns.add(lemma);         // Lemma
        columns.add(partOfSpeech);  // C-POS
        columns.add(partOfSpeech);  // F-POS
        columns.add(feat);          // features
        columns.add(underscore);    // Head
        columns.add(underscore);    // DepRel
        columns.add(underscore);    // Expansions 1
        columns.add(underscore);    // Expansion 2

        // Return a tab separated string of all the CoNLL values.
        return StringUtils.join(columns, "\t");
    }

    boolean agreesWith(ItalianToken other) {

        // Loop through all readings for this token.
        for (ItalianReading reading : this.readings) {
            // Compare it against all possible readings of the other token.
            for (int j = 0; j < other.readings.length; j++) {
                ItalianReading otherReading = other.readings[j];
                boolean readingsAgree = reading.agreesWith(otherReading);
                if (readingsAgree) return true;
            }
        }

        // If no readings agreed, then return false;
        return false;
    }
}
