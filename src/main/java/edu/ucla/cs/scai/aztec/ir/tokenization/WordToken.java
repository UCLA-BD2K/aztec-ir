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

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class WordToken implements Comparable<WordToken> {

    String word;
    String lemma;
    String pos;
    String synRepr;
    boolean lemmatized;
    boolean useLowerCase;

    static {

    }

    public WordToken(String word, String lemma, String pos) {
        this.word = word;
        this.lemma = lemma;
        this.pos = pos;
    }

    public String useLemma() {
        lemmatized = true;
        return lemma;
    }

    public void useLowerCase() {
        useLowerCase = true;
    }

    public String useWord() {
        lemmatized = false;
        return word;
    }

    @Override
    public String toString() {
        if (lemmatized) {
            if (useLowerCase) {
                return lemma.toLowerCase();// + "/" + pos.charAt(0);
            } else {
                return lemma;// + "/" + pos.charAt(0);
            }
        } else {
            if (useLowerCase) {
                return word.toLowerCase();// + "/" + pos;
            } else {
                return word;// + "/" + pos;
            }
        }
    }

    @Override
    public int compareTo(WordToken o) {
        return toString().compareTo(o.toString());
    }
}
