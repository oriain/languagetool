package org.languagetool.tagging.it;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.languagetool.AnalyzedToken;
import org.languagetool.tagging.it.tag.Feature;
import org.languagetool.tagging.it.tag.PartOfSpeech;
import org.maltparser.core.helper.HashMap;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by littl on 5/21/2016.
 */
public class ItalianReading {
    public AnalyzedToken analyzedToken;
    ItalianReading(AnalyzedToken analyzedToken) {
        this.analyzedToken = analyzedToken;
        parsePosTag();
    }

    public ItalianReading(ItalianReading original) {
        this.analyzedToken = original.analyzedToken;

        // Effectively "transfers" the morphology (POS & features).
        parsePosTag();

        // parsePosTag() will incorrectly update the posFeatureOrder and derivationalFeatures
        // collections, so we need to copy them over from the original.
        this.posFeatureOrder = original.posFeatureOrder;
        this.derivationalFeatures = original.derivationalFeatures;
    }

    public PartOfSpeech pos;
    private Feature.Gender gender;
    public Feature.Number number;
    private Feature.Person person;
    private Feature.Degree degree;
    private Feature.Mood mood;
    private Feature.Tense tense;
    private Feature.Clitics clitics;
    private Feature.Cli cli;
    private String originalDictionaryString = "";

    private TreeMap<Integer, String> posFeatureOrder = new TreeMap<>();
    private boolean parsingDerivations;
    private ArrayList<String> derivationalFeatures = new ArrayList<>();

    private boolean hasGender()  { return this.gender  != null; }
    private boolean hasNumber()  { return this.number  != null; }
    private boolean hasPerson()  { return this.person  != null; }
    private boolean hasDegree()  { return this.degree  != null; }
    private boolean hasMood()    { return this.mood    != null; }
    private boolean hasTense()   { return this.tense   != null; }
    private boolean hasClitics() { return this.clitics != null; }
    private boolean hasCli()     { return this.cli     != null; }
    private boolean hasPos()     { return this.pos     != null; }

    public String getLemma() {
        return this.analyzedToken.getLemma();
    }

    public String getPosString() {
        if (this.hasPos()) return this.pos.toString().replace("_", "-");
        return "_";
    }

