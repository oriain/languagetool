package org.languagetool;

import java.io.*;
import java.util.ArrayList;
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

        int correctTestCount = TestCount /2;
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
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);

        for (String line : getReport()) {
            out.println(line);
        }

        out.close();
    }
}
