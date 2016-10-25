package org.languagetool;

import org.apache.commons.lang.NotImplementedException;
import org.languagetool.tagging.it.ItalianSentence;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by littl on 10/15/2016.
 */
class CeilingAnalysisResults {
    String ReportHeader;
    int TestCount = 0;
    int IncorrectPassCount = 0;
    int CorrectPassCount = 0;
    List<String> TestResults = new ArrayList<String>();

    private List<String> conlData = new ArrayList<String>();
    void LogConlData(AnalyzedSentence sentence) {
        ItalianSentence italianSentence = ItalianSentence.create(sentence);
        LogConlData(italianSentence);
    }
    void LogConlData(ItalianSentence sentence) {
        boolean useRoot = true;
        String[] wordlines = sentence.toCoNLL(useRoot);
        Collections.addAll(conlData, wordlines);
        conlData.add("");
    }
    void SaveConl(String fileName) throws IOException {
        outputFile(fileName, conlData);
    }

    private List<String> report = new ArrayList<String>();
    private List<String> getReport() {
        if (report.isEmpty()) GenerateReport();
        return report;
    }

    private List<String> GenerateStats() {
        List<String> stats = new ArrayList<String>();

        int incorrectTestCount = (int)Math.ceil(TestCount /2.0);
        int incorrectFailCount = incorrectTestCount - IncorrectPassCount;
        stats.add("Incorrect Sentences: " + incorrectTestCount);
        stats.add("Passed: " + IncorrectPassCount + " / " + incorrectTestCount);
        stats.add("Failed: " + incorrectFailCount + " / " + incorrectTestCount);
        stats.add("");

        int correctTestCount = TestCount/2;
        int correctFailCount = correctTestCount - CorrectPassCount;
        stats.add("Correct Sentences: " + correctTestCount);
        stats.add("Passed: " + CorrectPassCount + " / " + correctTestCount);
        stats.add("Failed: " + correctFailCount + " / " + correctTestCount);
        stats.add("");

        int passCount = IncorrectPassCount + CorrectPassCount;
        stats.add("Passed Sentences: " + passCount);
        stats.add("Incorrect: " + IncorrectPassCount + " / " + passCount);
        stats.add("Correct: " + CorrectPassCount + " / " + passCount);
        stats.add("");

        int failCount = incorrectFailCount + correctFailCount;
        stats.add("Failed Sentences: " + failCount);
        stats.add("Incorrect: " + incorrectFailCount + " / " + failCount);
        stats.add("Correct: " + correctFailCount + " / " + failCount);
        stats.add("");

        stats.add("Total Sentences: " + TestCount);
        stats.add("Passed: " + passCount + " / " + TestCount);
        stats.add("Failed: " + failCount + " / " + TestCount);
        stats.add("");

        // https://en.wikipedia.org/wiki/F1_score
        // https://en.wikipedia.org/wiki/Precision_and_recall

        // The F1 score (also F-score or F-measure) is a measure of a test's accuracy.
        // It considers both the precision p and the recall r of the test to compute the score.
        // f1 = 2 * (precision * recall) / (precision + recall)

        // p (precision) is the number of correct (true) positive results
        // divided by the number of all (true & false) positive results.
        // p = TP / (TP + FP)

        // r (recall) is the number of correct positive results divided by
        // the number of positive results that should have been returned.
        // r = TP / (TP + FN)

        // In our case True Positives will be the number of incorrect
        // sentences for which we detected a single error.

        // False Positives will be the number of correct sentences
        // for which we detect one or more errors.

        // False Negatives will be the number of incorrect sentences
        // for which a single error was not detected.  Specifically,
        // this could mean zero or more than one error was detected.

        // p = TP / (TP + FP)
        int incorrectPassCount = incorrectTestCount - incorrectFailCount;
        double precision = incorrectPassCount / (double)(incorrectPassCount + correctFailCount);

        // r = TP / (TP + FN)
        double recall = incorrectPassCount / (double)incorrectTestCount;

        double f1 = 2 * (precision * recall) / (precision + recall);

        stats.add("Precision: " + precision);
        stats.add("Recall: " + recall);
        stats.add("F1 Score: " + f1);

        return stats;
    }
    private List<String> GenerateReport() {
        if (report.isEmpty()) {
            
            // Add header to report.
            if (ReportHeader != null && !ReportHeader.isEmpty()) {
                report.add(ReportHeader);
                report.add("");
            }
            
            // Add test results.
            for(String result : TestResults) {
                report.add(result);
            }
            
            // Add footer.
            List<String> stats = GenerateStats();
            for (String statistic : stats) {
                report.add(statistic);
            }
            
        }
        return report;
    }

    // Output report to console.
    public void PrintReport() {
        for (String line : getReport()) {
            System.out.println(line);
        }
    }

    // Output results to file.
    void SaveReport(String fileName) throws IOException {
        outputFile(fileName, getReport());
    }

    private void outputFile(String fileName, List<String> lines) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);

        for (String line : lines) {
            out.println(line);
        }

        out.close();
    }
}
