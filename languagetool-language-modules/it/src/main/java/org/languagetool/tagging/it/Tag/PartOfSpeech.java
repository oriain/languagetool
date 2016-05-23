package org.languagetool.tagging.it.tag;

/**
 * Created by littl on 5/21/2016.
 */
public enum PartOfSpeech {
    ABL,            // Abbreviated locutions, such as "a.C.", "ecc." and "i.e."
    ADJ,            // Adjectives
    ADV,            // Adverbs
    ART,            // Articles
    ARTPRE,         // Preposition+article compounds ("col", "della", "nei"...)
    ASP,            // Aspectuals ("stare" in "stare per")
    AUX,            // Auxiliaries ("essere", "avere", "venire")
    CAU,            // Causatives ("fare" in "far sapere")
    CE,             // Clitic "ce" as in "ce l'ho fatta".
    CI,             // Clitic "ci" as in "ci prova".
    CON,            // Conjunctions
    // TODO: Do I need to handle subcategories?
    DET_DEMO,       // Demonstrative determiners (such as "questa" in "questa sera")
    DET_INDEF,      // Indefinite determiners (such as "molti" in "molti amici")
    DET_NUM_CARD,   // Cardinal number determiners (e.g., "cinque" in "cinque amici")
    DET_POSS,       // Possessive determiners (e.g., "mio", "suo")
    DET_WH,         // Wh determiners (e.g., quale in "quale amico")
    INT,            // Interjections
    MOD,            // Modal verbs (e.g. "dover" in "dover ricostruire")
    NE,             // Clitic "ne" (as in: "ne hanno molte")
    NOUN,           // Nouns
    PON,            // Non-sentential punctuation marks (e.g. , " $)
    PRE,            // Prepositions
    PRO_DEMO,       // Demonstrative pronouns (e.g. "questa" in "voglio questa")
    PRO_INDEF,      // Indefinite pronouns (e.g., "molti" in "vengono molti")
    PRO_NUM,        // Numeral pronouns (e.g., "cinque" in "cinque sono sopravvissuti")
    PRO_PERS,       // Personal pronouns, such as "lui" and "loro"
    PRO_POSS,       // Possessive pronouns, such as "loro" in "non era uno dei loro")
    PRO_WH,         // Wh-pronouns, such as "quale" in "quale e' venuto?"
    SENT,           // End of sentence marker (! . ... : ?)
    SI,             // Clitic "si" as in "di cui si discute"
    TALE,           // "Tale" in constructions such as "una fortuna tale che...", "la tal cosa", "tali amici"
    VER,            // Verbs
    WH,             // Wh elements ("come", "qualora", "quando"...)
    WH_CHE          // "Che" as a wh element (e.g., "l'uomo che hai visto", "hai detto che")
}
