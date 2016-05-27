package org.languagetool.synthesis;

/**
 * Created by littl on 5/25/2016.
 */
public class ItalianSynthesizer extends BaseSynthesizer {

    public ItalianSynthesizer() {
        super("/it/italian_synth.dict", "/it/italian_tags.txt");
    }
}
