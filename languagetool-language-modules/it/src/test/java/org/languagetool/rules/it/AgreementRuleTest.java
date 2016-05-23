package org.languagetool.rules.it;

import org.junit.Before;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Italian;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by littl on 5/21/2016.
 */
public class AgreementRuleTest {
    private JLanguageTool langTool;
    private AgreementRule rule;

    @Before
    public void setup() {
        this.langTool = new JLanguageTool(new Italian());
        this.rule = new AgreementRule();
    }

    @Test
    public void testAgreement() throws IOException {
        // The code, and some special laws, make provision for those institutions through which the social function is to be realized.
        assertGood("Il codice, e alcune leggi speciali, prevedono alcuni istituti tramite i quali realizzare la funzione sociale.");

        // Mr. Anthony has eaten the food.
        //assertGood("Signor Anthony ha mangiato il cibo.");

        // The owner has the right to enjoy (1) and place (2) of things so full (3) and exclusive (4), within the limits and with the observance of the obligations established by the legal system [Const. 42, 43, 44].
        //assertGood("Il proprietario ha diritto di godere (1) e disporre (2) delle cose in modo pieno (3) ed esclusivo (4), entro i limiti e con l'osservanza degli obblighi stabiliti dall'ordinamento giuridico [Cost. 42, 43, 44].");

    }

    private void assertGood(String s) throws IOException {
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence(s));
        assertEquals("Found unexpected match in sentence '" + s + "': " + Arrays.toString(matches), 0, matches.length);
    }
}
