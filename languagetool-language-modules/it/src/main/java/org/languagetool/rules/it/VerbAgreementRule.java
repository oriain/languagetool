package org.languagetool.rules.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.synthesis.ItalianSynthesizer;
import org.languagetool.tagging.it.ItalianReading;
import org.languagetool.tagging.it.ItalianSentence;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.PartOfSpeech;

import java.io.IOException;
import java.util.*;

/**
 * Created by littl on 10/27/2016.
 */
public class VerbAgreementRule extends Rule {
    private static List<String> directObjectPronouns = Arrays.asList(
            "lo",       "la",       "li",       "le",       "l'",       //"ne",
            "melo",     "mela",     "meli",     "mele",                 //"mene",
            "telo",     "tela",     "teli",     "tele",                 //"tene",
            "selo",     "sela",     "seli",     "sele",                 //"sene",
            "glielo",   "gliela",   "glieli",   "gliele",   "gliel'",   //"gliene",
            "celo",     "cela",     "celi",     "cele",                 //"cene",
            "velo",     "vela",     "veli",     "vele"                  //"vene"
    );
    private List<String> getDirectObjectPronouns() {
        return directObjectPronouns;
    }

    private List<DependencyRelation> subjDepRel = new ArrayList<DependencyRelation>();
    private List<DependencyRelation> getSubjDepRel() {
        if (subjDepRel.size() == 0) {
            // Subjects
            subjDepRel.add(DependencyRelation.SUBJ);
            subjDepRel.add(DependencyRelation.SUBJ_OBJ);
            // Objects
            subjDepRel.add(DependencyRelation.OBJ_SUBJ);
            subjDepRel.add(DependencyRelation.PREDCOMPL_OBJ);
        }
        return subjDepRel;
    }

    private List<PartOfSpeech> subjArticlePOS = new ArrayList<PartOfSpeech>();
    private List<PartOfSpeech> getSubjArticlePOS() {
        if (subjArticlePOS.size() == 0) {
            subjArticlePOS.add(PartOfSpeech.ART);//YES-implied, should we check actual attachment?
            subjArticlePOS.add(PartOfSpeech.ARTPRE);

            subjArticlePOS.add(PartOfSpeech.PRO_DEMO);
            subjArticlePOS.add(PartOfSpeech.PRO_INDEF);
            subjArticlePOS.add(PartOfSpeech.PRO_PERS);
            subjArticlePOS.add(PartOfSpeech.PRO_WH);
            subjArticlePOS.add(PartOfSpeech.PRO_POSS);

            subjArticlePOS.add(PartOfSpeech.DET_DEMO);
            subjArticlePOS.add(PartOfSpeech.DET_POSS);
            subjArticlePOS.add(PartOfSpeech.DET_WH);
            subjArticlePOS.add(PartOfSpeech.DET_INDEF);

            // Invariable and don't need to be checked.
            //subjArticlePOS.add(PartOfSpeech.DET_NUM_CARD);
            //subjArticlePOS.add(PartOfSpeech.PRO_NUM);
            //subjArticlePOS.add(PartOfSpeech.WH);
            //subjArticlePOS.add(PartOfSpeech.WH_CHE);
        }
        return subjArticlePOS;
    }

    public VerbAgreementRule(ResourceBundle messages) {
        super(messages);
    }

    @Override
    public String getId() {
        return "IT_VERB_AGREEMENT";
    }

