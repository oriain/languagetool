package org.languagetool.rules.it;

import org.languagetool.tagging.it.ItalianReading;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littl on 11/1/2016.
 */
public class AgreementPair {
    private List<ItalianReading> parentReadings;
    public List<ItalianReading> getParentReadings() {
        return parentReadings;
    }

    private List<ItalianReading> childReadings = new ArrayList<>();
    public List<ItalianReading> getChildReadings() {
        return childReadings;
    }

    public AgreementPair(List<ItalianReading> parentReadings, List<ItalianReading> childReadings) {
        this.parentReadings = parentReadings;
        this.childReadings = childReadings;
    }
}
