package edu.ucla.cs.scai.aztec.ir.doc2vec.wordrank;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.ucla.cs.scai.aztec.ir.tokenization.WordTokenizer;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import net.sf.extjwnl.JWNLException;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class KeywordsRankSyntacticalDependencies {

    WeightedEdgeGraph g;
    HashMap<Integer, String> keywords = new HashMap<>();
    HashMap<String, Integer> keywordIds = new HashMap<>();
    double[] rank;
    Integer[] ordered;

    public KeywordsRankSyntacticalDependencies(String text, int... windowSizes) throws JWNLException, FileNotFoundException {

        Properties propsTokens = new Properties();
        propsTokens.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner, parse, dcoref");
        StanfordCoreNLP pipelineTokens = new StanfordCoreNLP(propsTokens);
        Annotation qaTokens = new Annotation(text);
        pipelineTokens.annotate(qaTokens);
        List<CoreMap> sentences = qaTokens.get(SentencesAnnotation.class);

        HashSet<String> distinctTokens = new HashSet<>();
        LinkedList<String> listTokens = new LinkedList<>();
        for (CoreMap sentence : sentences) {
            for (CoreLabel cl : (ArrayList<CoreLabel>) sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if (!WordTokenizer.isStopWord(cl.lemma())) {
                    distinctTokens.add(cl.lemma());
                    listTokens.add(cl.lemma());
                }
            }
        }
        System.out.println("Distinct lemmas: "+distinctTokens);

        int n = 0;
        for (String t : distinctTokens) {
            keywords.put(n, t);
            keywordIds.put(t, n);
            n++;
        }
        g = new WeightedEdgeGraph(n);

        for (CoreMap sentence : sentences) {
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
            LinkedList<IndexedWord> queue = new LinkedList<>(dependencies.getRoots());
            while (!queue.isEmpty()) {
                IndexedWord w1 = queue.removeFirst();
                Integer idw1 = keywordIds.get(w1.lemma());
                for (IndexedWord w2 : dependencies.getChildList(w1)) {
                    queue.addLast(w2);
                    if (idw1 != null) {
                        Integer idw2 = keywordIds.get(w2.lemma());
                        if (idw2 != null) {
                            g.addWeight(idw1, idw2, 1);
                        }
                    }
                }
            }
        }
        for (int windowSize : windowSizes) {
            if (windowSize > listTokens.size()) {
                windowSize = listTokens.size();
            }
            LinkedList<Integer> window = new LinkedList<>();
            Iterator<String> it = listTokens.iterator();
            //init window
            int i = 0;
            for (; i < windowSize; i++) {
                String w = it.next();
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
                String w = it.next();
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
    public List<String> topKeywords(Integer k) {
        if (k == null) {
            return topKeywords();
        }
        if (k > keywords.size()) {
            k = keywords.size();
        }
        LinkedList<String> res = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            res.add(keywords.get(ordered[i]));
        }
        return res;
    }

    public List<String> topKeywords() {
        LinkedList<String> res = new LinkedList<>();
        double minRank = rank[ordered[0]] * 0.9;
        int i = 0;
        while (i < ordered.length && rank[ordered[i]] >= minRank) {
            res.add(keywords.get(ordered[i]));
            i++;
        }
        return res;
    }

    public List<RankedString> topRankedKeywords(Integer k) {
        if (k == null) {
            return topRankedKeywords();
        }
        if (k > keywords.size()) {
            k = keywords.size();
        }
        LinkedList<RankedString> res = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            res.add(new RankedString(keywords.get(ordered[i]), rank[ordered[i]]));
        }
        return res;

    }

    public List<RankedString> topRankedKeywords() {
        LinkedList<RankedString> res = new LinkedList<>();
        double minRank = rank[ordered[0]] * 0.9;
        int i = 0;
        while (i < ordered.length && rank[ordered[i]] >= minRank) {
            res.add(new RankedString(keywords.get(ordered[i]), rank[ordered[i]]));
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

    public static void main(String[] args) throws JWNLException, FileNotFoundException {
        KeywordsRankSyntacticalDependencies kr = new KeywordsRankSyntacticalDependencies("RNA-Seq technique has been demonstrated as a revolutionary means for exploring transcriptome because it provides deep coverage and base-pair level resolution. RNA-Seq quantification is proven to be an efficient alternative to Microarray technique in gene expression study, and is a critical component in RNA-Seq differential expression analysis.", 4);
        List<RankedString> kw = kr.topRankedKeywords(100);
        double sum = 0;
        for (RankedString s : kw) {
            System.out.println(s.string + " " + s.rank);
            sum += s.rank;
        }
        System.out.println(sum);

    }
}
