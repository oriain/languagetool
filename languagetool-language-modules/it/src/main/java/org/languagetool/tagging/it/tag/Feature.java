package org.languagetool.tagging.it.tag;

/**
 * Created by littl on 5/21/2016.
 */
public class Feature {
    public enum Cli {
        CLI
    }

    public enum Clitics {
        cela,
        cele,
        celi,
        celo,
        cene,
        ci,
        gli,
        gliela,
        gliele,
        glieli,
        glielo,
        gliene,
        la,
        le,
        li,
        lo,
        mela,
        mele,
        meli,
        melo,
        mene,
        mi,
        ne,
        sela,
        sele,
        seli,
        selo,
        sene,
        si,
        tela,
        tele,
        teli,
        telo,
        tene,
        ti,
        vela,
        vele,
        veli,
        velo,
        vene,
        vi
    }

    public enum Degree {
        pos,    // Positive
        comp,   // Comparative
        sup     // Superlative
    }

    public enum Gender {
        // How do I deal with the different casing?  Just use toLowerCase? YES!
        F, f,   // Feminine
        M, m    // Masculine
    }

    public enum Mood {
        cond,   // Conditional
        ger,    // Gerundive
        impr,   // Imperative
        ind,    // Indicative
        inf,    // Infinitive
        part,   // Participle
        sub     // Subjunctive
    }

    public enum Number {
        S, s,      // Singular
        P, p       // Plural
    }

    public enum Person {
        // TODO: Not sure how to handle this case.
        First,  // 1
        Second, // 2
        Third   // 3
    }

    public enum Tense {
        pres,   // Present
        past,   // Past
        impf,   // Imperfect
        fut     // Future
    }
}
