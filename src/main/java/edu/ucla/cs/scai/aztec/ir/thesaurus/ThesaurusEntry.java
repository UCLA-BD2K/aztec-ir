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

import edu.ucla.cs.scai.aztec.ir.tokenization.TermToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.WeightedTermToken;
import java.util.ArrayList;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class ThesaurusEntry {

    TermToken term;
    double weight;
    ArrayList<WeightedTermToken> sortedSimilarTokens;
    ArrayList<TermToken> synonyms;
    ArrayList<TermToken> hypernyms;
    ArrayList<TermToken> hyponyms;
    int order;

    public ThesaurusEntry(TermToken term, int order, double weight, ArrayList<WeightedTermToken> sortedSimilarTokens, ArrayList<TermToken> synonyms, ArrayList<TermToken> hypernyms, ArrayList<TermToken> hyponyms) {
        this.term = term;
        this.order = order;
        this.weight = weight;
        this.sortedSimilarTokens = sortedSimilarTokens;
        this.synonyms = synonyms;
        this.hypernyms = hypernyms;
        this.hyponyms = hyponyms;
    }

    public TermToken getTerm() {
        return term;
    }

    public void setTerm(TermToken term) {
        this.term = term;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ArrayList<WeightedTermToken> getSortedSimilarTokens() {
        return sortedSimilarTokens;
    }

    public void setSortedSimilarTokens(ArrayList<WeightedTermToken> sortedSimilarTokens) {
        this.sortedSimilarTokens = sortedSimilarTokens;
    }

    public ArrayList<TermToken> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<TermToken> synonyms) {
        this.synonyms = synonyms;
    }

    public ArrayList<TermToken> getHypernyms() {
        return hypernyms;
    }

    public void setHypernyms(ArrayList<TermToken> hypernyms) {
        this.hypernyms = hypernyms;
    }

    public ArrayList<TermToken> getHyponyms() {
        return hyponyms;
    }

    public void setHyponyms(ArrayList<TermToken> hyponyms) {
        this.hyponyms = hyponyms;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
