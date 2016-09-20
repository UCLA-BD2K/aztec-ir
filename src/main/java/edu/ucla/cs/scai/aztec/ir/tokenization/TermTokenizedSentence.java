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
public class TermTokenizedSentence implements Iterable<TermToken>, Serializable {

    LinkedList<TermToken> tokens = new LinkedList<>();

    @Override
    public Iterator<TermToken> iterator() {
        return tokens.iterator();
    }

    public void appendToken(TermToken t) {
        tokens.addLast(t);
    }

    public LinkedList<TermToken> getFirst(int n) {
        LinkedList<TermToken> res = new LinkedList<>();
        Iterator<TermToken> it = tokens.iterator();
        for (int i = 0; i < n && it.hasNext(); i++) {
            res.addLast(it.next());
        }
        return res;
    }

    public LinkedList<TermToken> getLast(int n) {
        LinkedList<TermToken> res = new LinkedList<>();
        ListIterator<TermToken> it = tokens.listIterator(tokens.size());
        for (int i = 0; i < n && it.hasPrevious(); i++) {
            res.addFirst(it.next());
        }
        return res;
    }

    public int length() {
        return tokens.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for (TermToken tt:tokens) {
            if (sb.length()>0) {
                sb.append(" ");
            }
            sb.append(tt.toString());
        }
        return sb.toString();
    }

}
