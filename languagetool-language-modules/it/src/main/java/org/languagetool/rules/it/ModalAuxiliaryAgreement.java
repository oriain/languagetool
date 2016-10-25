package org.languagetool.rules.it;

import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.PartOfSpeech;

/**
 * Created by littl on 10/18/2016.
 */
class ModalAuxiliaryAgreement extends AgreementRelationship {
    ModalAuxiliaryAgreement() {
        this.description = "The modal and auxiliary verbs do not agree.";

        // Child
        this.childPos.add(PartOfSpeech.AUX);

        // Parent
        this.parentPos.add(PartOfSpeech.MOD);

        // Relationship
        // We are not going to use the relationship constraint because I think I remember that the
        // auxiliary relationships were not being consistently described in the dependency parser.
        //this.relation.add(DependencyRelation.AUX);
        //this.relation.add(DependencyRelation.AUX_PASSIVE);
        //this.relation.add(DependencyRelation.AUX_PROGRESSIVE);
        //this.relation.add(DependencyRelation.AUX_TENSE);
    }

    @Override
    public boolean checkForExemption(ItalianToken child, ItalianToken parent) {
        return checkForAuxiliaryVerbAvere(parent);
    }

    // Copied from SubjectVerbAgreement.java.
    private boolean checkForAuxiliaryVerbAvere(ItalianToken parent) {
        for (ItalianToken child : parent.getChildren()) {
            if (DependencyRelation.isAuxiliary(child) && child.isAvere()) return true;
        }
        return false;
    }
}

