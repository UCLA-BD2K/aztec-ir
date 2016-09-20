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
import java.util.List;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class TermToken implements Comparable<TermToken> {

    LinkedList<WordToken> tokenList;

    public TermToken() {
        tokenList = new LinkedList<>();
    }

    public TermToken(WordToken... ts) {
        tokenList = new LinkedList<>();
        for (WordToken t : ts) {
            tokenList.add(t);
        }
    }
    
    public TermToken(TermToken... ts) {
        tokenList = new LinkedList<>();
        for (TermToken t : ts) {
            tokenList.addAll(t.tokenList);
        }
    }
    
    public TermToken(TermToken tt, WordToken wt) {
        tokenList = new LinkedList<>();
        tokenList.addAll(tt.tokenList);
        tokenList.add(wt);
    }    

    public TermToken(List tl) {
        tokenList = new LinkedList<>();
        for (Object t : tl) {
            if (t instanceof TermToken) {
                tokenList.addAll(((TermToken)t).tokenList);
            } else if (t instanceof WordToken) {
                tokenList.add((WordToken)t);
            } else {
                throw new RuntimeException(t.getClass() + " is not a valid type for creating a term token");
            }
        }
    }    

    public TermToken getPrefix(int length) {
        TermToken res = new TermToken();
        int i = 0;
        for (WordToken t : tokenList) {
            if (i < length) {
                res.tokenList.add(t);
                i++;
            } else {
                break;
            }
        }
        return res;
    }

    public TermToken getSuffix(int length) {
        int skip = tokenList.size() - length - 1;
        TermToken res = new TermToken();
        int i = 0;
        for (WordToken t : tokenList) {
            if (i > skip) {
                res.tokenList.add(t);
            }
            i++;
        }
        return res;
    }

    public WordToken leftShift(WordToken t) { //remove the first token and append t        
        tokenList.addLast(t);
        return tokenList.removeFirst();
    }

    public WordToken rightShift(WordToken t) { //remove the last token and prepend t        
        tokenList.addFirst(t);
        return tokenList.removeLast();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (WordToken t : tokenList) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(t.toString());
        }
        return sb.toString();
    }

    public int length() {
        return tokenList.size();
    }

    @Override
    public int compareTo(TermToken o) {
        Iterator<WordToken> it1=tokenList.iterator();
        Iterator<WordToken> it2=o.tokenList.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            WordToken w1=it1.next();
            WordToken w2=it2.next();
            int c=w1.compareTo(w2);
            if (c!=0) {
                return c;
            }
        }
        if (it1.hasNext()) {
            return 1;            
        }
        if (it2.hasNext()) {
            return -1;
        }
        return 0;
    }

}
