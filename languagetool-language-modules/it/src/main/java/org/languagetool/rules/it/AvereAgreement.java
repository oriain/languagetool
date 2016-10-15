package org.languagetool.rules.it;

import org.languagetool.rules.RuleMatch;
import org.languagetool.tagging.it.ItalianReading;
import org.languagetool.tagging.it.ItalianSentence;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.Feature;
import org.languagetool.tagging.it.tag.PartOfSpeech;

import java.io.IOException;
import java.util.*;

/**
 * Created by littl on 5/30/2016.
 */
class AvereAgreement extends AgreementRelationship {
    private AgreementRule rule;
    private static HashSet<String> directObjectPronouns = new HashSet<>(Arrays.asList(
            "lo",       "la",       "li",       "le",                   //"ne",
            "melo",     "mela",     "meli",     "mele",                 //"mene",
            "telo",     "tela",     "teli",     "tele",                 //"tene",
            "selo",     "sela",     "seli",     "sele",                 //"sene",
            "glielo",   "gliela",   "glieli",   "gliele",   "gliel'",   //"gliene",
            "celo",     "cela",     "celi",     "cele",                 //"cene",
            "velo",     "vela",     "veli",     "vele"                  //"vene"
    ));

    AvereAgreement(AgreementRule rule) {
        this.rule = rule;

        // Description
        this.description = "The verb must agree with it's direct object pronoun.";

        // Dependency Relationships.
        this.relation.add(DependencyRelation.AUX);
        this.relation.add(DependencyRelation.AUX_PASSIVE);
        this.relation.add(DependencyRelation.AUX_PROGRESSIVE);
        this.relation.add(DependencyRelation.AUX_TENSE);

        // Parent (Main Verb)
        this.parentPos.add(PartOfSpeech.VER);

        // Any child is okay, as long as they are marked
        // with an auxiliary relationship to the main verb.
    }

    @Override
    public boolean checkForExemption(ItalianToken child, ItalianToken parent) {
        // If the lemma of the auxiliary verb is anything other than AVERE, then the main verb (which is the parent of
        // the auxiliary verb) follows normal subject-verb agreement rules with the subjct of the sentence.
        // This special rule only applies to verbs that pair with auxiliary verbs that have AVERE as a lemma.
        // If the main verb also connections to a direct object pronoun (LO, LA, LE or LI), then it matches in gender
        // and number to the direct object pronoun.  Otherwise it must take a masculine singular form.
        boolean isAvere = false;
        for (ItalianReading reading : child.readings) {
            if (reading.getLemma().equalsIgnoreCase("AVERE")) {
                isAvere = true;
            }
        }
        return !isAvere;
    }

