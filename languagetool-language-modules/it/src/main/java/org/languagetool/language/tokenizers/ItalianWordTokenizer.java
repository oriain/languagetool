package org.languagetool.language.tokenizers;

import org.languagetool.tokenizers.WordTokenizer;

import java.util.*;

/**
 * Created by littl on 11/6/2016.
 */
public class ItalianWordTokenizer extends WordTokenizer {
    private static List<String> multiCharDelims = Arrays.asList(
            "(^_^')",
            ":'-(",
            "@('.')@",
            "all'",
            "anch'",
            "assuefa'",
            "attivita'",
            "be'",
            "'beh",
            "bell'",
            "brav'",
            "c'",
            "cent'",
            "cinquant'",
            "citta'",
            "Citta'",
            "com'",
            "confa'",
            "cos'",
            "d'",
            "D'",
            "da'",
            "dall'",
            "dell'",
            "dev'",
            "di'",
            "diciott'",
            "disfa'",
            "dotto'",
            "dov'",
            "ebbe'",
            "embe'",
            "fa'",
            "'fanculo",
            "foss'",
            "gliel'",
            "grand'",
            "l'",
            "liberta'",
            "liquefa'",
            "m'",
            "malfa'",
            "mo'",
            "n'",
            "'ndrangheta",
            "'ndranghete",
            "'ndrina",
            "'ndrine",
            "neanch'",
            "nell'",
            "nessun'",
            "nient'",
            "novant'",
            "'oh",
            "ottant'",
            "po'",
            "pover'",
            "prim'",
            "putrefa'",
            "qualcos'",
            "qualcun'",
            "quand'",
            "quarant'",
            "quarantott'",
            "quell'",
            "quest'",
            "rarefa'",
            "rida'",
            "ridi'",
            "rifa'",
            "riva'",
            "s'",
            "sant'",
            "senz'",
            "sessant'",
            "settant'",
            "societa'",
            "soddisfa'",
            "sopraffa'",
            "sott'",
            "sottosta'",
            "sta'",
            "'sta",
            "'ste",
            "'sti",
            "'sto",
            "strafa'",
            "stupefa'",
            "sull'",
            "t'",
            "trent'",
            "un'",
            "v'",
            "va'",
            "vent'",
            "ventott'",
            "vu'"
            );
    private static HashSet<String> multiCharDelimsLowerCase;
    private static HashSet<String> getMultiCharDelimsLowerCase() {
        if (multiCharDelimsLowerCase == null) {
            multiCharDelimsLowerCase = new HashSet<String>();
            for (String word : multiCharDelims) {
                multiCharDelimsLowerCase.add(word.toLowerCase());
            }
        }
        return multiCharDelimsLowerCase;
    }

    @Override
    public List<String> tokenize(String text) {
        List<String> l = super.tokenize(text);
        List<String> combinedList = new ArrayList<String>();
        int i = 1;
        for (; i < l.size(); i++) {
            String prevToken = l.get(i-1);
            String currToken = l.get(i);
            String combinedToken = prevToken + currToken;
            if (getMultiCharDelimsLowerCase().contains(combinedToken.toLowerCase())) {
                combinedList.add(combinedToken);
                i++;
            }
            else {
                combinedList.add(prevToken);
            }
        }
        if (i == l.size()) combinedList.add(l.get(l.size()-1));
        return combinedList;
    }
}
