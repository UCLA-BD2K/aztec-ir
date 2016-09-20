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
package edu.ucla.cs.scai.aztec.ir.thesaurus.weightevaluator;

import edu.ucla.cs.scai.aztec.ir.tokenization.TermToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizedDocument;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizedSentence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class IDFevaluator implements TermWeightEvaluator {

    @Override
    public HashMap<TermToken, Double> evaluateTermWeights(List<TermTokenizedDocument> documents) {
        HashMap<TermToken, Double> idf = new HashMap<>();
        for (TermTokenizedDocument ttd : documents) {
            HashSet<TermToken> foundTerms = new HashSet<>();
            for (TermTokenizedSentence tts : ttd) {
                for (TermToken tt : tts) {
                    if (!foundTerms.contains(tt)) {
                        foundTerms.add(tt);
                        Double c = idf.get(tt);
                        if (tt == null) {
                            idf.put(tt, 1d);
                        } else {
                            idf.put(tt, c + 1);
                        }
                    }
                }
            }
        }
        Double N=1.0*documents.size();
        for (Map.Entry<TermToken, Double> e:idf.entrySet()) {
            e.setValue(Math.log(e.getValue()/N));
        }
        return idf;
    }

}
