package org.languagetool;

import org.languagetool.language.Italian;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.it.AgreementRule;
import org.languagetool.tagging.it.CoNLL;
import org.languagetool.tagging.it.ItalianSentence;
import org.languagetool.tagging.it.ItalianToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by littl on 6/1/2016.
 */
public class CeilingAnalysis {
    private ResourceBundle ItalianResourceBundle;
    private ResourceBundle getResourceBundle() {
        if (ItalianResourceBundle == null) {
            ItalianResourceBundle = TestTools.getMessages("it");
        }
        return ItalianResourceBundle;
    }

    private JLanguageTool lt;
    private JLanguageTool getJLanguageTool() {
        if (lt == null) {
            lt = new JLanguageTool(new Italian());
        }
        return lt;
    }



    // Article Test
    // Fail Rate   Pass Rate   Pass Rate
    // 67/145  ->   3/144  ->  6/144
    // 72/145  ->  11/144  ->  9/144 (extra reversed agreement rule)

    // Verb Test
    // 16/43   ->  xx/xx   ->  6/43

    // Phases in the NLP pipeline.
    // Phase 1: Break sentence into tokens.
    // Phase 2: Annotate tokens with POS tags.
    // Phase 3: Annotate tokens with dependency information.
    // Phase 4: Use heuristics to check agreement.

    // Levels for ceiling analysis.
    // Level 0: LanguageTools completes all phases.
    // Level 1: Tokens are provided.  LanguageTool executes phases 2 - 4.
    // Level 2: Tokens and POS tags are provided via gold file.
    //          LanguageTool executes phases 3 & 4.
    // Level 3: Tokens, POS tags and dependency information are all provided.
    //          LanguageTool only executes the heuristics in phase 4.

    CeilingAnalysisResults AnalyzeLevelZero(List<TestSentence> testSentences) throws IOException {
        // Setup results object.
        CeilingAnalysisResults results = new CeilingAnalysisResults();
        results.ReportHeader = "Performing Level 0 Ceiling Analysis on test sentences with no ground truth data.";

        // Setup LanguageTool and Resource bundle. These object are needed
        // for tokenizing, tagging and generating dependency information.
        JLanguageTool lt = getJLanguageTool();
        ResourceBundle italianResourceBundle = getResourceBundle();

        // Iterate through all the test sentences.
        for (int i = 0; i < testSentences.size(); i++) {

            // Grab the test case from the list.
            TestSentence testCase = testSentences.get(i);

            // Grab the italian sentence from the test case.
            String s = testCase.Sentence;

            // Break the sentence into tokens, tag the parts of speech,
            // and generate the dependency structure of the sentence.
            AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence(s);

            // Instantiate the AgreementRule and check for matches.
            AgreementRule rule = new AgreementRule(italianResourceBundle);
            RuleMatch[] matches = rule.match(analyzedSentence);

            // Update the results object to know we tested another sentence.
            results.TestCount++;
            results.TestResults.add((i+1) + ": ");

            // Track pass/fail statistics.
            String testResult = "PASS: ";
            if (matches.length == testCase.ExpectedErrorCount) {
                if (testCase.ExpectedErrorCount > 0) results.IncorrectPassCount++;
                else results.CorrectPassCount++;
            }
            else testResult = "FAIL: ";

            // Include indication of whether a correct or incorrect sentence is being tested.
            if (testCase.ExpectedErrorCount > 0) testResult = testResult + "(Incorrect) ";
            else testResult = testResult + "(Correct) ";

            // Store the results of the test.
            results.TestResults.add(testResult + s);
            for (RuleMatch match : matches) {
                results.TestResults.add("RULE MATCHED: " + match.getMessage());
            }
            results.TestResults.add("");
        }

        return results;
    }

    CeilingAnalysisResults AnalyzeLevelOne(String fileName, List<TestSentence> testSentences) throws IOException {
        // Setup results object.
        CeilingAnalysisResults results = new CeilingAnalysisResults();
        results.ReportHeader = "Performing Level 1 Ceiling Analysis on test sentences with tokens from ground truth data from '" + fileName + "'";

        // Read in data from gold file.
        List<ItalianSentence> sentences = CoNLL.loadItalianFile(fileName);

        // Iterate through all the sentences.
        for (int i = 0; i < sentences.size(); i++) {
            ItalianSentence italianSentence = sentences.get(i);

            // Grab the tokens from the gold file.
            List<String> tokens = new ArrayList<String>();
            for (ItalianToken token : italianSentence.tokens) {
                tokens.add(token.getToken());
            }

            // Use LanguageTool to get the POS tags and dependency information.
            AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence(tokens);

            // Even sentences (0, 2, 4, ...) represent sentences with an error in them.
            // Odd sentences (1, 3, 5, ...) represent sentences with no errors.
            int expectedErrorCount = (i + 1) % 2;

            AgreementRule rule = new AgreementRule(ItalianResourceBundle);
            RuleMatch[] matches = rule.match(analyzedSentence);

            // Update the results object to know we tested another sentence.
            results.TestCount++;
            results.TestResults.add((i+1) + ": ");

            // Track pass/fail statistics.
            String testResult = "PASS: ";
            if (matches.length == expectedErrorCount) {
                if (expectedErrorCount > 0) results.IncorrectPassCount++;
                else results.CorrectPassCount++;
            }
            else testResult = "FAIL: ";

            // Include indication of whether a correct or incorrect sentence is being tested.
            if (expectedErrorCount > 0) testResult = testResult + "(Incorrect) ";
            else testResult = testResult + "(Correct) ";

            // Store the results of the test.
            results.TestResults.add(testResult + testSentences.get(i).Sentence);
            for (RuleMatch match : matches) {
                results.TestResults.add("RULE MATCHED: " + match.getMessage());
            }
            results.TestResults.add("");

        }

        return results;
    }

    CeilingAnalysisResults AnalyzeLevelThree(String fileName, List<TestSentence> testSentences) throws IOException {
        // Setup results object.
        CeilingAnalysisResults results = new CeilingAnalysisResults();
        results.ReportHeader = "Performing Level 3 Ceiling Analysis with ground truth data from '" + fileName + "'";

        // Read in data from gold file.
        List<ItalianSentence> sentences = CoNLL.loadItalianFile(fileName);

        // Evaluate the data.
        for (int i=0; i<sentences.size(); i++) {
            ItalianSentence sentence = sentences.get(i);
            results.TestCount++;
            results.TestResults.add((i+1) + ": ");

            // Even sentences (0, 2, 4, ...) represent sentences with an error in them.
            // Odd sentences (1, 3, 5, ...) represent sentences with no errors.
            int expectedErrorCount = (i + 1) % 2;

            AgreementRule rule = new AgreementRule(ItalianResourceBundle);
            RuleMatch[] matches = rule.match(sentence);

            // Track pass/fail statistics.
            String result = "PASS: ";
            if (matches.length == expectedErrorCount) {
                if (expectedErrorCount > 0) results.IncorrectPassCount++;
                else results.CorrectPassCount++;
            }
            else result = "FAIL: ";

            // Include indication of whether a correct or incorrect sentence is being tested.
            if (expectedErrorCount > 0) result = result + "(Incorrect) ";
            else result = result + "(Correct) ";

            // Store the results.
            results.TestResults.add(result + testSentences.get(i).Sentence);
            for (RuleMatch match : matches) {
                results.TestResults.add("RULE MATCHED: " + match.getMessage());
            }
            results.TestResults.add("");

        } // Done processing sentences in file.

        return results;
    }
}
