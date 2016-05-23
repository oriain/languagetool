package org.languagetool.tagging.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;

import java.util.ArrayList;

/**
 * Created by littl on 5/21/2016.
 */
public class ItalianSentence {
    private AnalyzedSentence source;
    private ItalianToken[] tokens;
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
            ItalianToken italianToken = new ItalianToken(token);
            italianTokens.add(italianToken);
        }
        this.tokens = italianTokens.toArray(new ItalianToken[italianTokens.size()]);
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

    public void setDependencyGraph(ConcurrentDependencyGraph graph) {
        this.graph = graph;
    }

    public boolean checkAgreement() {

        // Loop through the tokens to check agreement.
        // Start at index 1 to skip over the blank root node.
        for (int i = 1; i < this.graph.nTokenNodes(); i++) {
            ConcurrentDependencyNode node = this.graph.getTokenNode(i);

            // At the moment, we only care about subject-verb agreement, for the moment.
            boolean isSubject = node.getLabel(7).contains("SUBJ");
            if (isSubject) {

                // Output subject information
                String word = node.getLabel(1);
                String pos = node.getLabel(3);
                String features = node.getLabel(5);
                System.out.printf("Subject: %s\t%s\t%s%n", word, pos, features);

                // Output parent information.
                ConcurrentDependencyNode head = node.getHead();
                word = head.getLabel(1);
                pos = head.getLabel(3);
                features = head.getLabel(5);
                System.out.printf("Parent: %s\t%s\t%s%n", word, pos, features);

                // Check to see of the head is a verb.
                boolean isVerb = head.getLabel(3).contains("VER");
                if (!isVerb) {
                    System.out.println("The parent of the subject is not a verb");
                    return false;
                }
                System.out.println("The parent of the subject is a verb.");

                // Use the token id of the head graph node to
                // retrieve the Italian token representing the verb.
                String headTokenId = head.getLabel(0);
                int headId = Integer.parseInt(headTokenId);
                ItalianToken verb = this.tokens[headId];

                // Grab the Italian token for the subject.
                ItalianToken subject = this.tokens[i];

                // Now that we have Italian tokens for the subject and verb, check agreement.
                boolean hasAgreement = verb.agreesWith(subject);
                if (!hasAgreement) {
                    System.out.println("The subject's features do not agree with the verb's features.");
                    return false;
                }
                System.out.println("The subject's features agree with the verb's features.");

            } // End of subject analysis.
        } // Done checking all the tokens in a sentence.

        return true;
    }
}
