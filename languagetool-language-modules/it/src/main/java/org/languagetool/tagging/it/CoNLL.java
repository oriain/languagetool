package org.languagetool.tagging.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.it.tag.DependencyRelation;
import org.languagetool.tokenizers.WordTokenizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by littl on 5/21/2016.
 */
public class CoNLL {

    public static void writeFile(ItalianSentence sentence, String filepath) throws IOException {
        // Output the CoNLL file for easier viewing.
        //        PrintWriter out = new PrintWriter(filepath);
        FileWriter fw = new FileWriter(filepath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);

        boolean useRoot = true;
        for (String wordline : sentence.toCoNLL(useRoot)) {
            out.println(wordline);
        }
        out.println("");
        out.close();
    }

    public static List<ItalianSentence> loadItalianFile(String filepath) throws IOException {
        List<ItalianSentence> sentences = new ArrayList<ItalianSentence>();
        List<ItalianToken> tokens = new ArrayList<ItalianToken>();
        HashMap<Integer, Integer> dependencies = new HashMap<Integer, Integer>();

        Path path = Paths.get(filepath);
        Stream<String> lines = Files.lines(path);

        int startPosition = -1;
        for( String line : (Iterable<String>) lines::iterator ) {
            // If we have a blank line, that's an indicator of the end of a sentence.
            if (line.isEmpty() && !tokens.isEmpty()) {
                // Connect the dependencies since the sentence has ended.
                for (Map.Entry<Integer, Integer> entry : dependencies.entrySet()) {
                    int headNumber = entry.getValue();
                    if (headNumber > 0)
                    {
                        int tokenNumber = entry.getKey();
                        ItalianToken token = tokens.get(tokenNumber-1);
                        token.head = tokens.get(headNumber-1);
                        token.head.addChild(token);
                    }
                }
                // Clear the dependencies hashmap for the next sentence.
                dependencies.clear();

                // Put everything together.
                ItalianToken[] sentenceTokens = tokens.toArray(new ItalianToken[tokens.size()]);
                AnalyzedSentence sentence = new AnalyzedSentence(sentenceTokens);
                ItalianSentence italianSentence = ItalianSentence.create(sentence);
                sentences.add(italianSentence);
                tokens.clear();
            }
            // Process the next token in the sentence.
            else {
                ItalianToken token = parseItalianWordline(line, startPosition);
                tokens.add(token);
                startPosition = token.getEndPos();

                // Save token for dependency relation processing.
                // Parse the head value
                int headNumber = -1;
                try { headNumber = Integer.parseInt(line.split("\t")[6]); }
                catch (NumberFormatException ignored) { /* Do nothing... */ }
                if (headNumber > -1) dependencies.put(tokens.size(), headNumber);
            }
        }

        // This is duplicate code from the if statement above.
        // Needed to finish handling the last sentence
        // Should probably be put into its own function.
        if (!tokens.isEmpty()) {

            // Connect the dependencies since the sentence has ended.
            for (Map.Entry<Integer, Integer> entry : dependencies.entrySet()) {
                int headNumber = entry.getValue();
                if (headNumber > 0) {
                    int tokenNumber = entry.getKey();
                    ItalianToken token = tokens.get(tokenNumber - 1);
                    token.head = tokens.get(headNumber - 1);
                }
            }
            // Clear the dependencies hashmap for the next sentence.
            dependencies.clear();

            // Put everything together.
            ItalianToken[] sentenceTokens = tokens.toArray(new ItalianToken[tokens.size()]);
            AnalyzedSentence sentence = new AnalyzedSentence(sentenceTokens);
            ItalianSentence italianSentence = ItalianSentence.create(sentence);
            sentences.add(italianSentence);
            tokens.clear();

        } // End of check for empty tokens list.

        return sentences;
    }

