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
package edu.ucla.cs.scai.aztec.ir.thesaurus.phrasemining;

import edu.ucla.cs.scai.aztec.ir.tokenization.TermToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizer;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizedDocument;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizedSentence;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChiSquarePhraseExtractor implements PhraseExtractor {

    Double relativeSupport;
    Integer absoluteSupport;
    double criticalValue;

    private static final int MAX_LENGTH = 5;

    public ChiSquarePhraseExtractor(double relativeSupport, double criticalValue) {
        this.relativeSupport = relativeSupport;
        this.criticalValue = criticalValue;
    }

    public ChiSquarePhraseExtractor(int absoluteSupport, double criticalValue) {
        this.absoluteSupport = absoluteSupport;
        this.criticalValue = criticalValue;
    }

    private int count(HashMap<TermToken, Integer> map, TermToken key) { //utility method returning 0 when the key is not in the map
        Integer c = map.get(key);
        if (c == null) {
            return 0;
        }
        return c;
    }

    private int increase(HashMap<TermToken, Integer> map, TermToken key) { //utility method increasing by one the value in a map, or setting it to one if the key is missing
        Integer c = map.get(key);
        if (c == null) {
            map.put(key, 1);
            return 1;
        } else {
            map.put(key, c + 1);
            return c + 1;
        }
    }

    @Override
    public HashMap<TermToken, Integer> extractPhrases(Iterable<WordTokenizedSentence> sentences) {

        //first, find frequent uni-grams - frequent uni-grams are necessary because frequent phrases must consist of frequent uni-grams
        int totalNumberOfUnigragms = 0;
        HashMap<TermToken, Integer> allUnigramCount = new HashMap<>(); //the count for each uni-gram/ token        
        int totalNumberOfSentences = 0;
        //numberOfTokensInShortSentences[i] is the number of tokens in senteces with not more than i tokens
        //numberOfShortSentences[i] is the number of senteces with not more than i tokens
        int[] numberOfShortSentences = new int[MAX_LENGTH + 1];
        int[] numberOfTokensInShortSentences = new int[MAX_LENGTH + 1];
        for (WordTokenizedSentence s : sentences) {
            totalNumberOfSentences++;
            if (s.length() < numberOfShortSentences.length) {
                numberOfShortSentences[s.length()]++;
                numberOfTokensInShortSentences[s.length()] += s.length();
            }
            if (s.length() == 0) {
                continue;
            }
            totalNumberOfUnigragms += s.length();
            for (WordToken t : s) {
                String w = ((WordToken) t).toString();
                if (w.contains("/D") || w.contains("/C") || w.contains("/I") || w.contains("/-") || w.contains("/.") || w.contains("/,") || w.contains("/T") || w.contains("/P") || w.equals("be/V") || w.contains("%/N") || w.contains("/W") || w.contains("/M") || w.contains("/:") || w.contains("/V") || w.contains("/'")) {
                    continue;
                }
                increase(allUnigramCount, new TermToken(t));

            }
        }

        for (int i = 1; i < numberOfShortSentences.length; i++) { //compute the cumulative count
            numberOfShortSentences[i] += numberOfShortSentences[i - 1];
            numberOfTokensInShortSentences[i] += numberOfTokensInShortSentences[i - 1];
        }

        int minSupport = absoluteSupport != null ? absoluteSupport : (int) Math.ceil(relativeSupport * totalNumberOfUnigragms);
        HashMap<TermToken, Integer> frequentUnigramCount = new HashMap<>(); //the count for each frequent uni-gram/ token        
        for (Map.Entry<TermToken, Integer> e : allUnigramCount.entrySet()) {
            if (e.getValue() >= minSupport) {
                frequentUnigramCount.put(e.getKey(), e.getValue());
            }
        }
        allUnigramCount.clear();
        HashMap<TermToken, Integer> frequentNgramCount = frequentUnigramCount; //the count of n-grams that are frequent - these are the only n-grams that we try to extend for creating (n+1)-grams

        HashMap<TermToken, Integer> result = new HashMap<>();
        int n = 1;

        HashMap<TermToken, Integer> unigramsAtBeginningCount = new HashMap<>(); //the number of times each uni-gram is the first n positions (n increases during the execution of the algorithm)        

        while (!frequentNgramCount.isEmpty() && n < MAX_LENGTH) { //try to extend each of the frequent n-gram with the frequent unigrams
            int totalNplusOneGrams = totalNumberOfUnigragms - numberOfTokensInShortSentences[n] - n * (totalNumberOfSentences - numberOfShortSentences[n]);
            HashMap<TermToken, Integer> nGramsAtEndCount = new HashMap<>(); //number of times a frequent n-gram appers at the end of the sentence
            //System.out.println("Trying to extend the frequent " + n + "-grams");
            HashMap<TermToken, Integer> nPlusOneGramsCount = new HashMap<>();
            for (WordTokenizedSentence sentence : sentences) {
                if (sentence.length() <= n) {
                    continue; //the sentence does not contain (n+1)-grams
                }
                Iterator<WordToken> it = sentence.iterator();
                TermToken window = new TermToken();
                while (it.hasNext() && window.length() < n) {
                    window = new TermToken(window, it.next());
                }
                //now, windows contains a n-gram
                if (frequentUnigramCount.containsKey(window.getSuffix(1))) {
                    increase(unigramsAtBeginningCount, window.getSuffix(1));
                }

                while (it.hasNext()) {
                    WordToken next = it.next();
                    if (frequentNgramCount.containsKey(window) && frequentUnigramCount.containsKey(new TermToken(next))) {
                        TermToken nPlusOneGram = new TermToken(window, next);
                        if (nPlusOneGram.toString().contains("/N")) {
                            increase(nPlusOneGramsCount, nPlusOneGram);
                        }
                    }
                    window.leftShift(next);
                }
                if (frequentNgramCount.containsKey(window)) {
                    increase(nGramsAtEndCount, window);
                }
            }

            HashMap<TermToken, Integer> frequentNPlusGramCount = new HashMap<>();
            for (Map.Entry<TermToken, Integer> e : nPlusOneGramsCount.entrySet()) {
                if (e.getValue() >= minSupport) {
                    frequentNPlusGramCount.put(e.getKey(), e.getValue());
                }
            }

            //now, for every frequent (n+1)-gram, apply the Chi-square test
            for (TermToken nPlusOneGram : frequentNPlusGramCount.keySet()) {
                TermToken head = nPlusOneGram.getPrefix(n);
                TermToken extension = nPlusOneGram.getSuffix(1);

                int cW1W2 = frequentNPlusGramCount.get(nPlusOneGram);

                int cW1 = frequentNgramCount.get(head) - count(nGramsAtEndCount, head); //c(w1, *): n-gram w1 followed by any token
                int cW2 = frequentUnigramCount.get(extension) - count(unigramsAtBeginningCount, extension); //c(*, w2): 1-gram w2 preceeded by any n-gram

                int cnW1 = totalNplusOneGrams - cW1; //c(!w1, *): any n-gram different from w1 followed by any token                    
                int cnW2 = totalNplusOneGrams - cW2; //c(*, !w2): 1-gram differenft form w2 preceeded by any n-gram

                int cW1nW2 = cW1 - cW1W2;
                int cnW1W2 = cW2 - cW1W2;

                int cnW1nW2 = totalNplusOneGrams - cW1 - cW2 + cW1W2;

                //the chi-square test, in the case of 2-by-2 tables, has a simple formula:
                //N * (main diagonal - secondary diagona)^2 / product of marginals
                double chiSquare = 1.0 * totalNplusOneGrams * Math.pow((1.0 * cW1W2 * cnW1nW2) - (1.0 * cW1nW2 * cnW1W2), 2) / (1.0 * cW1 * cW2 * cnW1 * cnW2);
                if (chiSquare >= 3.841) { //a phrase has been found
                    //System.out.println(nPlusOneGram+" Chi^2: "+chiSquare+" Freq: "+frequentNPlusGramCount.get(nPlusOneGram));
                    String[] p = nPlusOneGram.toString().split(" ");
                    String[] t = p[0].split("/");
                    System.out.print(t[0]);
                    for (int i = 1; i < p.length; i++) {
                        t = p[i].split("/");
                        System.out.print("_"+t[0]);
                    }
                    System.out.println();
                }
            }
            n++;
            frequentNgramCount = frequentNPlusGramCount;

        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<WordTokenizedSentence> sentences;
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream("/home/massimo/Downloads/sentences.data"))) {
            sentences = (ArrayList<WordTokenizedSentence>) oin.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            try (BufferedReader in = new BufferedReader(new FileReader("/home/massimo/Downloads/abstract_removeurl.txt"))) {
                sentences = new ArrayList<>();
                String l;
                int i = 0;
                while ((l = in.readLine()) != null) {
                    WordTokenizedDocument td = new WordTokenizer().tokenize(l, true, false, true);
                    sentences.addAll(td.getSentences());
                    i++;
                    if (i % 1000 == 0) {
                        System.out.println(i + " documements processed - " + sentences.size() + " sentences tokenized");
                    }
                }
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("/home/massimo/Downloads/sentences.data"))) {
                    out.writeObject(sentences);
                }
            }
        }
        new ChiSquarePhraseExtractor(20, 0.5).extractPhrases(sentences);

    }
}
