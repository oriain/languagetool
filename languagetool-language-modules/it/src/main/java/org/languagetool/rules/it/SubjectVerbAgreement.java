package org.languagetool.rules.it;

import org.languagetool.tagging.it.ItalianReading;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.Feature;
import org.languagetool.tagging.it.tag.PartOfSpeech;

/**
 * Created by littl on 5/27/2016.
 */
class SubjectVerbAgreement extends AgreementRelationship {

    SubjectVerbAgreement() {
        this.description = "The subject and verb do not agree.";

        // Child
        this.childPos.add(PartOfSpeech.NOUN);
        this.childPos.add(PartOfSpeech.ART);//YES-implied, should we check actual attachment?
        this.childPos.add(PartOfSpeech.PRO_DEMO);
        this.childPos.add(PartOfSpeech.PRO_INDEF);
        this.childPos.add(PartOfSpeech.PRO_NUM);
        this.childPos.add(PartOfSpeech.PRO_PERS);
        this.childPos.add(PartOfSpeech.PRO_WH);
        this.childPos.add(PartOfSpeech.WH);
        this.childPos.add(PartOfSpeech.WH_CHE);
        // What is an example of Pronoun-Numeral
        //this.childPos.add(PartOfSpeech.PRO_NUM);

        // Subjects
        this.relation.add(DependencyRelation.SUBJ);
        this.relation.add(DependencyRelation.SUBJ_OBJ);

        // Objects
        this.relation.add(DependencyRelation.OBJ_SUBJ);
        this.relation.add(DependencyRelation.PREDCOMPL_OBJ);

        // Parent
        this.parentPos.add(PartOfSpeech.VER);

        // TODO: Look up which sentences use this.
        //DependencyRelation.SUBJ_INDCOMPL;
        //DependencyRelation.SUBJ_INDOBJ;
        //DependencyRelation.SUBJ_LOCUT;
        //DependencyRelation.SUBJ_SUBJ_IMPERS;

        // TODO: Look up which sentences use this.
        //DependencyRelation.PREDCOMPL_SUBJ_CLEFT
        //DependencyRelation.PREDCOMPL_OBJ_LOCUT
    }

    @Override
    public boolean checkForExemption(ItalianToken child, ItalianToken parent) {
        return checkForCoordinatingConjunction(child) || checkForAuxiliaryVerbEssere(parent);
    }

    // If the verb is a past participle verb, and it's connected to an auxiliary verb that does has AVERE as it's
    // lemma, then special rules cover it's agreement and should be checked by a separate rule.  Specifically, if the
    // past participle verb is connected to an auxiliary verb with the lemma of AVERE, then must take the masculine
    // singular form, unless it's also connected to one of the direct object pronouns LO, LA, LE, LI, in which case it
    // agrees with those direct object pronouns in gender and number.
    private boolean checkForAuxiliaryVerbEssere(ItalianToken parent) {
        for (ItalianToken child : parent.getChildren()) {
            if (DependencyRelation.isAuxiliary(child) && child.isAvere()) return true;
        }
        return false;
    }

    // If the child (subject) is singular, but has a coordinating conjunction,
    // then the parent (verb) is allowed to be plural.  The basic agreement
    // checking would flag sentences with coordinating conjunctions since the
    // verb and subject don't match in number.
    private boolean checkForCoordinatingConjunction(ItalianToken child) {
        // Loop over all interpretations for the child token.
        for (ItalianReading reading : child.readings) {
            // If the subject is plural, Agreement checking would only flag the
            // sentence if the verb is singular, meaning this reading is not exempt.
            if (reading.number == Feature.Number.P || reading.number == Feature.Number.p) {
                return false;
            }
        }

        // Since we now know that the subject is singular and the verb is plural, we need to
        // check for a coordination conjunction, represented by a relationship of "COORD+BASE."
        for (ItalianToken grandchild : child.getChildren()) {
            if (grandchild.dependencyRelation == DependencyRelation.COORD_BASE) {
                return true;
            }
        }

        // There is no exemption if the subject is singular, the verb
        // is plural, and there is no coordination conjunction.
        return false;
    }
}
