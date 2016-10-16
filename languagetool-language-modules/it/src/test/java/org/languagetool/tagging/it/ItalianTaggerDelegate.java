package org.languagetool.tagging.it;

import org.languagetool.AnalyzedToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littl on 10/15/2016.
 */
public class ItalianTaggerDelegate extends ItalianTagger{
    public int tokenIndex = 0;
    public List<String> tags = new ArrayList<String>();
    public List<String> lemmas = new ArrayList<String>();

    public ItalianTaggerDelegate() {
        super();
    }

    @Override
    public List<AnalyzedToken> getAnalyzedTokens(String word) {
        List<AnalyzedToken> analyzedTokens = new ArrayList<AnalyzedToken>();

        String tag = tags.get(tokenIndex);
        String lemma = lemmas.get(tokenIndex);
        AnalyzedToken analyzedToken = new AnalyzedToken(word, tag, lemma);
        analyzedTokens.add(analyzedToken);

        // Increment token index for next word.
        tokenIndex++;

        return analyzedTokens;
    }
}
