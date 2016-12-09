package org.languagetool;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
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
//    private static String fileName = "Agreement Test Gold File (with commas).conl";
    private static String fileName = "Agreement Test Gold File Constrained.conl";
    private static String logFileName = "Agreement Ceiling Analysis Level ";
    private static CeilingAnalysis ceilingAnalysis = new CeilingAnalysis();

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
            //boolean isValid = CoNLL.Validate(fileName);
            boolean isValid = CoNLL.Validate("C:\\Users\\littl\\Downloads\\MaltOptimizer-1.0.3\\all.conl");
            assertTrue(isValid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Ignore
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

    // 10/15/2016
    // Nil -> Tok -> Tag -> Dep
    // 103 -> 105 -> 96  -> 98

    // 10/16/2016 (with commas)
    // 103 -> 105 -> 97  -> 98

    // 10/17/2106
    // 103 -> 105 -> 97  -> 98
    // 103 -> 105 -> 97  -> 95

    // Stages:       0        1        2        3
    // We provide:  nil     token     POS      Dep
    // 10/18/2016: 0.558 -> 0.571 -> 0.474 -> 0.696
    // 10/25/2016: 0.500 -> 0.500 -> 0.500 -> 0.929

    // After verb agreement rule rewrite.
    // Stages:       0        1        2        3
    // We provide:  nil     token     POS      Dep
    // 11/01/2016: 0.171 -> 0.176 -> 0.182 -> 0.182
    // 11/02/2016: 0.475 -> 0.480 -> 0.300 -> 0.522
    // 11/02/2016: 0.519 -> 0.528 -> 0.408 -> 0.571
    // 11/02/2016: 0.538 -> 0.549 -> 0.490 -> 0.769
    // 11/05/2016: 0.593 -> 0.630 -> 0.667 -> 0.966
    // 11/06/2016: 0.571 -> 0.632 -> 0.611 -> 0.854
    // 11/06/2016: 0.579 -> 0.613 -> 0.563 -> 0.950
    // 11/06/2016: 0.613 -> 0.613 -> 0.563 -> 0.950     LAS: 0.549 -> 0.549 -> 0.461 -> 1.000
    // 11/12/2016: 0.441 -> 0.441 -> 0.563 -> 0.950     LAS: 0.393 -> 0.393 -> 0.469 -> 1.000
    // 11/23/2016: 0.488 -> 0.488 -> 0.488 -> 0.927     LAS: 0.624 -> 0.624 -> 0.534 -> 1.000

    @Test
    public void CeilingAnalysis() throws IOException {
        CeilingAnalysisLevelZero();
        CeilingAnalysisLevelOne();
        CeilingAnalysisLevelTwo();
        CeilingAnalysisLevelThree();
    }

    @Test
    public void CeilingAnalysisLevelZero() throws IOException {
        CeilingAnalysisResults levelZeroResults = ceilingAnalysis.AnalyzeLevelZero(AgreementTest.testSentences);
        levelZeroResults.SaveReport(logFileName + "0.txt");
        levelZeroResults.SaveConl(logFileName + "0.conl");
    }

    @Test
    public void CeilingAnalysisLevelOne() throws IOException {
        CeilingAnalysisResults levelOneResults = ceilingAnalysis.AnalyzeLevelOne(fileName, AgreementTest.testSentences);
        levelOneResults.SaveReport(logFileName + "1.txt");
        levelOneResults.SaveConl(logFileName + "1.conl");
    }

    @Test
    public void CeilingAnalysisLevelTwo() throws IOException {
        CeilingAnalysisResults levelTwoResults = ceilingAnalysis.AnalyzeLevelTwo(fileName, AgreementTest.testSentences);
        levelTwoResults.SaveReport(logFileName + "2.txt");
        levelTwoResults.SaveConl(logFileName + "2.conl");
    }

    @Test
    public void CeilingAnalysisLevelThree() throws IOException {
        CeilingAnalysisResults levelThreeResults =  ceilingAnalysis.AnalyzeLevelThree(fileName, AgreementTest.testSentences);
        levelThreeResults.SaveReport(logFileName + "3.txt");
        levelThreeResults.SaveConl(logFileName + "3.conl");
    }
}