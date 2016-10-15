package org.languagetool.tagging.it.tag;

import org.languagetool.tagging.it.ItalianToken;

/**
 * Created by littl on 5/24/2016.
 */
public enum DependencyRelation {
    // *
    // +
    // %
    // /
    APPOSITION,
    ARG,
    ARG_LOCUT,
    AUX,
    AUX_PASSIVE,
    AUX_PROGRESSIVE,
    AUX_TENSE,
    CLOSE_PARENTHETICAL,
    CLOSE_QUOTES,
    CONTIN,
    CONTIN_DENOM,
    CONTIN_LOCUT,
    CONTIN_NUM,
    CONTIN_PREP,
    COORD,
    COORD_ADVERS,
    COORD_BASE,
    COORD_COMPAR,
    COORD_CONTIN,
    COORD_CORRELAT,
    COORD_ESPLIC,
    COORD_NEG,
    COORD_RANGE,
    COORD_SYMMETRIC,
    COORD2ND,
    COORD2ND_ADVERS,
    COORD2ND_BASE,
    COORD2ND_COMPAR,
    COORD2ND_CORRELAT,
    COORD2ND_ESPLIC,
    COORD2ND_NEG,
    COORD2ND_RANGE,
    COORD2ND_SYMMETRIC,
    COORDANTEC_COMPAR,
    COORDANTEC_CORRELAT,
    DEPENDENT,
    EMPTYCOMPL,
    END,
    EXTRAOBJ,
    EXTRASUBJ,
    INDCOMPL,
    INDCOMPL_LOCUT,
    INDOBJ,
    INTERJECTION,
    OBJ,
    OBJ_VERB,
    OBJ_LOCUT,
    OBJ_SUBJ,
    OBJ_YN,
    OPEN_PARENTHETICAL,
    OPEN_QUOTES,
    PHRAS,
    PREDCOMPL,
    PREDCOMPL_OBJ,
    PREDCOMPL_OBJ_LOCUT,
    PREDCOMPL_OBJ_PREDCOMPL_SUBJ,
    PREDCOMPL_SUBJ,
    PREDCOMPL_SUBJ_CLEFT,
    RMOD,
    RMOD_LOCUT,
    RMOD_RELCL,
    RMOD_RELCL_REDUC,
    RMODPRED_LOCUT,
    RMODPRED_OBJ,
    RMODPRED_SUBJ,
    SEPARATOR,
    SUBJ,
    SUBJ_LOCUT,
    SUBJ_INDCOMPL,
    SUBJ_INDOBJ,
    SUBJ_OBJ,
    SUBJ_SUBJ_IMPERS,
    TOP,
    TOPGAP,
    VISITOR,
    VISITOR_ROBJ;

    // Checks to see if the token has an auxiliary relationship (with a parent token).
    public static boolean isAuxiliary(ItalianToken token) {
        if (token.dependencyRelation == DependencyRelation.AUX) return true;
        if (token.dependencyRelation == DependencyRelation.AUX_PASSIVE) return true;
        if (token.dependencyRelation == DependencyRelation.AUX_PROGRESSIVE) return true;
        if (token.dependencyRelation == DependencyRelation.AUX_TENSE) return true;
        return false;
    }
}