    public static List<AnalyzedSentence> loadFile(String filepath) throws IOException {
        List<AnalyzedSentence> sentences = new ArrayList<>();
        List<AnalyzedTokenReadings> tokens = new ArrayList<>();

        Path path = Paths.get(filepath);
        Stream<String> lines = Files.lines(path);

        int startPosition = -1;
        for( String line : (Iterable<String>) lines::iterator ) {
            // If we have a blank line, that's an indicator of the end of a sentence.
            if (line.isEmpty()) {
                AnalyzedTokenReadings[] sentenceTokens = tokens.toArray(new AnalyzedTokenReadings[tokens.size()]);
                AnalyzedSentence sentence = new AnalyzedSentence(sentenceTokens);
                sentences.add(sentence);
                tokens.clear();
            }
            // Process the next token in the sentence.
            else {
                AnalyzedTokenReadings token = parseWordline(line, startPosition);
                tokens.add(token);
                startPosition = token.getEndPos();
            }
        }

        return sentences;
    }

    // TODO: Validate input.
    // i.e. There should only be a specific number of columns.
    // Which columns and what data are required?
    private static AnalyzedTokenReadings parseWordline(String wordline, int startPosition) {

        // Process the columns of the wordline.
        String[] column = wordline.split("\t");
        String id = column[0];
        String wordform = column[1];
        String lemma = column[2];
        String cpos = column[3];
        String fpos = column[4];
        String feat = column[5];
        String head = column[6];
        String depRel = column[7];

        // Get the readings for the word.
        List<AnalyzedToken> readings = processReadings(wordform, lemma, cpos, feat);

        // If the wordoform is not a tokenizing character (if the sentence is not split on the word)
        // then add one to the starting position of the token to account for the implied space between words.
        // TODO: NOT SURE HOW WELL THIS WORKS.
        boolean wordformIsTokenizingCharacter = new WordTokenizer().getTokenizingCharacters().contains(wordform);
        if (!wordformIsTokenizingCharacter) startPosition++;

        // Construct and return the token.
        return new AnalyzedTokenReadings(readings, startPosition);
    }

    private static ItalianToken parseItalianWordline(String wordline, int startPosition) {

        // Process the columns of the wordline.
        String[] column = wordline.split("\t");
        String id = column[0];
        String wordform = column[1];
        String lemma = column[2];
        String cpos = column[3];
        String fpos = column[4];
        String feat = column[5];
        String head = column[6]; // Will be processed outside this function.
        String depRel = column[7];

        // Get the readings for the word.
        List<AnalyzedToken> readings = processReadings(wordform, lemma, cpos, feat);

        // If the wordoform is not a tokenizing character (if the sentence is not split on the word)
        // then add one to the starting position of the token to account for the implied space between words.
        // TODO: NOT SURE HOW WELL THIS WORKS.
        boolean wordformIsTokenizingCharacter = new WordTokenizer().getTokenizingCharacters().contains(wordform);
        if (!wordformIsTokenizingCharacter) startPosition++;

        // Parse the token number
        int tokenNumber = 0;
        try { tokenNumber = Integer.parseInt(id); }
        catch (NumberFormatException ignored) { /* Do nothing... */ }

        // Construct the token.
        AnalyzedTokenReadings analyzedTokenReadings = new AnalyzedTokenReadings(readings, startPosition);
        ItalianToken italianToken = ItalianToken.create(analyzedTokenReadings, tokenNumber);

        // Parse the dependency relation
        String sanitizedDepRel = depRel.replace("*", "_").replace("+", "_").replace("%", "_").replace("/", "_");
        try { italianToken.dependencyRelation = DependencyRelation.valueOf(sanitizedDepRel); }
        catch (IllegalArgumentException ignored) { /* Do nothing?... */ }

        return italianToken;
    }

