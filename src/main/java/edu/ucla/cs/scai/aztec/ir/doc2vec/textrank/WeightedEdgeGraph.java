package edu.ucla.cs.scai.aztec.ir.doc2vec.textrank;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class WeightedEdgeGraph {

    double[][] weights;
    double[] outgoingWeights;

    int n;

    public WeightedEdgeGraph(int n) {
        this.n = n;
        weights = new double[n][n];
        outgoingWeights = new double[n];
    }

    public void setWeight(int i, int j, double w) {
        double diff = w - weights[i][j];
        outgoingWeights[i] += diff;
        //outgoingWeights[j] += diff;
        weights[i][j] = w;
        //weights[j][i] = w;
    }

    public void addWeight(int i, int j, double w) {
        outgoingWeights[i] += w;
        //outgoingWeights[j] += w;
        weights[i][j] += w;
        //weights[j][i] += w;
    }

    public double[] computeNodeRank(double dampingFactor, double epsilonForSteadyState) {
        double[][] adjustedWeights = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjustedWeights[i][j] = outgoingWeights[i] == 0 ? 0 : weights[i][j] / outgoingWeights[i];
            }
        }
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = 1.0 / n;
        }
        boolean steady = false;
        while (!steady) {
            double[] newRes = new double[n];
            double max = 0;
            for (int v = 0; v < n; v++) {
                //update rank of v
                newRes[v] = (1 - dampingFactor) / n;
                for (int u = 0; u < n; u++) {
                    if (u != v) {
                        newRes[v] += res[u] * adjustedWeights[u][v];
                    }
                }
                if (newRes[v] > max) {
                    max = newRes[v];
                }
            }
            steady = true;
            for (int v = 0; v < n; v++) {
                newRes[v] /= max;
                if (Math.abs(newRes[v] - res[v]) > epsilonForSteadyState) {
                    steady = false;
                }
            }
            res = newRes;
        }
        return res;
    }

    public static void main(String[] args) {
        WeightedEdgeGraph g = new WeightedEdgeGraph(13);
        g.setWeight(0, 1, 1);
        g.setWeight(0, 2, 1);
        g.setWeight(0, 3, 1);
        g.setWeight(0, 4, 1);
        g.setWeight(1, 5, 1);
        g.setWeight(1, 6, 1);
        g.setWeight(2, 7, 1);
        g.setWeight(2, 8, 1);
        g.setWeight(3, 9, 1);
        g.setWeight(3, 10, 1);
        g.setWeight(4, 11, 1);
        g.setWeight(4, 12, 1);
        double[] r = g.computeNodeRank(0.85, 0.01);
        for (int i = 0; i < r.length; i++) {
            System.out.println(r[i]);
        }
    }

}
