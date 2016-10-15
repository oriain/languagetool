package org.languagetool.parsers;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.JLanguageTool;
import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.core.exception.MaltChainedException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by littl on 5/28/2016.
 */
public class MaltParser implements DependencyParser {
    private ConcurrentMaltParserModel parser;

    MaltParser(String filename) throws DependencyParserException {
        URL model = JLanguageTool.getDataBroker().getFromResourceDirAsUrl(filename);
        try {
            parser = ConcurrentMaltParserService.initializeParserModel(model);
        } catch (MaltChainedException e) {
            throw new DependencyParserException(e);
        }
    }

    protected ConcurrentDependencyGraph parse(String[] tokens) throws DependencyParseException {
        ConcurrentDependencyGraph graph;
        try {
            graph = parser.parse(tokens);
        } catch (MaltChainedException e) {
            throw new DependencyParseException(e);
        }
        return graph;
    }

    private String[] toCoNLL(AnalyzedTokenReadings[] tokens) {
        ArrayList<String> wordLines = new ArrayList<>();

        int tokenNumber = 0;
        for (AnalyzedTokenReadings token : tokens) {
            // Also skip over whitespace and sentence start tokens.
            if (token.isWhitespace() || token.isSentenceStart()) continue;

            // Increment the token number.
            tokenNumber++;

            // A single word may have multiple interpretations.
            // We need to extract the Lemma and Pos for each.
            TreeMap<Integer, String> lemmas = new TreeMap<>();
            TreeMap<Integer, String> pos = new TreeMap<>();

            // Loop over all possible readings to extract the relevant info.
            List<AnalyzedToken> readings = token.getReadings();
            for (int i=0; i<readings.size(); i++) {
                AnalyzedToken reading = readings.get(i);

                // Reading ID.
                int readingId = i+1;

                // Lemma
                String lemma = reading.getLemma();
                if (lemma == null || lemma.isEmpty()) lemma = "_";
                lemmas.put(readingId, lemma);

                // C-POS and F-POS
                pos.put(readingId, reading.getPOSTag());
            }

            if (lemmas.size() < 1)   lemmas.put(1, "_");
            if (pos.size() < 1)      pos.put(1, "_");

            // Grab all the parts for output in CoNLL format.
            String tokenId = tokenNumber + "";
            String wordform = token.getToken();
            String lemma = lemmas.size() > 1 ? lemmas.toString() : lemmas.get(1);
            String partOfSpeech = pos.size() > 1 ? pos.toString() : pos.get(1);
            String underscore = "_";

            // Add the info to the appropriate column.
            ArrayList<String> columns = new ArrayList<>();
            columns.add(tokenId);       // Token Number
            columns.add(wordform);      // Wordform
            columns.add(lemma);         // Lemma
            columns.add(partOfSpeech);  // C-POS
            columns.add(partOfSpeech);  // F-POS
            columns.add(underscore);    // Features
            columns.add(underscore);    // Head
            columns.add(underscore);    // DepRel
            columns.add(underscore);    // Expansions 1
            columns.add(underscore);    // Expansion 2

            // Return a tab separated string of all the CoNLL values.
            String wordline = StringUtils.join(columns, "\t");
            wordLines.add(wordline);
        }

        return wordLines.toArray(new String[wordLines.size()]);
    }

    private String[] toCoNLL(List<AnalyzedTokenReadings> tokens) {
        return toCoNLL(tokens.toArray(new AnalyzedTokenReadings[tokens.size()]));
    }

    @Override
    public void addDependencies(List<AnalyzedTokenReadings> tokens) throws DependencyParseException {
        String[] conll = toCoNLL(tokens);
        ConcurrentDependencyGraph graph = parse(conll);
        // Analyzed Token Readings don't currently accomodate dependency information...
        // TODO: Add support for dependency information in the AnalyzedTokenReadings class?
        throw new NotImplementedException();
    }

    @Override
    public void addDependencies(AnalyzedSentence sentence) throws DependencyParseException {
        AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();
        String[] conll = toCoNLL(tokens);
        ConcurrentDependencyGraph graph = parse(conll);
        // Analyzed Token Readings don't currently accomodate dependency information...
        // TODO: Add support for dependency information in the AnalyzedTokenReadings class?
        throw new NotImplementedException();
    }
}
