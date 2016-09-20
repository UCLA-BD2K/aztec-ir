/*
 * Copyright 2016 ScAi, CSD, UCLA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.ucla.cs.scai.aztec.ir.thesaurus;

import edu.ucla.cs.scai.aztec.ir.thesaurus.phrasemining.PhraseExtractor;
import edu.ucla.cs.scai.aztec.ir.thesaurus.similaritybuilder.TermSimilarityBuilder;
import edu.ucla.cs.scai.aztec.ir.thesaurus.weightevaluator.TermWeightEvaluator;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizedDocument;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizedSentence;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizer;
import edu.ucla.cs.scai.aztec.ir.tokenization.WeightedTermToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizedDocument;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizedDocumentsToSentences;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public abstract class ThesaurusBuilder {

    List<String> fileNames;
    PhraseExtractor phraseExtractor;
    TermSimilarityBuilder similarityBuilder;
    TermWeightEvaluator weightEvaluator;

    WordTokenizer wordTokenizer;
    TermTokenizer termTokenizer;
    ArrayList<WordTokenizedDocument> wordTokenizedDocuments;
    ArrayList<TermTokenizedDocument> termTokenizedDocuments;
    HashMap<TermToken, Integer> phrasesCount;
    Set<TermToken> phrases;
    HashMap<Integer, TermToken> lexicon;

    public ThesaurusBuilder(List<String> fileNames, PhraseExtractor phraseExtractor, TermSimilarityBuilder similarityBuilder, TermWeightEvaluator weightEvaluator) {
        this.fileNames = fileNames;
        this.phraseExtractor = phraseExtractor;
        this.similarityBuilder = similarityBuilder;
        this.weightEvaluator = weightEvaluator;
        wordTokenizer = new WordTokenizer();
        termTokenizer = new TermTokenizer();
    }

    private void wordTokenizeCorpus1() {
        int i = 0;
        for (String fileName : fileNames) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                String l;
                while ((l = in.readLine()) != null) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(l);
                }
            } catch (Exception e) {
                System.out.println("Error with file " + fileName);
                e.printStackTrace();
                continue;
            }
            WordTokenizedDocument wtd = new WordTokenizer().tokenize(sb.toString(), true, false, true);
            wordTokenizedDocuments.add(wtd);
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + " documements tokenized");
            }
        }
        System.out.println(i + " documements tokenized");
    }

    private void termTokenizeDocuments() {
        termTokenizedDocuments = new ArrayList<>();
        for (WordTokenizedDocument wtd : wordTokenizedDocuments) {
            TermTokenizedDocument ttd = termTokenizer.tokenize(wtd, phrases, true);
            termTokenizedDocuments.add(ttd);
        }
    }

    void extractPhrases() {
        WordTokenizedDocumentsToSentences doc2wts = new WordTokenizedDocumentsToSentences(wordTokenizedDocuments);
        phrasesCount = phraseExtractor.extractPhrases(doc2wts);
        phrases = phrasesCount.keySet();
    }

    //receives an empty thesaurus
    public void build(Thesaurus thesaurus) {
        wordTokenizeCorpus1();
        extractPhrases();
        termTokenizeDocuments();
        similarityBuilder.initialize(termTokenizedDocuments);
        HashMap<TermToken, Double> termWeights = weightEvaluator.evaluateTermWeights(termTokenizedDocuments);
        ArrayList<TermToken> allTokens=new ArrayList(termWeights.values());
        Collections.sort(allTokens);
        int i=1;
        for (TermToken tt:allTokens) {
            lexicon.put(i, tt);
            i++;
        }
        for (i=1; i<lexicon.size(); i++) {
            TermToken tt=lexicon.get(i);
            Double weight=termWeights.get(tt);
            ArrayList<WeightedTermToken> similarTerms=similarityBuilder.getSimilarTerms(tt);
            ArrayList<TermToken> synonyms=similarityBuilder.getSynonyms(tt);
            ArrayList<TermToken> hypernyms=similarityBuilder.getSynonyms(tt);
            ArrayList<TermToken> hyponyms=similarityBuilder.getSynonyms(tt);
            thesaurus.setEntry(tt, i, weight, similarTerms, synonyms, hypernyms, hyponyms);
        }
    }

}
