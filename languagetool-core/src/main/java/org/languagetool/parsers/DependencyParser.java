package org.languagetool.parsers;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;

import java.util.List;

/**
 * Created by littl on 5/28/2016.
 */
public interface DependencyParser {
    void addDependencies(List<AnalyzedTokenReadings> tokens) throws DependencyParseException;
    void addDependencies(AnalyzedSentence sentence) throws DependencyParseException;
}
