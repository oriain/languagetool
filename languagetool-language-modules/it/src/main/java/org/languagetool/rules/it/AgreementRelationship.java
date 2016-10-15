package org.languagetool.rules.it;

import org.languagetool.tagging.it.ItalianReading;
import org.languagetool.tagging.it.ItalianToken;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tagging.it.tag.PartOfSpeech;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by littl on 5/24/2016.
 */
class AgreementRelationship implements AgreementRelationExemption {
    public String description;
    HashSet<PartOfSpeech> childPos = new HashSet<>();
    HashSet<PartOfSpeech> parentPos = new HashSet<>();
    HashSet<DependencyRelation> relation = new HashSet<>();

    private ItalianReading[] getValidReadings(HashSet<PartOfSpeech> set, ItalianToken token) {
        // An agreement relationship with a null set means that
        // all tokens are valid for this part of the agreement check.
        if (set.isEmpty()) return token.readings;

        // A token may have more than one possible interpretation.
        ArrayList<ItalianReading> readings = new ArrayList<>();
        for (ItalianReading reading : token.readings) {

            // Check to make sure the interpretation's POS is
            // valid for this type of agreement relationship.
            if (set.contains(reading.pos)) readings.add(reading);
        }

        return readings.toArray(new ItalianReading[readings.size()]);
    }

    ItalianReading[] getValidChildReadings(ItalianToken token) {
        return getValidReadings(this.childPos, token);
    }

    ItalianReading[] getValidParentReadings(ItalianToken token) {
        return getValidReadings(this.parentPos, token);
    }

//    public boolean isValidChild(ItalianToken token) {
//        // An agreement relationship with a null childPos means that
//        // all tokens are valid for this part of the agreement check.
//        if (this.childPos.isEmpty()) return true;
//
//        // A token may have more than one possible interpretation.
//        for (ItalianReading reading : token.readings) {
//            // Check to make sure the interpretation's POS is
//            // valid for this type of agreement relationship.
//            if (this.childPos.contains(reading.pos)) return true;
//        }
//
//        return false;
//    }

//    public boolean isValidParent(ItalianToken token) {
//        // An agreement relationship with a null parentPos means that
//        // all tokens are valid for this part of the agreement check.
//        if (this.parentPos.isEmpty()) return true;
//
//        // A token may have more than one possible interpretation.
//        for (ItalianReading reading : token.readings) {
//            // Check to make sure the interpretation's POS is
//            // valid for this type of agreement relationship.
//            if (this.parentPos.contains(reading.pos)) return true;
//        }
//
//        return false;
//    }

    boolean isValidDependencyRelation(ItalianToken token) {
        if (this.relation.isEmpty()) return true;
        return this.relation.contains(token.dependencyRelation);
    }

    @Override
    public boolean checkForExemption(ItalianToken child, ItalianToken parent) {
        return false;
    }
}
