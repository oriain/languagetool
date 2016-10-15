package org.languagetool.rules.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.synthesis.ItalianSynthesizer;
import org.languagetool.tagging.it.CoNLL;
import org.languagetool.tagging.it.ItalianReading;
import org.languagetool.tagging.it.ItalianSentence;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.PartOfSpeech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by littl on 5/21/2016.
 */
public class AgreementRule extends Rule {
    // TODO: Handle coordinating conjunction (false and true positives).
    private ArrayList<RuleMatch> ruleMatches;
    private ArrayList<AgreementRelationship> relationships;

    public AgreementRule(ResourceBundle messages) {
        super(messages);
        ruleMatches = new ArrayList<>();

        // Construct subject-verb agreement
        AgreementRelationship subjectVerbAgreement = new SubjectVerbAgreement();

        // Construct noun-adjective agreement
        AgreementRelationship nounAdjectiveAgreement = new AgreementRelationship();
        nounAdjectiveAgreement.description = "The adjective and the noun it modifies do not agree.";
        nounAdjectiveAgreement.childPos.add(PartOfSpeech.ADJ);
        nounAdjectiveAgreement.relation.add(DependencyRelation.RMOD);
        nounAdjectiveAgreement.parentPos.add(PartOfSpeech.NOUN);

        // Construct noun-determiner agreement
        AgreementRelationship nounDeterminerAgreement = new AgreementRelationship();
        nounDeterminerAgreement.childPos.add(PartOfSpeech.NOUN);
        nounDeterminerAgreement.relation.add(DependencyRelation.ARG);
        nounDeterminerAgreement.description = "The noun and the determiner it modifies do not agree.";
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
        // sentences have Italian tokens, which have Italian specific information.
        ItalianSentence italianSentence = ItalianSentence.create(sentence);
        return match(italianSentence);
    }

    public RuleMatch[] match(ItalianSentence sentence) throws IOException {
        // Write the tokens to a file for viewing in MatlEval.
        //System.out.println("Outputting tokens for viewing.");
        CoNLL.writeFile(sentence, "tokens.conl");

        boolean hasValidAgreement = checkAgreement(sentence);
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

    private boolean checkAgreement(ItalianSentence sentence) throws IOException {

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
                boolean hasAgreeablePair = checkForAgreeablePair(validChildReadings, validParentReadings);

                // If the words disagree, generate a rule match.
                if (!hasAgreeablePair) {
                    // Make check for exemptions to this agreement relationship.
                    boolean isExempt = relationship.checkForExemption(child, parent);
                    if (isExempt) continue;

                    // We need to generate suggestions for replacement.
                    // Use the features from the child overlaid onto the parent.
                    ArrayList<String> replacements = generateReplacements(validChildReadings, validParentReadings);

                    // Construct the rule match to replace the parent.
                    int start = parent.getStartPos();
                    int end = parent.getEndPos();
                    RuleMatch ruleMatch = new RuleMatch(this, start, end, relationship.description);
                    if (replacements.size() > 0) ruleMatch.setSuggestedReplacements(replacements);
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

        // We also need to check for AvereAgreemnt.
        AvereAgreement avereAgreement = new AvereAgreement(this);
        RuleMatch[] match = avereAgreement.match(sentence);
        Collections.addAll(ruleMatches, match);

        return ruleMatches.size() < 1;
    }

    boolean checkForAgreeablePair(ItalianReading[] childReadings, ItalianReading[] parentReadings) {
        // If all three requirements are met, then check for agreeable pairs.
        for (ItalianReading childReading : childReadings) {
            for (ItalianReading parentReading : parentReadings) {
                if (childReading.agreesWith(parentReading)) {
                    return true;
                }
            }
        }
        return false;
    }

    ArrayList<String> generateReplacements(ItalianReading[] childReadings, ItalianReading[] parentReadings) throws IOException {
        ArrayList<String> replacements = new ArrayList<>();
        for (ItalianReading parentReading : parentReadings) {
            for (ItalianReading childReading : childReadings) {

                // Create a copy of the reading being replaced
                ItalianReading suggestion = new ItalianReading(parentReading);

                // Pull in the features from the controlling word.
                suggestion.matchOverlappingFeatures(childReading);

                // Construct the morphology for the suggestion.
                // Hopefully it will be found in the dictionary.
                String posTag = suggestion.generatePosTag();

                // Synthesize the suggested word.
                ItalianSynthesizer synthesizer = new ItalianSynthesizer();
                String[] suggestions = synthesizer.synthesize(suggestion.analyzedToken, posTag);

                if (suggestions.length > 0) Collections.addAll(replacements, suggestions);
            }
        }
        return replacements;
    }
}
