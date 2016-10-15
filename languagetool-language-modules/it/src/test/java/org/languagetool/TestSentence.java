package org.languagetool;

/**
 * Created by littl on 10/14/2016.
 */
public class TestSentence {
    String Code;
    String Sentence;
    String Translation;
    int ExpectedErrorCount;

    public TestSentence(String code, String translation, String sentence, int expectedErrorCount) {
        Code = code;
        Translation = translation;
        Sentence = sentence;
        ExpectedErrorCount = expectedErrorCount;
    }
}
