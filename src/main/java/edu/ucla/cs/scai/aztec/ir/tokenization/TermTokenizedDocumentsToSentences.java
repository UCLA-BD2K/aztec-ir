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
package edu.ucla.cs.scai.aztec.ir.tokenization;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class TermTokenizedDocumentsToSentences implements Iterable<TermTokenizedSentence> {
    
    List<TermTokenizedDocument> documents;
    
    public TermTokenizedDocumentsToSentences(List<TermTokenizedDocument> documents) {
        this.documents=documents;
    }

    @Override
    public Iterator<TermTokenizedSentence> iterator() {
        return new TermTokenizedSentenceIterator(documents);
    }
    
}