    // TODO: Apparently features may have more than one value. Ex: Case=Acc,Dat
    // Will probably never need to handle this, at least for the Italian dictionary
    // being used, but it's nice to be aware of it in the future.
    public String getFeaturesString() {
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

    public String generatePosTag() {
        if (!this.originalDictionaryString.isEmpty()) return this.originalDictionaryString;

        // Keep track of which strings are derivational or inflectional.
        ArrayList<String> derivationalStrings = new ArrayList<>();
        ArrayList<String> inflectionalStrings = new ArrayList<>();

        // Loop over the morphology for this reading.  The order should
        // be the same as seen in the morphological dictionary.
        for (HashMap.Entry<Integer, String> entry : posFeatureOrder.entrySet()) {
            String posFeature = entry.getValue();
            int key = entry.getKey();

            // Get the pos or feature information for the string.
            String value = "";
            switch (posFeature) {
                case "Pos":
                    value = this.pos.toString().replace("_", "-");
                    break;
                case "Gender":
                    value = this.gender.toString();
                    break;
                case "Number":
                    value = this.number.toString();
                    break;
                case "Person":
                    if (this.person == Feature.Person.First) {
                        value = "1";
                    } else if (this.person == Feature.Person.Second) {
                        value = "2";
                    } else if (this.person == Feature.Person.Third) {
                        value = "3";
                    }
                    break;
                case "Degree":
                    value = this.degree.toString();
                    break;
                case "Mood":
                    value = this.mood.toString();
                    break;
                case "Tense":
                    value = this.tense.toString();
                    break;
                case "Clitics":
                    value = this.clitics.toString();
                    break;
                case "Cli":
                    value = this.cli.toString();
                    break;
            }

            // Store the pos or feature information.
            if (!value.isEmpty()) {
                if (derivationalFeatures.contains(posFeature)) {
                    derivationalStrings.add(value);
                } else inflectionalStrings.add(value);
            }

        }

        String derivationalInfo = StringUtils.join(derivationalStrings, "-");
        String inflectionalInfo = StringUtils.join(inflectionalStrings, "+");
        this.originalDictionaryString = derivationalInfo + ":" + inflectionalInfo;

        return this.originalDictionaryString;
    }

    public boolean agreesWith(ItalianReading other) {
        // A single feature agrees if the following is true.
        //   IF _this_ doesn't have the feature
        //   OR _that_ doesn't have the feature
        //   OR the features match.
        // If all features agree, then the feature sets agree.
        return     (!this.hasGender()  || !other.hasGender()  ||
                this.gender.toString().toLowerCase().equals(other.gender.toString().toLowerCase()))

                && (!this.hasNumber()  || !other.hasNumber()  ||
                this.number.toString().toLowerCase().equals(other.number.toString().toLowerCase()))

                && (!this.hasPerson()  || !other.hasPerson()  ||
                this.person.toString().toLowerCase().equals(other.person.toString().toLowerCase()))

                && (!this.hasDegree()  || !other.hasDegree()  ||
                this.degree.toString().toLowerCase().equals(other.degree.toString().toLowerCase()))

                && (!this.hasMood()    || !other.hasMood()    ||
                this.mood.toString().toLowerCase().equals(other.mood.toString().toLowerCase()))

                && (!this.hasTense()   || !other.hasTense()   ||
                this.tense.toString().toLowerCase().equals(other.tense.toString().toLowerCase()))

                && (!this.hasClitics() || !other.hasClitics() ||
                this.clitics.toString().toLowerCase().equals(other.clitics.toString().toLowerCase()))

                && (!this.hasCli()     || !other.hasCli()     ||
                this.cli.toString().toLowerCase().equals(other.cli.toString().toLowerCase()));
    }

    private void parsePosTag() {
        // TODO
//        // LanguageTool adds an additional reading or interpretation to the last word in a source.
//        // This is typically the source ending punctuation mark, but in reality could be any token.
//        // We're ignoring the source-ending marker since it doesn't come from the dictionary.
//        String posTag = interpretation.getPOSTag();
//        assert posTag != null;
//        if (posTag.equals("SENT_END")) continue;


        String posTag = this.analyzedToken.getPOSTag();
        if (posTag == null || posTag.isEmpty()) return;

        // Attempt to split the information into derivational and inflectional parts.
        String derivationalInflectionalSeparator = ":";
        String[] parts = posTag.split(derivationalInflectionalSeparator);
        String derivationalInfo = parts[0];

        // Process the derivational feature information.
        String derivationalSeparator = "-";
        parsingDerivations = true;
        parsePosTag(derivationalInfo, derivationalSeparator);

        // If the info string also has inflectional features, parse them and add them to the features object.
        if (parts.length > 1) {
            String inflectionalInfo = parts[1];
            String inflectionalSeparator = "+";
            parsingDerivations = false;
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

            // Gender
            // This if statement will always be false.  Just keeping for consistency.
            // TODO: Should I throw an exception if two features are defined for the same token?
            if (!foundMatch && this.gender == null) {
                try {
                    this.gender = Feature.Gender.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Gender");
                    if (parsingDerivations) derivationalFeatures.add("Gender");
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Number
            if (!foundMatch && this.number == null) {
                try {
                    this.number = Feature.Number.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Number");
                    if (parsingDerivations) derivationalFeatures.add("Number");
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Person
            if (!foundMatch && this.person == null) {
                switch (featureName) {
                    case "1":
                        this.person = Feature.Person.First;
                        break;
                    case "2":
                        this.person = Feature.Person.Second;
                        break;
                    case "3":
                        this.person = Feature.Person.Third;
                        break;
                }
                if (this.person != null) {
                    posFeatureOrder.put(fromIndex, "Person");
                    if (parsingDerivations) derivationalFeatures.add("Person");
                    foundMatch = true;
                }
            }

            // Degree
            if (!foundMatch && this.degree == null) {
                try {
                    this.degree = Feature.Degree.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Degree");
                    if (parsingDerivations) derivationalFeatures.add("Degree");
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Mood
            if (!foundMatch && this.mood == null) {
                try {
                    this.mood = Feature.Mood.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Mood");
                    if (parsingDerivations) derivationalFeatures.add("Mood");
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Tense
            if (!foundMatch && this.tense == null) {
                try {
                    this.tense = Feature.Tense.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Tense");
                    if (parsingDerivations) derivationalFeatures.add("Tense");
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Clitics
            if (!foundMatch && this.clitics == null) {
                try {
                    this.clitics = Feature.Clitics.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Clitics");
                    if (parsingDerivations) derivationalFeatures.add("Clitics");
                    foundMatch = true;
                } catch (IllegalArgumentException ignored) { }
            }

            // Cli
            if (!foundMatch && this.cli == null) {
                try {
                    this.cli = Feature.Cli.valueOf(featureName);
                    posFeatureOrder.put(fromIndex, "Cli");
                    if (parsingDerivations) derivationalFeatures.add("Cli");
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
                    posFeatureOrder.put(fromIndex, "Pos");
                    if (parsingDerivations) derivationalFeatures.add("Pos"); // Always true for Pos.
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

    public void matchOverlappingFeatures(ItalianReading other) {
        if (this.hasGender() && other.hasGender() && this.gender != other.gender)
            this.gender = other.gender;

        if (this.hasNumber() && other.hasNumber() && this.number != other.number)
            this.number = other.number;

        if (this.hasPerson() && other.hasPerson() && this.person != other.person)
            this.person = other.person;

        if (this.hasDegree() && other.hasDegree() && this.degree == other.degree)
            this.degree = other.degree;

        if (this.hasMood() && other.hasMood() && this.mood == other.mood)
            this.mood = other.mood;

        if (this.hasTense() && other.hasTense() && this.tense == other.tense)
            this.tense = other.tense;

        if (this.hasClitics() && other.hasClitics() && this.clitics == other.clitics)
            this.clitics = other.clitics;

        if (this.hasCli() && other.hasCli() && this.cli == other.cli)
            this.cli = other.cli;
    }
}
