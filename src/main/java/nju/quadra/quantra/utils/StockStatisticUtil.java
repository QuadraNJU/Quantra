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
        if (ptr.getYesterday() != null) {
            return (ptr.getToday().getAdjClose() - ptr.getYesterday().getAdjClose()) / ptr.getYesterday().getAdjClose();
        } else {
            return Double.NaN;
        }
    }

    public static List<Double> DAILY_LOG_RETURN(List<StockBaseProtos.StockBase.StockInfo> stock) {
        List<Double> logReturn = IntStream
                .range(1, stock.size())
                .mapToObj(i -> Math.log(stock.get(i).getAdjClose() / stock.get(i - 1).getAdjClose()))
                .collect(Collectors.toList());
        return logReturn;
    }

    public static double DAILY_LOG_RETURN_VAR(List<StockBaseProtos.StockBase.StockInfo> stock) {
        return VAR_SAMPLE(DAILY_LOG_RETURN(stock));
    }

    public static double EMA(List<StockBaseProtos.StockBase.StockInfo> stock) {
        int n = stock.size();
        double alpha = 2.0 / (n + 1);
        return alpha * IntStream
                .range(0, n - 1)
                .mapToDouble(i -> Math.pow(alpha, i) * stock.get(i).getClose())
                .sum();
    }


    public static double SMA(List<StockInfoPtr> stock) {
        return stock.stream().mapToDouble(ptr -> ptr.getToday().getClose()).average().getAsDouble();
    }

    public static double BOLL_UPPER(List<StockInfoPtr> stock, int k) {
        return SMA(stock) + k * NumericalStatisticUtil.STD_SAMPLE(stock.stream()
                .mapToDouble(ptr -> ptr.getToday().getClose())
                .toArray());
    }

    public static double BOLL_UPPER(List<StockInfoPtr> stock) {
        return BOLL_UPPER(stock, 2);
    }

    public static double BOLL_LOWER(List<StockInfoPtr> stock, int k) {
        return SMA(stock) - k * NumericalStatisticUtil.STD_SAMPLE(stock.stream()
                .mapToDouble(ptr -> ptr.getToday().getClose())
                .toArray());
    }

    public static double BOLL_LOWER(List<StockInfoPtr> stock) {
        return BOLL_LOWER(stock, 2);
    }

//    public static double DIF(StockBaseProtos.StockBase.StockInfo info) {
//        //TODO:after finish getting by info...
//        List<StockBaseProtos.StockBase.StockInfo> list = StockData.getList().subList(info.getSerial(), info.getSerial() + 12);
//    }

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
