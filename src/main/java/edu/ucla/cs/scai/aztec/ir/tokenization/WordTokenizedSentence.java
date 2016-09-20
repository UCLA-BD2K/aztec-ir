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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class WordTokenizedSentence implements Iterable<WordToken>, Serializable {

    LinkedList<WordToken> tokens = new LinkedList<>();

    @Override
    public Iterator<WordToken> iterator() {
        return tokens.iterator();
    }

    public void appendToken(WordToken t) {
        tokens.addLast(t);
    }

    public LinkedList<WordToken> getFirst(int n) {
        LinkedList<WordToken> res = new LinkedList<>();
        Iterator<WordToken> it = tokens.iterator();
        for (int i = 0; i < n && it.hasNext(); i++) {
            res.addLast(it.next());
        }
        return res;
    }

    public LinkedList<WordToken> getLast(int n) {
        LinkedList<WordToken> res = new LinkedList<>();
        ListIterator<WordToken> it = tokens.listIterator(tokens.size());
        for (int i = 0; i < n && it.hasPrevious(); i++) {
            res.addFirst(it.next());
        }
        return res;
    }

    public int length() {
        return tokens.size();
    }

}
