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
public class WordTokenizedSentenceIterator implements Iterator<WordTokenizedSentence> {

    List<WordTokenizedDocument> documents;
    Iterator<WordTokenizedDocument> documentIterator;
    Iterator<WordTokenizedSentence> sentenceIterator;

    //it is assumed that the document list is not empy and every document has at least one sentence
    public WordTokenizedSentenceIterator(List<WordTokenizedDocument> documents) {
        this.documents = documents;
        documentIterator = documents.iterator();
        sentenceIterator = documentIterator.next().sentences.iterator();
    }

    @Override
    public boolean hasNext() {
        return sentenceIterator.hasNext() || documentIterator.hasNext();
    }

    @Override
    public WordTokenizedSentence next() {
        if (!sentenceIterator.hasNext()) {
            sentenceIterator = documentIterator.next().sentences.iterator();
        }
        return sentenceIterator.next();
    }

}
