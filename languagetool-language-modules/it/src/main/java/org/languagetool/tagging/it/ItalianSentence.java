package org.languagetool.tagging.it;

import org.apache.commons.lang.StringUtils;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.language.Italian;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littl on 5/21/2016.
 */
public class ItalianSentence {
    private AnalyzedSentence analyzedSentence;
    public ItalianToken[] tokens;

    // Constructor is private to force use of the create() method.
    private ItalianSentence(AnalyzedSentence sentence) {
        this.analyzedSentence = sentence;
    }

/*
    public static ItalianSentence create(String[] tokens) {
        ItalianSentence sentence = new ItalianSentence();
        return sentence;
    }
*/

    // Use of a create() method prevents construction of an
    // object without initialization of requisite Italian tokens.
    public static ItalianSentence create(AnalyzedSentence sentence) {
        ItalianSentence italianSentence = new ItalianSentence(sentence);
        italianSentence.initializeItalianTokens();
        return italianSentence;
    }

    // Convert AnalyzedTokenReadings to Italian Tokens.
    private void initializeItalianTokens() {
        ArrayList<ItalianToken> italianTokens = new ArrayList<>();
        for (AnalyzedTokenReadings token : this.analyzedSentence.getTokens()) {
            if (token instanceof ItalianToken)
                italianTokens.add((ItalianToken) token);
        }
        this.tokens = italianTokens.toArray(new ItalianToken[italianTokens.size()]);
    }

    public String[] toCoNLL(boolean useRoot) {
        ArrayList<String> wordLines = new ArrayList<>();

        // Output the token number and the rest of the token
        // information, which is already in CoNLL-X format.
        for (int i=0; i<tokens.length; i++) {
            ItalianToken token = tokens[i];
            wordLines.add((i+1) + "\t" + token.toCoNLL(useRoot));
        }

        return wordLines.toArray(new String[wordLines.size()]);
    }

    public String[] toCoNLL() {
        return toCoNLL(false);
    }

    @Override
    public String toString() {
        List<String> words = new ArrayList<String>();
        for (ItalianToken token : tokens) {
            words.add(token.getToken());
        }
        return StringUtils.join(words, " ");
    }
}