    @Override
    public String getDescription() {
        return "Checks agreement (generally number and gender) between verbs, including modal and auxiliary verbs," +
                " and their subjects and direct object pronouns.";
    }

    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) throws IOException {
        // Convert the analyzed sentence to an Italian sentence. Italian
        // sentences have Italian tokens, which have Italian specific information.
        ItalianSentence italianSentence = ItalianSentence.create(sentence);
        return match(italianSentence);
    }

    public RuleMatch[] match(ItalianSentence sentence) throws IOException {
        List<RuleMatch> ruleMatches = new ArrayList<RuleMatch>();

        // Loop through the tokens to identify the subject, direct object pronoun and verbs.
        for (ItalianToken child : sentence.tokens) {
            // We will iterate over the tokens in a sentence to find pairs of tokens that need to agree.
            List<AgreementPair> agreementPairs = new ArrayList<AgreementPair>();

            // The subject must have a parent (usually a verb of some kind).
            if (child.head == null) continue;

            // TODO: We need to accomodate the scenario where there is no subject in the sentence,
            // but there is anavere auxiliary and verb with no direct object pronoun,
            // meaning the verb should be singular and masculine.

            // Check for subject dependency relations: SUBJ, SUBJ_OBJ, OBJ_SUBJ, PREDCOMPL_OBJ
            if (!getSubjDepRel().contains(child.dependencyRelation)) continue;

            // The subject will control agreement for one or more tokens in a sentence.
            List<ItalianReading> subjectReadings = new ArrayList<ItalianReading>();

            // Was trying to use noun, but sometimes there is only a personal pronoun. So we'll use
            // the token who dependency relation is a subject and is either an article or the noun.
            for (ItalianReading reading : child.readings) {
                // A handful of words can be both a noun and an article: cosa, dei, nei, uno, voi
                if (getSubjArticlePOS().contains(reading.pos) || reading.pos == PartOfSpeech.NOUN) {
                    subjectReadings.add(reading);
                }
            }

            /*
            // Loop through all interpretations of the word and check to
            // see if the interpretation represents a noun or an article.
            boolean hasArticle = false;
            for (ItalianReading reading : child.readings) {
                // A handful of words can be both a noun and an article: cosa, dei, nei, uno, voi
                if (reading.pos == PartOfSpeech.NOUN) {
                    subjectReadings.add(reading);
                }
                if (getSubjArticlePOS().contains(reading.pos)) {
                    hasArticle = true;
                }
            }

            if (hasArticle) {
                // If the token being examined has an article as one of its interpretation,
                // check grandchildren for the real subject (noun).
                Map<ItalianToken, List<ItalianReading>> grandchildren = new HashMap<ItalianToken, List<ItalianReading>>();
                for (ItalianToken grandchild : child.getChildren()) {
                    // The subject must be an argument of the article.
                    if (grandchild.dependencyRelation == DependencyRelation.ARG) {
                        // Loop through all possible readings.
                        for (ItalianReading reading : grandchild.readings) {
                            // We only want children of the article if they are a noun.
                            if (reading.pos == PartOfSpeech.NOUN) {
                                // Add the reading to the set.
                                if (grandchildren.containsKey(grandchild)) {
                                    List<ItalianReading> readings = grandchildren.get(grandchild);
                                    readings.add(reading);
                                } else {
                                    grandchildren.put(grandchild, new ArrayList<ItalianReading>(Arrays.asList(reading)));
                                }
                            }
                        } // Don looping over all the readings
                    }
                } // Done checking all the grandchildren of the article.

                // It's possible there is more than one noun that is an argument of the article.
                if (grandchildren.size() > 0) {
                    // I don't know how to tell which is the "correct" subject, so just use the first one.
                    // Note: HashMap does not guarantee an order, although it doesn't matter in this case.
                    Map.Entry<ItalianToken, List<ItalianReading>> random = grandchildren.entrySet().iterator().next();
                    subjectReadings = random.getValue();
                }
            } // Done with processing the article to find the real subject (noun).
            */

            /* Sometimes the subject is implied.  Even if we can't enforce agreement with the auxiliary, we
             * can still enfore masculine+singular for the verb when no direct object pronoun is present. */
            //// If we don't have a subject, no need to look for associated verbs
            //if (subjectReadings.size() == 0) continue;

            // A variable number of verbs may be associated with the subject.  We can track them by level.
            // Modals, auxiliaries and direct object pronouns are optional, but all sentences must have a verb.
            int level = 0;
            HashMap<Integer, List<ItalianReading>> verbs = new HashMap<Integer, List<ItalianReading>>();

            // We need to track auxiliaries with the lemma of Avere separately from the others.
            HashMap<Integer, List<ItalianReading>> auxiliariesAvere = new HashMap<Integer, List<ItalianReading>>();
            HashMap<Integer, List<ItalianReading>> auxiliariesOther = new HashMap<Integer, List<ItalianReading>>();

            // We also need a HashMap for tracking direct object pronouns.
            HashMap<Integer, List<ItalianReading>> directObjectPronouns = new HashMap<Integer, List<ItalianReading>>();

            // POS tags are used to differentiate between verbs, modals and auxiliaries.
            List<PartOfSpeech> verbPosTags = Arrays.asList(PartOfSpeech.VER, PartOfSpeech.MOD);
            List<PartOfSpeech> auxPosTags = Arrays.asList(PartOfSpeech.AUX);
            // TODO: What about aspectuals (ASP) and causitives (CAU)?

            // We'll use an empty list as a placeholder for participants
            // not identified by POS tags (direct object pronouns).
            List<PartOfSpeech> emptyPosTags = new ArrayList<PartOfSpeech>();

            // Direct Object Pronouns are a closed group of words.
            // The full group of words is saved as a static list on this class.
            // We need an empty list as a placeholder for verbs, modals and auxiliaries.
            List<String> emptyWordGroup = new ArrayList<String>();

            // Direct object pronouns are also identified by the dependency relation of OBJ.
            // Again we'll need an empty list as a placeholder for verbs, modals and auxiliaries.
            List<DependencyRelation> dopRelations = Arrays.asList(DependencyRelation.OBJ);
            List<DependencyRelation> emptyRelations = new ArrayList<DependencyRelation>();

            // The longest chain currently supported has a length of three: modal -> aux -> verb
            // TODO: Add support for longer chains.
            // Chains with aspectuals can have a length of four: modal -> aux -> asp -> verb
            // TODO: Investigate causitive verbs.

            // Process the verb chain as long as necessary, starting with the head of the subject.
            LinkedList<ItalianToken> participants = new LinkedList<ItalianToken>(Arrays.asList(child.head));
            LinkedList<ItalianToken> nextLevel = new LinkedList<ItalianToken>();
            while (participants.size() > 0) {

                // Get the participant from the list.
                ItalianToken participant = participants.remove();

                // Use indirection to save the reading information to the appropriate bucket.
                int bucketLevel = level;
                HashMap<Integer, List<ItalianReading>> bucket = verbs;
                List<PartOfSpeech> bucketPosTags = verbPosTags;
                List<String> bucketWordGroup = emptyWordGroup;
                List<DependencyRelation> bucketRelations = emptyRelations;
                boolean isAuxiliary = false;

                // We don't need to check for an auxiliary at level 0.
                // Auxiliary verbs will always be the child of a modal
                // or a verb, but never the parent of the subject.
                if (level > 0) {

                    // Similarly, direct object pronouns will only be present as a child of
                    // the main verb or modal.  At earliest, they will appear at level 1.
                    if (participant.dependencyRelation == DependencyRelation.OBJ) {
                        bucketLevel = level - 1;
                        bucket = directObjectPronouns;
                        bucketPosTags = emptyPosTags;
                        bucketWordGroup = getDirectObjectPronouns();
                        bucketRelations = dopRelations;

                        // Direct object pronouns must come before auxiliary verbs if they are to influence agreement.
                        // Skip processing this obj if we have already processed an auxiliary for this level.
                        if (auxiliariesAvere.containsKey(bucketLevel)) {
                            continue;
                        }
                    }

                    // If the participant comes before it's parent in the sentence, then
                    // it's probably an auxiliary.  Otherwise it's probably a modal or verb.
                    // Auxiliaries are one level down from their associated verbs.
                    // Using start position in lieu of token number.
                    else if (participant.getStartPos() < participant.head.getStartPos()) {
                        bucketLevel = level - 1;
                        bucket = auxiliariesOther;
                        bucketPosTags = auxPosTags;
                        isAuxiliary = true;
                    }
                }

                // Check dependency relations.  Only direct object pronouns are restricted by dependency relation.
                if (bucketRelations.isEmpty() || bucketRelations.contains(participant.dependencyRelation)) {

                    // Check word.  Again, only direct object pronouns have this restriction.
                    if (bucketWordGroup.isEmpty() || bucketWordGroup.contains(participant.getToken().toLowerCase())) {

                        // Save only relevant interpretations, either auxiliaries or modals/verbs.
                        // Throw away any readings that are not currently supported.
                        for (ItalianReading reading : participant.readings) {

                            // Check POS tags
                            if (bucketPosTags.isEmpty() || bucketPosTags.contains(reading.pos)) {

                                // Check auxiliary for avere lemma.
                                if (isAuxiliary && reading.getLemma().equalsIgnoreCase("avere")) {
                                    bucket = auxiliariesAvere;
                                }

                                // Update the entry or add a new entry.
                                if (bucket.containsKey(bucketLevel)) {
                                    List<ItalianReading> savedReadings = bucket.get(bucketLevel);
                                    savedReadings.add(reading);
                                } else {
                                    bucket.put(bucketLevel, new ArrayList<>(Arrays.asList(reading)));
                                }
                            } // End of POS tag check.
                        } // Done looping over the interpretations for the participant.

                    } // End of word group check.
                } // End of dependency relation check

                // If we processed a modal or verb, add it's children for processing at the next level.
                // bucketLevel and level will match if we for verbs and modals.
                // bucketLevel will be one less level for direct object pronouns and auxiliaries.
                if (bucketLevel == level && bucket.containsKey(bucketLevel)){
                    nextLevel.addAll(Arrays.asList(participant.getChildren()));
                }

                // If we are done processing participants from this level, add the next level in.
                if (participants.size() == 0) {
                    level++;
                    participants.addAll(nextLevel);
                    nextLevel.clear();
                }
            } // Done processing participants from the verb chain.

            // If we failed to identify any verbs for the subject,
            // then there's no point in trying to process agreement pairs.
            if (verbs.size() == 0) continue;

            // Now we need to create pairs for enforcement of agreement (gender and number).

            // If there is more than one verb in the chain, then the first verb will be a modal.
            // In all cases, the last verb in the chain will be a verb (not modal).
            List<Integer> agreementLevels = new ArrayList<Integer>();
            agreementLevels.add(verbs.size() - 1);
            if (verbs.size() > 1) {
                agreementLevels.add(0);
            }

            // Use a filter to screen out modal interpretations for verbs and verb interpretations for modals.
            PartOfSpeech verbModalPosFilter = PartOfSpeech.VER;

            // This loop will only execute once or twice, processing the last verb in
            // the chain and then optionally the first verb (modal) in the verb chain.
            for (int agreementLevel : agreementLevels) {

                // Hurray for defensive coding...
                if (verbs.containsKey(agreementLevel)) {

                    // In the first iteration, modal interpretations will be discarded since we are processing a verb.
                    // In the second iteration, verb interpretations will be discarded since we are processing a modal.
                    List<ItalianReading> verbModalReadings = new ArrayList<ItalianReading>();
                    for (ItalianReading reading : verbs.get(agreementLevel)) {
                        if (reading.pos == verbModalPosFilter) {
                            verbModalReadings.add(reading);
                        }
                    }

                    // If the modal has an avere auxiliary verb attached to it,
                    // then the subject no longer controls the agreement.
                    if (auxiliariesAvere.containsKey(agreementLevel)) {

                        // Auxiliary verbs must always match the subject.
                        List<ItalianReading> auxReadings = auxiliariesAvere.get(agreementLevel);
                        if (subjectReadings.size() > 0 && auxReadings.size() > 0) {
                            AgreementPair pair = new AgreementPair(subjectReadings, auxReadings);
                            agreementPairs.add(pair);
                        }

                        // If a direct object pronoun is present, then it controls the agreement of the modal
                        if (directObjectPronouns.containsKey(agreementLevel)) {
                            List<ItalianReading> dopReadings = directObjectPronouns.get(agreementLevel);
                            if (verbModalReadings.size() > 0 && dopReadings.size() > 0) {
                                AgreementPair pair = new AgreementPair(dopReadings, verbModalReadings);
                                agreementPairs.add(pair);
                            }
                        }
                        // Otherwise, the modal must be masculine and singular.
                        else {
                            AnalyzedToken masculineSinglarToken = new AnalyzedToken("token", "NOUN-M:s", "lemma");
                            ItalianReading masculineSingularReading = new ItalianReading(masculineSinglarToken);
                            List<ItalianReading> msReadings = Arrays.asList(masculineSingularReading);
                            if (msReadings.size() > 0 && verbModalReadings.size() > 0) {
                                AgreementPair pair = new AgreementPair(msReadings, verbModalReadings);
                                agreementPairs.add(pair);
                            }
                        }
                    }
                    // If the modal does not have an auxiliary, or the associated auxiliary does
                    // not have avere as the lemma, then the modal must agree with the subject.
                    else {
                        if (verbModalReadings.size() > 0 && subjectReadings.size() > 0) {
                            AgreementPair pair = new AgreementPair(subjectReadings, verbModalReadings);
                            agreementPairs.add(pair);
                        }

                        // If the modal has an auxiliary associated with it, and the auxiliary did not have
                        // "avere" as it's lemma, then the auxiliary must also agree with the subject.
                        if (auxiliariesOther.containsKey(agreementLevel)) {
                            List<ItalianReading> auxReadings = auxiliariesOther.get(agreementLevel);
                            if (subjectReadings.size() > 0 && auxReadings.size() > 0) {
                                AgreementPair pair = new AgreementPair(subjectReadings, auxReadings);
                                agreementPairs.add(pair);
                            }
                        }
                    }

                } // End of verbs.size() check.

                // Update filter for modals.
                verbModalPosFilter = PartOfSpeech.MOD;

            } // Done processing verb and modal agreement pairs.

            // We now need to turn the agreement pairs for matches.
            for (AgreementPair pair : agreementPairs) {
                List<ItalianReading> parentReading = pair.getParentReadings();
                List<ItalianReading> childReadings = pair.getChildReadings();
                boolean hasAgreeablePair = checkForAgreeablePair(parentReading, childReadings);
                if (!hasAgreeablePair) {
                    // Create new rule match.

                    // We need to generate suggestions for replacement.
                    // Use the features from the parent overlaid onto the child.
                    ArrayList<String> replacements = generateReplacements(parentReading, childReadings);

                    // TODO: Need to track actual tokens as well, since we need character position for the UI.
                    int start = 0;
                    int end = 5;
                    /*
                    // Construct the rule match to replace the parent.
                    int start = child.getStartPos();
                    int end = child.getEndPos();
                    */

                    // Construct a message for the user.
                    // TODO: Make a better message.
                    String message = "\"" + childReadings.get(0).analyzedToken.getToken() + "\" " +
                            "does not and needs to agree with " +
                            "\"" + parentReading.get(0).analyzedToken.getToken() + "\"";

                    RuleMatch ruleMatch = new RuleMatch(this, start, end, message);
                    if (replacements.size() > 0) ruleMatch.setSuggestedReplacements(replacements);
                    ruleMatches.add(ruleMatch);
                }
            } // Done iterating over agreement pairs.
        } // Done checking each token in the sentence.

        RuleMatch[] results = ruleMatches.toArray(new RuleMatch[ruleMatches.size()]);
        return results;
    }

    static boolean checkForAgreeablePair(List<ItalianReading> parentReadings, List<ItalianReading> childReadings) {
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

    static ArrayList<String> generateReplacements(List<ItalianReading> parentReadings, List<ItalianReading> childReadings) throws IOException {
        Set<String> replacements = new HashSet<String>();
        for (ItalianReading parentReading : parentReadings) {
            for (ItalianReading childReading : childReadings) {

                // Create a copy of the reading being replaced
                ItalianReading suggestion = new ItalianReading(childReading);

                // Pull in the features from the controlling word.
                suggestion.matchOverlappingFeatures(parentReading);

                // Construct the morphology for the suggestion.
                // Hopefully it will be found in the dictionary.
                String posTag = suggestion.generatePosTag();

                // Synthesize the suggested word.
                ItalianSynthesizer synthesizer = new ItalianSynthesizer();
                String[] suggestions = synthesizer.synthesize(suggestion.analyzedToken, posTag);

                if (suggestions.length > 0) Collections.addAll(replacements, suggestions);
            }
        }
        return new ArrayList<String>(replacements);
    }

    @Override
    public void reset() {

    }
}