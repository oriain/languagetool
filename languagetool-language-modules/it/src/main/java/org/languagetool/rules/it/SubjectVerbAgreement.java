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
        this.childPos.add(PartOfSpeech.NOUN);
        this.childPos.add(PartOfSpeech.ART);
        this.relation.add(DependencyRelation.SUBJ);
        this.parentPos.add(PartOfSpeech.VER);
    }

    @Override
    public boolean checkForExemption(ItalianToken child) {
        return checkForCoordinatingConjunction(child);
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
