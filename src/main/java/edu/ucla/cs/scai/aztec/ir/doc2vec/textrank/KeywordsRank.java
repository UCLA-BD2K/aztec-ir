package edu.ucla.cs.scai.aztec.ir.doc2vec.textrank;

import edu.ucla.cs.scai.aztec.ir.tokenization.TermToken;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizedDocument;
import edu.ucla.cs.scai.aztec.ir.tokenization.TermTokenizedSentence;
import edu.ucla.cs.scai.aztec.ir.tokenization.WeightedTermToken;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.extjwnl.JWNLException;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class KeywordsRank {

    WeightedEdgeGraph g;
    HashMap<Integer, TermToken> keywords = new HashMap<>();
    HashMap<TermToken, Integer> keywordIds = new HashMap<>();
    double[] rank;
    Integer[] ordered;

    public KeywordsRank(TermTokenizedDocument document, int... windowSizes) throws JWNLException, FileNotFoundException {

        HashSet<TermToken> distinctTokens = new HashSet<>();
        LinkedList<TermToken> tokenList = new LinkedList<>();
        for (TermTokenizedSentence tts:document) {
            for (TermToken tt:tts) {
                distinctTokens.add(tt);
                tokenList.addLast(tt);
            }
        }

        int n = 0;
        for (TermToken tt : distinctTokens) {
            keywords.put(n, tt);
            keywordIds.put(tt, n);
            n++;
        }
        g = new WeightedEdgeGraph(n);

        for (int windowSize : windowSizes) {
            if (windowSize > tokenList.size()) {
                windowSize = tokenList.size();
            }
            LinkedList<Integer> window = new LinkedList<>();
            Iterator<TermToken> it = tokenList.iterator();
            //init window
            int i = 0;
            for (; i < windowSize; i++) {
                TermToken w = it.next();
                int idw = keywordIds.get(w);
                int j = 0;
                for (int idw2 : window) {
                    g.addWeight(idw, idw2, 1.0 / (i - j));
                    j++;
                }
                window.addLast(idw);
            }
            //advance window
            while (it.hasNext()) {
                TermToken w = it.next();
                int idw = keywordIds.get(w);
                window.removeFirst();
                int j = i - windowSize + 1; //i is increase from previous iteration
                for (int idw2 : window) {
                    g.addWeight(idw, idw2, 1.0 / (i - j));
                    g.addWeight(idw2, idw, 1.0 / (i - j));
                    j++;
                }                
                window.addLast(idw);
                i++;
            }
        }
        
        rank = g.computeNodeRank(0.85, 0.01);
        ordered = new Integer[n];
        for (int i = 0; i < n; i++) {
            ordered[i] = i;
        }
        Arrays.sort(ordered, new RankComparator(rank));
    }

//returns the keywords with the top-k rank
    public List<TermToken> topKeywords(Integer k) {
        if (k == null) {
            return topKeywords();
        }
        if (k > keywords.size()) {
            k = keywords.size();
        }
        LinkedList<TermToken> res = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            res.add(keywords.get(ordered[i]));
        }
        return res;
    }

    public List<TermToken> topKeywords() {
        LinkedList<TermToken> res = new LinkedList<>();
        double minRank = rank[ordered[0]] * 0.9;
        int i = 0;
        while (i < ordered.length && rank[ordered[i]] >= minRank) {
            res.add(keywords.get(ordered[i]));
            i++;
        }
        return res;
    }

    public List<WeightedTermToken> topRankedKeywords(Integer k) {
        if (k == null) {
            return topRankedKeywords();
        }
        if (k > keywords.size()) {
            k = keywords.size();
        }
        LinkedList<WeightedTermToken> res = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            res.add(new WeightedTermToken(keywords.get(ordered[i]), rank[ordered[i]]));
        }
        return res;

    }

    public List<WeightedTermToken> topRankedKeywords() {
        LinkedList<WeightedTermToken> res = new LinkedList<>();
        double minRank = rank[ordered[0]] * 0.9;
        int i = 0;
        while (i < ordered.length && rank[ordered[i]] >= minRank) {
            res.add(new WeightedTermToken(keywords.get(ordered[i]), rank[ordered[i]]));
            i++;
        }
        return res;

    }

    class RankComparator implements Comparator<Integer> {

        double[] rank;

        public RankComparator(double[] rank) {
            this.rank = rank;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return Double.compare(rank[o2], rank[o1]);
        }
    }
}
