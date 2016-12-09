/* LanguageTool, a natural language style checker 
 * Copyright (C) 2007 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.language;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.jetbrains.annotations.NotNull;
import org.languagetool.Language;
import org.languagetool.LanguageMaintainedState;
import org.languagetool.language.tokenizers.ItalianWordTokenizer;
import org.languagetool.languagemodel.LanguageModel;
import org.languagetool.languagemodel.LuceneLanguageModel;
import org.languagetool.parsers.DependencyParser;
import org.languagetool.parsers.DependencyParserException;
import org.languagetool.parsers.ItalianMaltParser;
import org.languagetool.parsers.MaltParser;
import org.languagetool.rules.*;
import org.languagetool.rules.it.*;

import org.languagetool.synthesis.ItalianSynthesizer;
import org.languagetool.synthesis.Synthesizer;
import org.languagetool.tagging.Tagger;
import org.languagetool.tagging.it.ItalianTagger;
import org.languagetool.tokenizers.SRXSentenceTokenizer;
import org.languagetool.tokenizers.SentenceTokenizer;

import org.languagetool.tagging.disambiguation.Disambiguator;
import org.languagetool.tagging.disambiguation.rules.it.ItalianRuleDisambiguator;
import org.languagetool.tokenizers.Tokenizer;
import org.languagetool.tokenizers.WordTokenizer;
import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.core.exception.MaltChainedException;

public class Italian extends Language implements AutoCloseable {

  private static final WordTokenizer WORD_TOKENIZER = new ItalianWordTokenizer();
  public Tokenizer getWordTokenizer() {
    return WORD_TOKENIZER;
  }

  private Tagger tagger;
  private SentenceTokenizer sentenceTokenizer;
  private LuceneLanguageModel languageModel;
  private Disambiguator disambiguator;
  private Synthesizer synthesizer;
  private DependencyParser parser;

  @Override
  public String getName() {
    return "Italian";
  }

  @Override
  public String getShortName() {
    return "it";
  }
  
  @Override
  public String[] getCountries() {
    return new String[]{"IT", "CH"};
  }

  @Override
  public Tagger getTagger() {
    if (tagger == null) {
      tagger = new ItalianTagger();
    }
    return tagger;
  }

  @Override
  public DependencyParser getDependencyParser() throws DependencyParserException {
    if (parser == null) {
      parser = new ItalianMaltParser("/it/italian.mco");
    }
    return parser;
  }

  @Override
  @NotNull
  public Synthesizer getSynthesizer() {
    if (synthesizer == null) {
      synthesizer = new ItalianSynthesizer();
    }
    return synthesizer;
  }

  @Override
  public SentenceTokenizer getSentenceTokenizer() {
    if (sentenceTokenizer == null) {
      sentenceTokenizer = new SRXSentenceTokenizer(this);
    }
    return sentenceTokenizer;
  }

  @Override
  public Contributor[] getMaintainers() {
    Contributor contributor = new Contributor("Paolo Bianchini");
    return new Contributor[] { contributor };
  }

  @Override
  public List<Rule> getRelevantRules(ResourceBundle messages) throws IOException {
    return Arrays.asList(
            new WhitespaceBeforePunctuationRule(messages),
            new CommaWhitespaceRule(messages),
            new DoublePunctuationRule(messages),
            new GenericUnpairedBracketsRule(messages,
                    Arrays.asList("[", "(", "{", "»", "«" /*"‘"*/),
                    Arrays.asList("]", ")", "}", "«", "»" /*"’"*/)),
            new MorfologikItalianSpellerRule(messages, this),
            new UppercaseSentenceStartRule(messages, this),
            new ItalianWordRepeatRule(messages, this),
            new MultipleWhitespaceRule(messages, this),
            new AgreementRule(messages),
            new VerbAgreementRule(messages)
    );
  }

  /** @since 3.1 */
  @Override
  public synchronized LanguageModel getLanguageModel(File indexDir) throws IOException {
    if (languageModel == null) {
      languageModel = new LuceneLanguageModel(new File(indexDir, getShortName()));
    }
    return languageModel;
  }

  /** @since 3.1 */
  @Override
  public List<Rule> getRelevantLanguageModelRules(ResourceBundle messages, LanguageModel languageModel) throws IOException {
    return Arrays.<Rule>asList(
            new ItalianConfusionProbabilityRule(messages, languageModel, this)
    );
  }

  /** @since 3.1 */
  @Override
  public void close() throws Exception {
    if (languageModel != null) {
      languageModel.close();
    }
  }

  @Override
  public final Disambiguator getDisambiguator() {
    if (disambiguator == null) {
      disambiguator = new ItalianRuleDisambiguator();
    }
    return disambiguator;
  }

  @Override
  public LanguageMaintainedState getMaintainedState() {
    return LanguageMaintainedState.ActivelyMaintained;
  }

}
