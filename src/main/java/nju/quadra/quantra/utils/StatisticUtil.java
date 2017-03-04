package nju.quadra.quantra.utils;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics;
import org.apache.commons.math.stat.regression.SimpleRegression;

import java.util.Arrays;

/**
 * Created by RaUkonn on 2017/3/4.
 */
public class StatisticUtil {
    public static double MEAN(double[] list) { return Arrays.stream(list).average().getAsDouble(); }

    /**
     * Mean Absolute Error
     * @param list
     * @return
     */
    public static double AVEDEV(double[] list) {
        double mean = MEAN(list);
        return Arrays.stream(list).map(u -> Math.abs(u - mean)).average().getAsDouble();
    }

    /**
     * sum of square of deviations
     * @param list
     * @return
     */
    public static double DEVSQ(double[] list) {
        double mean = MEAN(list);
        return Arrays.stream(list).map(u -> (u - mean) * (u - mean)).sum();
    }


    /**
     * The slope of linear regression function
     * @param list(0) means the set of x
     * @param list(1) means the set of y
     * @return
     */
    public static double SLOPE(double[][] list) {
        SimpleRegression sr = new SimpleRegression();
        sr.addData(list);
        return sr.getSlope();
    }

    /**
     *  Use list of points to calculate a regression function and get the y of given x
     * @param list
     * @param x
     * @return
     */
    public static double FORECAST(double[][] list, double x) {
        SimpleRegression sr = new SimpleRegression();
        sr.addData(list);
        return sr.predict(x);
    }

    /**
     * Unbiased variance
     * @param list
     * @return
     */
    public static double VAR(double[] list) {
        double mean = MEAN(list);
        return 1.0 / (list.length - 1) * DEVSQ(list);
    }

    public static double STD(double[] list) {
        return Math.sqrt(VAR(list));
    }

    private static double covLikeSum(double[] x, double[] y, double sigmax, double sigmay) {
        double meanx = MEAN(x);
        double meany = MEAN(y);
        double sum = 0;
        for(int i = 0; i < x.length; i++) {
            sum += (x[i] - meanx) * (y[i] - meany) / (sigmax * sigmay);
        }
        return 1.0 / (x.length - 1) * sum;
    }

    /**
     * The covariance of multi-parameter
     * @param list(0) means the set of x
     * @param list(1) means the set of y
     * @return
     */
    public static RealMatrix COV_MATRIX(double[][] list) {
        MultivariateSummaryStatistics mss = new MultivariateSummaryStatistics(list[0].length, true);
        Arrays.stream(list).forEach(u -> {
            try {
                mss.addValue(u);
            } catch (DimensionMismatchException e) {
                e.printStackTrace();
            }
        });
        return mss.getCovariance();
    }

    /**
     * The covariance of 2-D points
     * @param list(0) means the set of x
     * @param list(1) means the set of y
     * @return
     */
    public static double COV(double[][] list) {
        if(list.length == 1)
            return covLikeSum(list[0], list[0], 1, 1);
        return covLikeSum(list[0], list[1], 1, 1);
    }

    /**
     * The Relate coefficient parameter of 2-D points
     * @param list(0) means the set of x
     * @param list(1) means the set of y
     * @return
     */
    public static double RELATE(double[][] list) {
        double stdx = STD(list[0]);
        double stdy = STD(list[1]);
        return covLikeSum(list[0], list[1], stdx, stdy);
    }

    public static double BETA(double[] a, double[] m) {
        return COV(new double[][]{a, m}) / VAR(m);
    }

}
