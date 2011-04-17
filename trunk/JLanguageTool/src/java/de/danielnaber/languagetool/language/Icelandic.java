/* LanguageTool, a natural language style checker 
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package de.danielnaber.languagetool.language;

/**
 * @author Anton Karl Ingason
 */

import java.util.*;

import de.danielnaber.languagetool.Language;
import de.danielnaber.languagetool.rules.*;
import de.danielnaber.languagetool.tagging.Tagger;
import de.danielnaber.languagetool.tagging.xx.DemoTagger;
import de.danielnaber.languagetool.tokenizers.SRXSentenceTokenizer;
import de.danielnaber.languagetool.tokenizers.SentenceTokenizer;

public class Icelandic extends Language {
	
	private final Tagger tagger = new DemoTagger();
	private final SentenceTokenizer sentenceTokenizer = new SRXSentenceTokenizer(getShortName());

  @Override
  public String getName() {
    return "Icelandic";
  }

  @Override
  public String getShortName() {
    return "is";
  }

	@Override
	public String[] getCountryVariants() {
	    return new String[]{"IS"};
	}

	@Override
	public Locale getLocale() {
	    return new Locale(getShortName());
	}

	@Override
	public Contributor[] getMaintainers() {
	     return new Contributor[] {new Contributor("Anton Karl Ingason")};
	}

	public Tagger getTagger() {
		    return tagger;
	}

	public SentenceTokenizer getSentenceTokenizer() {
    return sentenceTokenizer;
  }

  @Override
  public List<Class<? extends Rule>> getRelevantRules() {
    return Arrays.asList(
            CommaWhitespaceRule.class,
            DoublePunctuationRule.class,
            GenericUnpairedBracketsRule.class,
            UppercaseSentenceStartRule.class,
            WordRepeatRule.class,
            WhitespaceRule.class
    );
  }

}
