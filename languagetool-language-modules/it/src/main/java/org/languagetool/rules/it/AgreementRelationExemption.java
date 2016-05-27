package org.languagetool.rules.it;

import org.languagetool.tagging.it.ItalianToken;

/**
 * Created by littl on 5/27/2016.
 */
interface AgreementRelationExemption {
    boolean checkForExemption(ItalianToken child);
}
