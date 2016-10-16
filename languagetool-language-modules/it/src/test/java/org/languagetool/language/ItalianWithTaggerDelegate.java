package org.languagetool.language;

import org.languagetool.tagging.it.ItalianTaggerDelegate;

/**
 * Created by littl on 10/15/2016.
 */
public class ItalianWithTaggerDelegate extends Italian
{
    private ItalianTaggerDelegate tagger;

    @Override
    public ItalianTaggerDelegate getTagger() {
        if (tagger == null) {
            tagger = new ItalianTaggerDelegate();
        }
        return tagger;
    }
}
