package nju.quadra.quantra.utils;

import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nju.quadra.quantra.utils.NumericalStatisticUtil.VAR_SAMPLE;

/**
 * Created by RaUkonn on 2017/3/7.
 */
public class StockStatisticUtil {

    public static double RATE(StockInfoPtr ptr) {
        if (ptr.prev() != null) {
            return (ptr.get().getAdjClose() - ptr.prev().get().getAdjClose()) / ptr.prev().get().getAdjClose();
        } else {
            return Double.NaN;
        }
    }

    public static double MA_CLOSE(StockInfoPtr ptr, int n) {
        double[] nums = new double[n];
        int i = 0;
        while (i < n && ptr != null) {
            nums[i] = ptr.get().getClose();
            ptr = ptr.prev();
            i++;
        }
        if (i < n) {
            return Double.NaN;
        } else {
            return NumericalStatisticUtil.MEAN(nums);
        }
    }

    public static double EMA_CLOSE(StockInfoPtr ptr, int n) {
        double[] nums = new double[n];
        int i = 0;
        while (i < n && ptr != null) {
            nums[i] = ptr.get().getClose();
            ptr = ptr.prev();
            i++;
        }
        if (i < n) {
            return Double.NaN;
        } else {
            return NumericalStatisticUtil.EMA(nums);
        }
    }

    public static double LLV(List<StockInfoPtr> stock) {
        return stock.stream().mapToDouble(u -> u.get().getLow()).min().getAsDouble();
    }

    public static double HHV(List<StockInfoPtr> stock) {
        return stock.stream().mapToDouble(u -> u.get().getHigh()).max().getAsDouble();
    }

    public static List<Double> DAILY_LOG_RETURN(List<StockInfoPtr> stock) {
        List<Double> logReturn = IntStream
                .range(1, stock.size())
                .mapToObj(i -> Math.log(stock.get(i).get().getAdjClose() / stock.get(i - 1).get().getAdjClose()))
                .collect(Collectors.toList());
        return logReturn;
    }

    public static double DAILY_LOG_RETURN_VAR(List<StockInfoPtr> stock) {
        return VAR_SAMPLE(DAILY_LOG_RETURN(stock));
    }

    public static List<Double> RISING_RATE(List<StockBaseProtos.StockBase.StockInfo> stock) {
        List<Double> result;
        int n = stock.size();
        result = IntStream.range(1, n)
                .mapToDouble(i -> (stock.get(i).getAdjClose() - stock.get(i - 1).getAdjClose())
                        / stock.get(i - 1).getAdjClose())
                .boxed()
                .collect(Collectors.toList());
        return result;
    }

}