    public RuleMatch[] match(ItalianSentence sentence) throws IOException {
        List<RuleMatch> ruleMatches = new ArrayList<>();
        for (ItalianToken child : sentence.tokens) {
            /* // Changed nothing...
            // The Dependency parser is not robustly assigning the auxiliary labels.
            // So instead, if the child has a reading that is an auxiliary verb, and it's
            // parent has a reading that is a verb, then we'll assume an auxiliary relationship.
            if (!isAuxiliaryVerb(child)) continue;
            */

            // The child token must be acting as an auxiliary verb in the sentence.
            if (!DependencyRelation.isAuxiliary(child)) continue;

            // The auxiliary verb must have a lemma of AVERE. Auxiliary verbs with lemma's
            // of ESSERE, STARE, VENIRE and ANDARE follow normal subject verb agreement.
            if (!child.isAvere()) continue;

            // The parent should never be null if the child has an auxiliary
            // relationship, but let's code defensively anyways!
            ItalianToken parent = child.head;
            if (parent == null) continue;

            // Get VERB readings
            List<ItalianReading> parentReadings = new ArrayList<>();
            for (ItalianReading parentReading : parent.readings) {
                if (parentReading.pos == PartOfSpeech.VER) {
                    parentReadings.add(parentReading);
                }
            }

            // If none of the parent readings have a VERB part-of-speech, then this rule doesn't apply.
            ItalianReading[] validParentReadings = parentReadings.toArray(new ItalianReading[parentReadings.size()]);

            // The parent of the auxiliary verb will always be a main verb.
            // If the main verb is connected to the direct object pronouns of LO, LA, LE or Li
            // then the main verb must match the direct object pronouns in gender and number.
            ItalianReading[] directObjectPronounReadings = new ItalianReading[0];
            for (ItalianToken directObjectPronoun : parent.getChildren()) {
                boolean isDirectObject = directObjectPronoun.dependencyRelation == DependencyRelation.OBJ;
                        //|| directObjectPronoun.dependencyRelation == DependencyRelation.OBJ_SUBJ
                        //|| directObjectPronoun.dependencyRelation == DependencyRelation.SUBJ_OBJ;
                boolean isDirectObjectPronoun = directObjectPronouns.contains(directObjectPronoun.getToken().toLowerCase());
                if (isDirectObject && isDirectObjectPronoun) {
                    directObjectPronounReadings = getPersonalPronounReadings(directObjectPronoun);
                    break;
                }
            }

            // If we have a direct object pronoun, the main verb must match in gender and number.
            // Otherwise, the main verb must take the masculine, singular form.
            boolean hasRuleMatch = false;
            ItalianReading[] sourceReadings = new ItalianReading[0];
            if (directObjectPronounReadings.length > 0) {
                // If the verb is connected to a direct object pronoun
                // (LO, LA, LE, LE), then check for agreeable pairs.
                boolean hasAgreeablePair = rule.checkForAgreeablePair(directObjectPronounReadings, validParentReadings);
                if (!hasAgreeablePair) {
                    hasRuleMatch = true;
                    sourceReadings = directObjectPronounReadings;
                }
            } else {
                // If the verb is already masculine and singular,
                // then nothing further needs to be done.
                if (!hasMasculineSingularReading(parent)) {
                    hasRuleMatch = true;
                    // The main verb needs to be masculine, singular.
                    String errorMessage = "The past participle verb %s is used with the avere auxiliary verb %s " +
                            "and without a direct object pronoun, meaning it must be masculine and singular.";
                    this.description = String.format(errorMessage, parent.getToken(), child.getToken());
                    ItalianReading correctFeatures = new ItalianReading(validParentReadings[0]);
                    correctFeatures.setGender(Feature.Gender.m);
                    correctFeatures.setNumber(Feature.Number.s);
                    sourceReadings = new ItalianReading[]{correctFeatures};
                }
            }

            if (hasRuleMatch) {
                // We need to generate suggestions for replacement.
                ArrayList<String> replacements = rule.generateReplacements(sourceReadings, validParentReadings);

                // Construct the rule match to replace the parent.
                int start = parent.getStartPos();
                int end = parent.getEndPos();
                RuleMatch ruleMatch = new RuleMatch(this.rule, start, end, this.description);
                if (replacements.size() > 0) ruleMatch.setSuggestedReplacements(replacements);
                ruleMatches.add(ruleMatch);
            }
        } // Done looping through the tokens in the sentence.

        return ruleMatches.toArray(new RuleMatch[ruleMatches.size()]);
    }

    private boolean isAuxiliaryVerb(ItalianToken token) {
        for (ItalianReading reading : token.readings) {
            if (reading.pos == PartOfSpeech.AUX) {
                return true;
            }
        }
        return false;
    }

    private ItalianReading[] getPersonalPronounReadings(ItalianToken token) {
        List<ItalianReading> readings = new ArrayList<>();
        for (ItalianReading reading : token.readings) {
            if (reading.pos == PartOfSpeech.PRO_PERS) {
                readings.add(reading);
            }
        }
        return readings.toArray(new ItalianReading[readings.size()]);
    }

    private boolean hasMasculineSingularReading(ItalianToken token) {
        for (ItalianReading reading : token.readings) {
            boolean isMasculine = (reading.getGender() == Feature.Gender.M
                    || reading.getGender() == Feature.Gender.m);
            boolean isSingular = (reading.getNumber() == Feature.Number.s
                    || reading.getNumber()== Feature.Number.S);
            if (isMasculine && isSingular) {
                return true;
            }
        }
        return false;
    }
}
