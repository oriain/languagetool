package org.languagetool;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.it.AgreementRule;
import org.languagetool.rules.it.AgreementTest;
import org.languagetool.tagging.it.CoNLL;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by littl on 10/15/2016.
 */
public class CeilingAnalysisTest {
    private static ResourceBundle ItalianResourceBundle = TestTools.getMessages("it");
    private static AgreementRule rule = new AgreementRule(ItalianResourceBundle);
    private static String fileName = "Agreement Test.conl";

    private void assertGood(AnalyzedSentence sentence) throws IOException {
        RuleMatch[] matches = rule.match(sentence);
        assertThat(matches.length, is(0));
    }

    private void assertBad(AnalyzedSentence sentence) throws IOException {
        RuleMatch[] matches = rule.match(sentence);
        assertThat(matches.length, is(1));
    }

    @Before
    public void ruleSetup() {
        rule.reset();
    }

    @Test
    public void validateFile(){
        try {
            boolean isValid = CoNLL.Validate(fileName);
            assertTrue(isValid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SplitFeaturesFromPOS()
    {
        try {
            FileWriter fw = new FileWriter("Processed " + fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            Path path = Paths.get(fileName);
            Stream<String> lines = Files.lines(path);
            for( String line : (Iterable<String>) lines::iterator ) {
                // Pass through blank lines.
                if (line.equals("") ) {
                    out.println();
                    continue;
                }

                // Split the line into its columns.
                String[] columns = line.split("\t");

                // If the POS has no features, blank out that column.
                if (!columns[3].contains(":")) {
                    columns[5] = "_";
                }
                else {
                    String[] parts = columns[3].split(":");
                    // Split the features from the pos tag.
                    if (parts.length == 2){
                        columns[3] = parts[0]; // Course Grain POS
                        columns[4] = parts[0]; // Fine Grain POS
                        columns[5] = parts[1]; // Features
                    }
                    // Do not split features if more than one semicolon is detected.
                    // In that case we encountered something unexpected.
                }

                // Rejoin the columns into a line and output to the processed file.
                String processedLine = StringUtils.join(columns, "\t");
                out.println(processedLine);
            }

            out.println("");
            out.close();
        }
        catch (IOException e) {
            // caught the exceptions...
        }

    }

    @Test
    public void CeilingAnalysis() throws IOException {
        CeilingAnalysis ceilingAnalysis = new CeilingAnalysis();

        CeilingAnalysisResults levelZeroResults = ceilingAnalysis.AnalyzeLevelZero(AgreementTest.testSentences);
        levelZeroResults.SaveReport("Agreement Ceiling Analysis Level 0.txt");

        CeilingAnalysisResults levelOneResults = ceilingAnalysis.AnalyzeLevelOne(fileName, AgreementTest.testSentences);
        levelOneResults.SaveReport("Agreement Ceiling Analysis Level 1.txt");

        //CeilingAnalysisResults levelTwoResults = ceilingAnalysis.AnalyzeLevelZero(AgreementTest.testSentences);
        //levelTwoResults.SaveReport("Agreement Ceiling Analysis Level 2.txt");

        CeilingAnalysisResults levelThreeResults =  ceilingAnalysis.AnalyzeLevelThree(fileName, AgreementTest.testSentences);
        levelThreeResults.SaveReport("Agreement Ceiling Analysis Level 3.txt");
    }
}
