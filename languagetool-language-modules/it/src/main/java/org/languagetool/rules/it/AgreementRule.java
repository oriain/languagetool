package org.languagetool.rules.it;

import org.languagetool.AnalyzedSentence;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.tagging.it.CoNLL;
import org.languagetool.tagging.it.ItalianSentence;
import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.core.exception.MaltChainedException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by littl on 5/21/2016.
 */
class AgreementRule extends Rule {

    @Override
    public String getId() {
        return "IT_AGREEMENT";
    }

    @Override
    public String getDescription() {
        return "INSERT AN ITALiAN RULE DESCRIPTION HERE.";
    }

    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) throws IOException {
        // Convert the analyzed sentence to an Italian sentence. Italian
        // sentences have Italian tokens, which have Italian specific features.
        ItalianSentence italianSentence = ItalianSentence.create(sentence);
        String[] tokens = italianSentence.toCoNLL();

        // Load the model, initialize the parser, and parse the tokens.
        String filename = "/it/italian.mco";
        URL model = JLanguageTool.getDataBroker().getFromResourceDirAsUrl(filename);
        //URL model = new File(filename).toURI().toURL();
        ConcurrentMaltParserModel parser;
        ConcurrentDependencyGraph graph = null;
        try {
            parser = ConcurrentMaltParserService.initializeParserModel(model);
            graph = parser.parse(tokens);
        } catch (MaltChainedException e) {
            e.printStackTrace();
        }
        assert graph != null : "Could not parse the sentence tokens.";

        italianSentence.setDependencyGraph(graph);
        boolean hasValidAgreement = italianSentence.checkAgreement();
        if (hasValidAgreement) {
            System.out.println("No agreement violations in the sentence were detected.");
        } else {
            System.out.println("Agreement violations in the sentence were detected.");
        }

        // Write the tokens to a file for viewing in MatlEval.
        System.out.println("Outputting tokens for viewing.");
        CoNLL.writeFile(graph, "tokens.conl");

        System.out.println("Rule matches are not yet being returned.");
        return new RuleMatch[0];
    }

    @Override
    public void reset() {

    }
}
