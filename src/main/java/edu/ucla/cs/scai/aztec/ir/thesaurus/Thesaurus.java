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
import java.util.Set;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public interface Thesaurus {
    
    public boolean hasTerm(TermToken t);
    
    public int getLexiconSize();
    
    public TermToken getTerm(int i);    
    
    public double getWeight(TermToken t);
    
    public ArrayList<WeightedTermToken> getMostSimilarTerms(TermToken t, int k);
    
    public ArrayList<TermToken> getSynonyms(TermToken t);
    
    public ArrayList<TermToken> getHypernyms(TermToken t);
    
    public ArrayList<TermToken> getHyponyms(TermToken t);
    
    public void setEntry(TermToken t, int order, double weight, ArrayList<WeightedTermToken> similarTerms, ArrayList<TermToken> synonyms, ArrayList<TermToken> hypernyms, ArrayList<TermToken> hyponyms);
    
    public void removeEntry(TermToken t);
    
    public Set<TermToken> getPhrases();
    
}
