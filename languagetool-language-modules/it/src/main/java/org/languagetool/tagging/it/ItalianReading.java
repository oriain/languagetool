package org.languagetool.tagging.it;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.languagetool.AnalyzedToken;
import org.languagetool.tagging.it.tag.Feature;
import org.languagetool.tagging.it.tag.PartOfSpeech;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by littl on 5/21/2016.
 */
public class ItalianReading {
    private AnalyzedToken source;
    ItalianReading(AnalyzedToken source) {
        this.source = source;
        parsePosTag();
    }

    public PartOfSpeech pos;
    private Feature.Gender gender;
    private Feature.Number number;
    private Feature.Person person;
    private Feature.Degree degree;
    private Feature.Mood mood;
    private Feature.Tense tense;
    private Feature.Clitics clitics;
    private Feature.Cli cli;

    private boolean hasGender()  { return this.gender  != null; }
    private boolean hasNumber()  { return this.number  != null; }
    private boolean hasPerson()  { return this.person  != null; }
    private boolean hasDegree()  { return this.degree  != null; }
    private boolean hasMood()    { return this.mood    != null; }
    private boolean hasTense()   { return this.tense   != null; }
    private boolean hasClitics() { return this.clitics != null; }
    private boolean hasCli()     { return this.cli     != null; }
    private boolean hasPos()     { return this.pos     != null; }

    String getLemma() {
        return this.source.getLemma();
    }

    String getPosString() {
        if (this.hasPos()) return this.pos.toString().replace("_", "-");
        return "_";
    }

    // TODO: Apparently features may have more than one value. Ex: Case=Acc,Dat
    // Will probably never need to handle this, at least for the Italian dictionary
    // being used, but it's nice to be aware of it in the future.
    String getFeaturesString() {
        ArrayList<String> features = new ArrayList<>();

        // Only include features relevant for the particular part-of-speech.
        if (this.hasGender())  features.add(String.format("Gender=%s",  gender.toString()));
        if (this.hasNumber())  features.add(String.format("Number=%s",  number.toString()));
        if (this.hasPerson())  features.add(String.format("Person=%s",  person.toString()));
        if (this.hasDegree())  features.add(String.format("Degree=%s",  degree.toString()));
        if (this.hasMood())    features.add(String.format("Mood=%s",    mood.toString()));
        if (this.hasTense())   features.add(String.format("Tense=%s",   tense.toString()));
        if (this.hasClitics()) features.add(String.format("Clitics=%s", clitics.toString()));
        if (this.hasCli())     features.add(String.format("Cli=%s",     cli.toString()));

        // Sort the features alphabetically
        features.sort(null);

        // Features are separated by a pipe character
        String featureString = StringUtils.join(features, "|");
        if (featureString.isEmpty()) featureString = "_";

        return featureString;
    }

    public boolean agreesWith(ItalianReading other) {
        // A single feature agrees if the following is true.
        //   IF _this_ doesn't have the feature
        //   OR _that_ doesn't have the feature
        //   OR the features match.
        // If all features agree, then the feature sets agree.
        return     (!this.hasGender()  || !other.hasGender()  || this.gender  == other.gender)
                && (!this.hasNumber()  || !other.hasNumber()  || this.number  == other.number)
                && (!this.hasPerson()  || !other.hasPerson()  || this.person  == other.person)
                && (!this.hasDegree()  || !other.hasDegree()  || this.degree  == other.degree)
                && (!this.hasMood()    || !other.hasMood()    || this.mood    == other.mood)
                && (!this.hasTense()   || !other.hasTense()   || this.tense   == other.tense)
                && (!this.hasClitics() || !other.hasClitics() || this.clitics == other.clitics)
                && (!this.hasCli()     || !other.hasCli()     || this.cli     == other.cli);
    }

    private void parsePosTag() {
        // TODO
//        // LanguageTool adds an additional reading or interpretation to the last word in a source.
//        // This is typically the source ending punctuation mark, but in reality could be any token.
//        // We're ignoring the source-ending marker since it doesn't come from the dictionary.
//        String posTag = interpretation.getPOSTag();
//        assert posTag != null;
//        if (posTag.equals("SENT_END")) continue;


        String posTag = this.source.getPOSTag();
        if (posTag == null || posTag.isEmpty()) return;

        // Attempt to split the information into derivational and inflectional parts.
        String derivationalInflectionalSeparator = ":";
        String[] parts = posTag.split(derivationalInflectionalSeparator);
        String derivationalInfo = parts[0];

        // Process the derivational feature information.
        String derivationalSeparator = "-";
        parsePosTag(derivationalInfo, derivationalSeparator);

        // If the info string also has inflectional features, parse them and add them to the features object.
        if (parts.length > 1) {
            String inflectionalInfo = parts[1];
            String inflectionalSeparator = "+";
            parsePosTag(inflectionalInfo, inflectionalSeparator);
        }
    }