    private static List<AnalyzedToken> processReadings(String wordform, String lemma, String cpos, String feat) {
        List<AnalyzedToken> readings = new ArrayList<>();

        // If there is more than one reading for this token, process in a loop.
        if (lemma.startsWith("{")) {
            String[] lemmaReadings = lemma.substring(1, lemma.length() - 1).split(",");
            String[] posReadings = cpos.substring(1, cpos.length() - 1).split(",");
            String[] featReadings = feat.substring(1, feat.length() - 1).split(",");
            for (int i=0; i<posReadings.length; i++) {
                String lemmaReading = lemmaReadings[i].split("=")[1];
                String posReading = posReadings[i].split("=")[1];
                String featReading = featReadings[i].split("=")[1];
                String posTag = (featReading.equals("_")) ? cpos : posReading+":"+featReading;
                AnalyzedToken token = new AnalyzedToken(wordform, posTag, lemmaReading);
                readings.add(token);
            }
        }
        // If there is only a single reading for this token.
        else {
            String posTag = (feat.equals("_")) ? cpos : cpos+":"+feat;
            AnalyzedToken token = new AnalyzedToken(wordform, posTag, lemma);
            readings.add(token);
        }

        return readings;
    }

    public static boolean Validate(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        Stream<String> lines = Files.lines(path);

        List<Integer> incorrectLines = new ArrayList<Integer>();
        List<Integer> incorrectTokenNumbers = new ArrayList<Integer>();
        List<Integer> incorrectDependencyNumbers = new ArrayList<Integer>();
        List<Integer> incorrectExpansionSlots = new ArrayList<Integer>();

        int lineNo = 1;
        int tokenCount = 0;
        List<Integer> heads = new ArrayList<Integer>();
        for( String line : (Iterable<String>) lines::iterator ) {
            // A line may either be blank or contain 10 tab separated values.
            if (line.equals("")) {

                // Validate that a token doesn't have a head
                // that is out of bounds of the sentence.
                for (Integer head : heads) {
                    if (head > tokenCount) {
                        incorrectDependencyNumbers.add(lineNo);
                    }
                }

                // If the head numbers are fine, clear the tokenCount and heads list.
                tokenCount = 0;
                heads.clear();

                // Increment line number and move on.
                lineNo++;
                continue;
            }

            // If it's not an empty line, then we must have a new token.
            tokenCount++;

            // Each line must have ten tab-separated values.
            String[] columns = line.split("\t");
            if (columns.length != 10) {
                incorrectLines.add(lineNo);
                lineNo++;

                // Don't continue validating this line if values are missing.
                continue;
            }

            // Validate Token Number is an integer.
            int tokenNumber = -1;
            try {
                tokenNumber = Integer.parseInt(columns[0]);

                // Also validate that the token number has the correct value.
                if (tokenNumber != tokenCount) {
                    incorrectTokenNumbers.add(lineNo);
                    lineNo++;

                    // Since we check the head value against the token number, skip
                    // processing the rest of the line if the token number is wrong.
                    continue;
                }
            }
            catch (NumberFormatException e) {
                incorrectTokenNumbers.add(lineNo);
                lineNo++;

                // Since we check the head value against the token number, skip
                // processing the rest of the line if the token number is wrong.
                continue;
            }

            // Validate Dependency Number
            int head = -1;
            try {
                head = Integer.parseInt(columns[6]);
                if (head == tokenNumber) {
                    incorrectDependencyNumbers.add(lineNo);
                    lineNo++;
                    continue;
                }
            }
            catch (NumberFormatException e) {
                incorrectDependencyNumbers.add(lineNo);
            }

            // Validate last two expansion slots.
            if (!"_".equals(columns[8]) || !"_".equals(columns[9])){
                incorrectExpansionSlots.add(lineNo);
            }

            lineNo++;
        }

        boolean isValid = incorrectLines.size() == 0
                && incorrectTokenNumbers.size() == 0
                && incorrectDependencyNumbers.size() == 0
                && incorrectExpansionSlots.size() == 0;

        return isValid;
    }
}
