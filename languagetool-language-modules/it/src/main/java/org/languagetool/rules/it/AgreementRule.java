package org.languagetool.rules.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.tagging.it.CoNLL;
import org.languagetool.tagging.it.ItalianReading;
import org.languagetool.tagging.it.ItalianSentence;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.PartOfSpeech;
import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.core.exception.MaltChainedException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by littl on 5/21/2016.
 */
public class AgreementRule extends Rule {
    private ArrayList<RuleMatch> ruleMatches;
    private ArrayList<AgreementRelationship> relationships;

    public AgreementRule(ResourceBundle messages) {
        super(messages);
        ruleMatches = new ArrayList<>();

        // Construct subject-verb agreement
        AgreementRelationship subjectVerbAgreement = new AgreementRelationship();
        subjectVerbAgreement.description = "The subject and verb do not agree.";
        subjectVerbAgreement.childPos.add(PartOfSpeech.NOUN);
        subjectVerbAgreement.relation.add(DependencyRelation.SUBJ);
        subjectVerbAgreement.parentPos.add(PartOfSpeech.VER);

        // Construct noun-article agreement
        AgreementRelationship nounAdjectiveAgreement = new AgreementRelationship();
        nounAdjectiveAgreement.description = "The adjective and the noun it modifies do not agree.";
        nounAdjectiveAgreement.childPos.add(PartOfSpeech.ADJ);
        nounAdjectiveAgreement.relation.add(DependencyRelation.RMOD);
        nounAdjectiveAgreement.parentPos.add(PartOfSpeech.NOUN);

        // Construct noun-determiner agreement
        AgreementRelationship nounDeterminerAgreement = new AgreementRelationship();
        nounDeterminerAgreement.childPos.add(PartOfSpeech.NOUN);
        nounDeterminerAgreement.relation.add(DependencyRelation.ARG);
        nounDeterminerAgreement.description = "The determiner and the noun it modifies do not agree.";
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.DET_DEMO);
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.DET_POSS);
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.DET_WH);
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.DET_INDEF);
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.DET_NUM_CARD);
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.ART);
        nounDeterminerAgreement.parentPos.add(PartOfSpeech.ARTPRE);

        // Add the relationships whose agreement needs to be checked.
        relationships = new ArrayList<>();
        relationships.add(subjectVerbAgreement);
        relationships.add(nounAdjectiveAgreement);
        relationships.add(nounDeterminerAgreement);
    }

    @Override
    public String getId() {
        return "IT_AGREEMENT";
    }

    @Override
    public String getDescription() {
        return "INSERT AN ITALiAN RULE DESCRIPTION HERE.";
    }

    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) throws IOException {
        // Convert the analyzed sentence to an Italian sentence. Italian
        // sentences have Italian tokens, which have Italian specific features.
        ItalianSentence italianSentence = ItalianSentence.create(sentence);
        String[] tokens = italianSentence.toCoNLL();

        // Load the model, initialize the parser, and parse the tokens.
        String filename = "/it/italian.mco";
        URL model = JLanguageTool.getDataBroker().getFromResourceDirAsUrl(filename);
        //URL model = new File(filename).toURI().toURL();
        ConcurrentMaltParserModel parser;
        ConcurrentDependencyGraph graph = null;
        try {
            parser = ConcurrentMaltParserService.initializeParserModel(model);
            graph = parser.parse(tokens);
        } catch (MaltChainedException e) {
            e.printStackTrace();
        }

        if (graph == null) {
            //System.out.println("Could not parse the sentence tokens.");
            return new RuleMatch[0];
        }

        // Write the tokens to a file for viewing in MatlEval.
        //System.out.println("Outputting tokens for viewing.");
        CoNLL.writeFile(graph, "tokens.conl");

        italianSentence.setDependencyGraph(graph);
        boolean hasValidAgreement = checkAgreement(italianSentence);
        if (hasValidAgreement) {
            //System.out.println("No agreement violations in the sentence were detected.");
            return new RuleMatch[0];
        }

        //System.out.println("Agreement violations in the sentence were detected.");
        return ruleMatches.toArray(new RuleMatch[ruleMatches.size()]);
    }

    @Override
    public void reset() {

    }

    private boolean checkAgreement(ItalianSentence sentence) {

        // We need to check each type of agreement rule to see if the
        // tokens in the sentence fall into that type of agreement check.
        for (AgreementRelationship relationship : relationships) {
            for (ItalianToken child : sentence.tokens) {

                // The token's POS must be covered by the agreement relationship type.
                ItalianReading[] validChildReadings = relationship.getValidChildReadings(child);
                if (validChildReadings.length == 0) continue;

                // If this token is the root (represented by having a null parent)
                // then there is no agreement to be checked.
                ItalianToken parent = child.head;
                if (parent == null) continue;

                // Otherwise the parent token's POS must also be covered by the agreement relationship type.
                ItalianReading[] validParentReadings = relationship.getValidParentReadings(parent);
                if (validParentReadings.length == 0) continue;

                // Lastly, the child's relationship to the parent must
                // also be covered by the agreement relationship type.
                if (!relationship.isValidDependencyRelation(child)) continue;

                // If all three requirements are met, then check for agreeable pairs.
                boolean hasAgreeablePair = false;
                for (ItalianReading childReading : validChildReadings) {
                    for (ItalianReading parentReading : validParentReadings)
                        if (childReading.agreesWith(parentReading))
                            hasAgreeablePair = true;
                }

                // If the words disagree, generate a rule match.
                if (!hasAgreeablePair) {
                    int start = parent.source.getStartPos();
                    int end = parent.source.getEndPos();
                    RuleMatch ruleMatch = new RuleMatch(this, start, end, relationship.description);
                    //ruleMatch.setSuggestedReplacement("replacement (TODO)");
                    ruleMatch.setSuggestedReplacement("Paired word: " + child.source.getToken());
                    this.ruleMatches.add(ruleMatch);
                }
            } // Done looping through the tokens in the sentence.
        }  // Done looping through the various types of agreement relationships.

//        // Loop through the tokens to check agreement.
//        // Start at index 1 to skip over the blank root node.
//        ConcurrentDependencyGraph graph = sentence.getDependencyGraph();
//        for (int i = 1; i < graph.nTokenNodes(); i++) {
//            ConcurrentDependencyNode node = graph.getTokenNode(i);
//
//            // At the moment, we only care about subject-verb agreement.
//            boolean isSubject = node.getLabel(7).contains("SUBJ");
//            if (isSubject) {
//
//                // Output subject information
//                String word = node.getLabel(1);
//                String pos = node.getLabel(3);
//                String features = node.getLabel(5);
//                //System.out.printf("Subject: %s\t%s\t%s%n", word, pos, features);
//
//                // Output parent information.
//                ConcurrentDependencyNode head = node.getHead();
//                word = head.getLabel(1);
//                pos = head.getLabel(3);
//                features = head.getLabel(5);
//                //System.out.printf("Parent: %s\t%s\t%s%n", word, pos, features);
//
//                // Check to see of the head is a verb.
//                boolean isVerb = head.getLabel(3).contains("VER");
//                if (!isVerb) {
//                    //System.out.println("The parent of the subject is not a verb");
//                    continue;
//                }
//                //System.out.println("The parent of the subject is a verb.");
//
//                // Use the token id of the head graph node to
//                // retrieve the Italian token representing the verb.
//                String headTokenId = head.getLabel(0);
//                int headId = Integer.parseInt(headTokenId);
//                // token id is off by one since the sentence doesn't track the root as a token.
//                ItalianToken verb = sentence.tokens[headId-1];
//
//                // Grab the Italian token for the subject.
//                // The ItalianSentence object doesn't track the root node, so we need to offset by one.
//                ItalianToken subject = sentence.tokens[i-1];
//
//                // Now that we have Italian tokens for the subject and verb, check agreement.
//                boolean hasAgreement = verb.agreesWith(subject);
//                if (hasAgreement) {
//                    //System.out.println("The subject's features agree with the verb's features.");
//                    continue;
//                }
//
//                //System.out.println("The subject's features do not agree with the verb's features.");
//
//                // Add a match for the sentence.
//                AnalyzedTokenReadings analyzedTokenReadings = verb.getAnalyzedTokenReadings();
//                int start = analyzedTokenReadings.getStartPos();
//                int end = analyzedTokenReadings.getEndPos();
//                RuleMatch ruleMatch = new RuleMatch(this, start, end, "The verb does not match with the subject.");
//                ruleMatch.setSuggestedReplacement("replacement");
//                this.ruleMatches.add(ruleMatch);
//
//            } // End of subject analysis.
//        } // Done checking all the tokens in a sentence.

        return ruleMatches.size() < 1;
    }
}
