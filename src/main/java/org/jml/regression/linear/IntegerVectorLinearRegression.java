package org.jml.regression.linear;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class IntegerVectorLinearRegression {

    private int intercept;
    private int slope;
    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public void fit(int[] x, int[] y) {
        if (x.length != y.length) throw new IllegalArgumentException("Arrays length are not equals");


        int sumx = 0, sumy = 0;
        int i = 0;
        int upperBound = SPECIES.loopBound(x.length); // defines the upperbound of array length in which a vector transformation can be applied
        for (; i < upperBound; i += SPECIES.length()) {
            var xs = IntVector.fromArray(SPECIES, x, i);
            var ys = IntVector.fromArray(SPECIES, y, i);
            sumx += xs.reduceLanes(VectorOperators.ADD);
            sumy += ys.reduceLanes(VectorOperators.ADD);
        }

        for (; i < x.length; i++) {
            sumx += x[i];
            sumy += y[i];
        }

        int xbar = sumx / x.length;
        int ybar = sumy / x.length;

        int xxbar = 0;
        int xybar = 0;

        i = 0;
        for (; i < upperBound; i += SPECIES.length()) {             // (x[i] - xbar) * (x[i] - xbar)
            var xs = IntVector.fromArray(SPECIES, x, i);
            var ys = IntVector.fromArray(SPECIES, y, i);
            xs = xs.sub(xbar);
            ys = ys.sub(ybar).mul(xs); // ys = xs.mul(ys.sub(ybar));
            xs = xs.mul(xs);
            xxbar += xs.reduceLanes(VectorOperators.ADD);
            xybar += ys.reduceLanes(VectorOperators.ADD);
        }

        for (; i < x.length; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }

        slope = xybar / xxbar;
        intercept = ybar - slope * xbar;
    }

    public int getIntercept() {
        return intercept;
    }

    public int getSlope() {
        return slope;
    }

    public int predict(int x) {
        return slope*x + intercept;
    }

    @Override
    public String toString() {
        return "VectorLinearRegression{" +
                "intercept=" + intercept +
                ", slope=" + slope +
                '}';
    }
}
