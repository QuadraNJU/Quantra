package nju.quadra.quantra.utils;

import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nju.quadra.quantra.utils.NumericalStatisticUtil.VAR_SAMPLE;

/**
 * Created by RaUkonn on 2017/3/7.
 */
public class StockStatisticUtil {
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


    public static double SMA(List<StockBaseProtos.StockBase.StockInfo> stock) {
        return stock.stream().mapToDouble(StockBaseProtos.StockBase.StockInfo::getClose).average().getAsDouble();
    }


    public static double BOLL_MID(List<StockBaseProtos.StockBase.StockInfo> stock) {
        return SMA(stock);
    }

    public static double BOLL_UPPER(List<StockBaseProtos.StockBase.StockInfo> stock, int k) {
        return BOLL_MID(stock) + k * NumericalStatisticUtil.STD_SAMPLE(stock.stream()
                .mapToDouble(StockBaseProtos.StockBase.StockInfo::getClose)
                .toArray());
    }

    public static double BOLL_UPPER(List<StockBaseProtos.StockBase.StockInfo> stock) {
        return BOLL_MID(stock) + 2 * NumericalStatisticUtil.STD_SAMPLE(stock.stream()
                .mapToDouble(StockBaseProtos.StockBase.StockInfo::getClose)
                .toArray());
    }

    public static double BOLL_LOWER(List<StockBaseProtos.StockBase.StockInfo> stock, int k) {
        return BOLL_MID(stock) - k * NumericalStatisticUtil.STD_SAMPLE(stock.stream()
                .mapToDouble(StockBaseProtos.StockBase.StockInfo::getClose)
                .toArray());
    }

    public static double BOLL_LOWER(List<StockBaseProtos.StockBase.StockInfo> stock) {
        return BOLL_MID(stock) - 2 * NumericalStatisticUtil.STD_SAMPLE(stock.stream()
                .mapToDouble(StockBaseProtos.StockBase.StockInfo::getClose)
                .toArray());
    }

//    public static double DIF(StockBaseProtos.StockBase.StockInfo info) {
//        //TODO:after finish getting by info...
//        List<StockBaseProtos.StockBase.StockInfo> list = StockData.getList().subList(info.getSerial(), info.getSerial() + 12);
//    }
}