    private void validateRequiredFeatures(ItalianReading feature) {
        throw new NotImplementedException();

//        // Check to make sure that we have all the features relevant for the particular Pos.
//        PartOfSpeech pos = fe.Pos;
//        if (pos == null)
//            throw new Exception("Parse failure: The part of speech could not be found.");
//        if (pos.hasGender() && combined.Gender == null)
//            throw new Exception("Parse failure: The Gender feature could not be found.");
//        if (pos.hasNumber() && combined.Number == null)
//            throw new Exception("Parse failure: The Number feature could not be found.");
//        if (pos.hasPerson() && combined.Person == null)
//            throw new Exception("Parse failure: The Person feature could not be found.");
//        if (pos.hasDegree() && combined.Degree == null)
//            throw new Exception("Parse failure: The Degree feature could not be found.");
//        if (pos.hasMood() && combined.Mood == null)
//            throw new Exception("Parse failure: The Mood feature could not be found.");
//        if (pos.hasTense() && combined.Tense == null)
//            throw new Exception("Parse failure: The Tense feature could not be found.");
//        if (pos.hasClitics() && combined.Clitics == null)
//            throw new Exception("Parse failure: The Clitics feature could not be found.");
//        if (pos.hasCli() && combined.Cli == null)
//            throw new Exception("Parse failure: The Cli feature could not be found.");
    }

    private void parsePosTag(String info, String delimiter) {
        // We process features starting at the end of the string working our way towards the beginning.
        // I guess we could have worked from the beginning of the string towards the end too... :\
        int fromIndex = info.length();  // fromIndex is inclusive.
        int endIndex = info.length();   //  endIndex is exclusive.
        while (fromIndex > -1) {
            boolean foundMatch = false;

            // Find the index of the delimiter.  Offset by the delimiter's
            // length to avoid including the delimiter in the substring.
            int beginIndex = info.lastIndexOf(delimiter, fromIndex) + delimiter.length();

            // The index is inclusive when used as the fromIndex in lastIndexOf()
            // but it's exclusive when used as the endIndex in substring.
            String featureName = info.substring(beginIndex, endIndex);
            String lowerFeatureName = featureName.toLowerCase();

            // Gender
            // This if statement will always be false.  Just keeping for consistency.
            // TODO: Should I throw an exception if two features are defined for the same token?
            if (!foundMatch && this.gender == null) {
                try {
                    this.gender = Feature.Gender.valueOf(lowerFeatureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Number
            if (!foundMatch && this.number == null) {
                try {
                    this.number = Feature.Number.valueOf(lowerFeatureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Person
            if (!foundMatch && this.person == null) {
                switch (featureName) {
                    case "1":
                        this.person = Feature.Person.First;
                        foundMatch = true;
                        break;
                    case "2":
                        this.person = Feature.Person.Second;
                        foundMatch = true;
                        break;
                    case "3":
                        this.person = Feature.Person.Third;
                        foundMatch = true;
                        break;
                }
            }

            // Degree
            if (!foundMatch && this.degree == null) {
                try {
                    this.degree = Feature.Degree.valueOf(lowerFeatureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Mood
            if (!foundMatch && this.mood == null) {
                try {
                    this.mood = Feature.Mood.valueOf(lowerFeatureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Tense
            if (!foundMatch && this.tense == null) {
                try {
                    this.tense = Feature.Tense.valueOf(lowerFeatureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Clitics
            if (!foundMatch && this.clitics == null) {
                try {
                    this.clitics = Feature.Clitics.valueOf(lowerFeatureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Cli
            if (!foundMatch && this.cli == null) {
                try {
                    this.cli = Feature.Cli.valueOf(featureName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Pos
            // If the feature has not been detected by now, we may be evaluating the word class.
            // Convert the dashes to underscores and try to match on the PartOfSpeech enum.
            if (!foundMatch && this.pos == null) {
                String posName = featureName.replaceAll(delimiter, "_");
                try {
                    this.pos = PartOfSpeech.valueOf(posName);
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Only move the endIndex closer to the front of the
            // string if the current substring matched a feature or pos.
            if (foundMatch) endIndex = beginIndex - delimiter.length();

            // Move the fromIndex closer to the front of the string to find the next delimiter.
            // The delimiter length needs to be subtracted twice. Once for avoiding the match we just
            // processed and a second time for giving space for it to match the end of the new range.
            fromIndex = beginIndex - delimiter.length() - delimiter.length();

        } // End of while-loop
    }
}
