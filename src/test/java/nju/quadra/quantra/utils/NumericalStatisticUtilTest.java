package nju.quadra.quantra.utils;

import nju.quadra.quantra.utils.NumericalStatisticUtil;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by RaUkonn on 2017/3/4.
 */
public class NumericalStatisticUtilTest {
    @Test
    public void testMean() {
        double[] list = {1, 2, 3};
        Assert.assertEquals(2, NumericalStatisticUtil.MEAN(list), 0.01);
    }

    @Test
    public void testAvedev() {
        double[] list = {1, 2, 3};
        Assert.assertEquals(0.6666, NumericalStatisticUtil.AVEDEV(list), 0.01);
    }

    @Test
    public void testDevsq() {
        double[] list = {1, 2, 3};
        Assert.assertEquals(2, NumericalStatisticUtil.DEVSQ(list), 0.01);
    }

    @Test
    public void testSlope() {
        double[][] list = {
                {1, 2, 3},
                {2, 4, 6}
        };
        Assert.assertEquals(2, NumericalStatisticUtil.SLOPE(list), 0.01);
    }

    @Test
    public void testForecast() {
        double[][] list = {
                {1, 2, 3},
                {2, 4, 6}
        };
        Assert.assertEquals(12, NumericalStatisticUtil.FORECAST(list, 6), 0.01);
    }

    @Test
    public void testVarSample() {
        double[] list = {11, 45, 77, 10, 96};
        Assert.assertEquals(1491.7, NumericalStatisticUtil.VAR_SAMPLE(list), 0.01);

    }

    @Test
    public void testVarTotal() {
        double[] list = {11, 45, 77, 10, 96};
        Assert.assertEquals(1193.36, NumericalStatisticUtil.VAR_TOTAL(list), 0.01);
    }

    @Test
    public void testStdTotal() {
        double[] list = {11, 45, 77, 10, 96};
        Assert.assertEquals(34.54504, NumericalStatisticUtil.STD_TOTAL(list), 0.01);
    }

    @Test
    public void testStdSample() {
        double[] list = {11, 45, 77, 10, 96};
        Assert.assertEquals(38.62253, NumericalStatisticUtil.STD_SAMPLE(list), 0.01);
    }

    @Test
    public void testCovMatrix() {
        double[][] list = {
                {1, 5, 6},
                {4, 3, 9},
                {4, 2, 9},
                {4, 7, 2}
        };
        RealMatrix result = NumericalStatisticUtil.COV_MATRIX(list);
        Assert.assertEquals(4.9167, result.getData()[1][1], 0.01);
    }

    @Test
    public void testCov() {
        double[][] list = {
                {5, 20, 40, 80, 100},
                {10, 24, 33, 54, 10}
        };
        Assert.assertEquals(187.75, NumericalStatisticUtil.COV(list), 0.01);

        list = new double[][]{
                {3.0 / 4, 0.33}, //set of x
                {1.0 / 4, 0.67} // set of y
        };
        Assert.assertEquals(-0.085, NumericalStatisticUtil.COV(list), 0.01);
    }

    @Test
    public void testRelate() {
        double[][] list = {
                {1.0 / 4, 0.33},
                {3.0 / 4, 0.67}
        };
        Assert.assertEquals(-1, NumericalStatisticUtil.RELATE(list), 0.01);
    }

}
