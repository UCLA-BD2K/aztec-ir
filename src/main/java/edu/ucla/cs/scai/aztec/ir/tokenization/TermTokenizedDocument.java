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
import java.util.LinkedList;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class TermTokenizedDocument implements Iterable<TermTokenizedSentence> {

    LinkedList<TermTokenizedSentence> sentences;

    public TermTokenizedDocument() {
        sentences = new LinkedList<>();
    }

    public LinkedList<TermTokenizedSentence> getSentences() {
        return sentences;
    }

    @Override
    public Iterator<TermTokenizedSentence> iterator() {
        return sentences.iterator();
    }

    public void appendSentence(TermTokenizedSentence s) {
        sentences.addLast(s);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TermTokenizedSentence tts : sentences) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(tts.toString());
        }
        return sb.toString();
    }
}
