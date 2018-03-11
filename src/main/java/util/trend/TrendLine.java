package util.trend;

/**
 * Created by Alexey on 10.12.2017.
 */
public interface TrendLine {
    public void setValues(double[] y, double[] x); // y ~ f(x)
    public double predict(double x); // get a predicted y for a given x
}
