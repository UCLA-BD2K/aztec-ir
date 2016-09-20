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
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class AztecThesaurus implements Thesaurus {

    HashMap<Integer, TermToken> enumeratedTerms=new HashMap<>();
    HashMap<TermToken, ThesaurusEntry> entries=new HashMap<>();
    Set<TermToken> phrases;

    @Override
    public boolean hasTerm(TermToken t) {
        return entries.containsKey(t);
    }

    @Override
    public double getWeight(TermToken t) {
        return entries.get(t).getWeight();
    }

    @Override
    public ArrayList<WeightedTermToken> getMostSimilarTerms(TermToken t, int k) {
        ArrayList<WeightedTermToken> fullList=entries.get(t).sortedSimilarTokens;
        if (fullList==null || fullList.isEmpty()) {
            return new ArrayList<>();
        }
        if (fullList.size()<k) {
            k=fullList.size();
        }
        return new ArrayList<>(fullList.subList(0, k));
    }

    @Override
    public ArrayList<TermToken> getSynonyms(TermToken t) {
        return entries.get(t).getSynonyms();
    }

    @Override
    public ArrayList<TermToken> getHypernyms(TermToken t) {
        return entries.get(t).getHypernyms();
    }

    @Override
    public ArrayList<TermToken> getHyponyms(TermToken t) {
        return entries.get(t).getHyponyms();
    }

    @Override
    public void setEntry(TermToken t, int order, double weight, ArrayList<WeightedTermToken> similarTerms, ArrayList<TermToken> synonyms, ArrayList<TermToken> hypernyms, ArrayList<TermToken> hyponyms) {
        enumeratedTerms.put(order, t);
        entries.put(t, new ThesaurusEntry(t, order, weight, similarTerms, synonyms, hypernyms, hyponyms));
        if (t.length()>1) {
            phrases.add(t);
        }
    }

    @Override
    public void removeEntry(TermToken t) {
        entries.remove(t);
    }

    @Override
    public Set<TermToken> getPhrases() {
        return phrases;
    }

    @Override
    public int getLexiconSize() {
        return enumeratedTerms.size();
    }

    @Override
    public TermToken getTerm(int i) {
        return enumeratedTerms.get(i);
    }

}
