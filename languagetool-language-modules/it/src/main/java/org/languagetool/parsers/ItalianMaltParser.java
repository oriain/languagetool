package org.languagetool.parsers;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by littl on 5/29/2016.
 */
public class ItalianMaltParser extends MaltParser {

    public ItalianMaltParser(String filename) throws DependencyParserException {
        super(filename);
    }

    // Process each reading for information required by the CoNLL-X format.
    // The columns are as follows: ID, Form, Lemma, C-POS, F-POS, Features, Head, DepRel, P-Head, P-DepRel
    private String[] toCoNLL(List<ItalianToken> tokens) {
        ArrayList<String> wordLines = new ArrayList<>();

        // Skip over the sentence_start token by starting at 1.
        int tokenNumber=0;
        for (AnalyzedTokenReadings analyzedTokenReadings : tokens) {
            // For the Italian MaltParser, we only care about processing ItalianToken obects.
            if (!(analyzedTokenReadings instanceof ItalianToken)) continue;

            // If we are dealing with an ItalianToken, increment the token number and convert.
            tokenNumber++;
            ItalianToken token = (ItalianToken) analyzedTokenReadings;

            // Construct the wordline
            String wordline = tokenNumber + "\t" + token.toCoNLL();

            // Add the wordline to the list.
            wordLines.add(wordline);
        }

        // Return the wordlines as an array.
        return wordLines.toArray(new String[wordLines.size()]);
    }

    @Override
    public void addDependencies(AnalyzedSentence sentence) throws DependencyParseException {
        addDependencies(Arrays.asList(sentence.getTokensWithoutWhitespace()));
    }

    @Override
    public void addDependencies(List<AnalyzedTokenReadings> tokens) throws DependencyParseException {
        // Dependency parsing only applies to ItalianToken objects.
        List<ItalianToken> italianTokens = new ArrayList<>();
        int tokenId = 1;
        for (int i = 0, tokensLength = tokens.size(); i < tokensLength; i++) {
            AnalyzedTokenReadings token = tokens.get(i);
            // Skip over white space and sentence start tokens.
            if (token.isWhitespace() || token.isSentenceStart()) continue;
            // Convert to ItalianToken and update array with new object.
            ItalianToken italianToken = ItalianToken.create(token, tokenId++);
            tokens.set(i, italianToken);
            italianTokens.add(italianToken);
        }

        // Convert the tokens to CoNLL-X format before parsing.
        String[] conll = toCoNLL(italianTokens);
        ConcurrentDependencyGraph graph = parse(conll);

        // Loop over each node in the graph and store dependency information onto the ItalianToken objects.
        // Skip over the root node by starting the loop at 1.
        for (int i=1; i<=graph.nTokenNodes(); i++) {
            // Retrieve the node.
            ConcurrentDependencyNode node = graph.getTokenNode(i);

            // Retrieve the ItalianToken.
            int id = Integer.parseInt(node.getLabel(0));

            // Offset the token id by one since there is no ItalianToken for the root node in the graph.
            ItalianToken token = italianTokens.get(id-1);

            // Update the dependency relation.
            String depRel = node.getLabel(7)
                    .replace("*", "_")
                    .replace("+", "_")
                    .replace("%", "_")
                    .replace("/", "_");
            try {
                token.dependencyRelation = DependencyRelation.valueOf(depRel);
            } catch (IllegalArgumentException ignored) { }

            // Update the head of the token.
            int headId = node.getHeadIndex();
            if (headId > 0) { // Don't process the root node.
                // Offset the tokenId by one since there is no ItalianToken for the root node in the graph.
                token.head = italianTokens.get(headId-1);
                // Set this token as a child of it's head.
                token.head.addChild(token);
            }
        }
    }
}
