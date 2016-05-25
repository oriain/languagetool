package org.languagetool.tagging.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.Feature;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.util.ArrayList;

/**
 * Created by littl on 5/21/2016.
 */
public class ItalianSentence {
    private AnalyzedSentence source;
    public ItalianToken[] tokens;
    private ConcurrentDependencyGraph graph;

    // Constructor is private to force use of the create() method.
    private ItalianSentence(AnalyzedSentence sentence) {
        this.source = sentence;
    }

    // Use of a create() method prevents construction of an
    // object without initialization of requisite Italian tokens.
    public static ItalianSentence create(AnalyzedSentence sentence) {
        ItalianSentence italianSentence = new ItalianSentence(sentence);
        italianSentence.initializeItalianTokens();
        return italianSentence;
    }

    private void initializeItalianTokens() {
        // Convert AnalyzedTokenReadings to Italian Tokens.
        ArrayList<ItalianToken> italianTokens = new ArrayList<>();
        AnalyzedTokenReadings[] tokens = this.source.getTokensWithoutWhitespace();
        for (int i=1; i<tokens.length; i++) {
            AnalyzedTokenReadings token = tokens[i];
            ItalianToken italianToken = ItalianToken.create(token);
            italianTokens.add(italianToken);
        }
        this.tokens = italianTokens.toArray(new ItalianToken[italianTokens.size()]);
    }

    public void setDependencyGraph(ConcurrentDependencyGraph graph) {
        this.graph = graph;

        // Update the Italian tokens to store the dependency relation.
        for (int i = 1; i<graph.nTokenNodes(); i++) { // Start at 1 to skip over the root node.

            // Get the tokens.
            ConcurrentDependencyNode graphNode = graph.getTokenNode(i);
            ItalianToken italianToken = tokens[i-1];

            // Update the head of the token.
            int headId = graphNode.getHeadIndex();
            if (headId > 0) italianToken.head = tokens[headId-1];

            // Update the dependency relation.
            String depRel = graphNode.getLabel(7);
            try {
                italianToken.dependencyRelation = DependencyRelation.valueOf(depRel);
            } catch (IllegalArgumentException ignored) { }
        }
    }

    public ConcurrentDependencyGraph getDependencyGraph() {
        return this.graph;
    }

    public String[] toCoNLL() {
        ArrayList<String> wordLines = new ArrayList<>();

        // Output the token number and the rest of the token
        // information, which is already in CoNLL-X format.
        for (int i=0; i<tokens.length; i++) {
            ItalianToken token = tokens[i];
            wordLines.add((i+1) + "\t" + token.toCoNLL());
        }

        return wordLines.toArray(new String[wordLines.size()]);
    }
}
