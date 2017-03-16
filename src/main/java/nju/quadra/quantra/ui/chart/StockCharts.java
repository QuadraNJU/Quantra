package nju.quadra.quantra.ui.chart;

import javafx.scene.paint.Color;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.NumericalStatisticUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by adn55 on 2017/3/16.
 */
public class StockCharts {

    public static QuantraBarChart VOL(List<StockInfoPtr> ptrList) {
        return QuantraBarChart.createFrom(ptrList, "成交量");
    }

    public static QuantraLineChart MACD(List<StockInfoPtr> ptrList) {
        int srt = 12, lng = 26, mid = 9;
        LinkedList<Number> difList = new LinkedList<>();
        ArrayList<Number> deaList = new ArrayList<>();
        ArrayList<Number> macdList = new ArrayList<>();
        int i = 0;
        StockInfoPtr ptr = ptrList.get(0).prev();
        while (i < mid - 1 && ptr != null) {
            difList.add(StockStatisticUtil.EMA_CLOSE(ptr, srt) - StockStatisticUtil.EMA_CLOSE(ptr, lng));
            ptr = ptr.prev();
            i++;
        }
        for (StockInfoPtr ptr2 : ptrList) {
            double dif = StockStatisticUtil.EMA_CLOSE(ptr2, srt) - StockStatisticUtil.EMA_CLOSE(ptr2, lng);
            difList.addFirst(dif);
            double dea = Double.NaN;
            i++;
            if (i >= mid) {
                dea = NumericalStatisticUtil.EMA(difList.subList(0, mid).stream().mapToDouble(Number::doubleValue).toArray());
            }
            deaList.add(dea);
            macdList.add((dif - dea) * 2);
        }
        LinkedList<Number> difList2 = new LinkedList<>();
        for (Number num : difList.subList(0, ptrList.size())) {
            difList2.addFirst(num);
        }
        QuantraLineChart lineChart = QuantraLineChart.createFrom(ptrList);
        lineChart.addPath("DIF", Color.WHITE, difList2);
        lineChart.addPath("DEA", Color.YELLOW, deaList);
        lineChart.addPath("MACD", Color.LIGHTGREEN, macdList);
        return lineChart;
    }

    public static QuantraLineChart KDJ(List<StockInfoPtr> ptrList) {
        int n = 9, m = 3;
        ArrayList<Number> rsvList = new ArrayList<>();
        ArrayList<Number> kList = new ArrayList<>();
        ArrayList<Number> dList = new ArrayList<>();
        ArrayList<Number> jList = new ArrayList<>();
        int i = 0, size = ptrList.size();
        StockInfoPtr ptr = ptrList.get(0).prev();
        while (i < 2 * m - 2 && ptr != null) {
            ptrList.add(0, ptr);
            ptr = ptr.prev();
            i++;
        }
        i = 0;
        for (StockInfoPtr ptr2 : ptrList) {
            double[] lows = new double[n];
            double[] highs = new double[n];
            double close = ptr2.get().getClose();
            double rsv = Double.NaN;
            int j = 0;
            while (j < n && ptr2 != null) {
                lows[j] = ptr2.get().getLow();
                highs[j] = ptr2.get().getHigh();
                ptr2 = ptr2.prev();
                j++;
            }
            if (j >= n) {
                double llv = Arrays.stream(lows).min().getAsDouble();
                double hhv = Arrays.stream(highs).max().getAsDouble();
                rsv = (close - llv) / (hhv - llv) * 100;
            }
            rsvList.add(rsv);
            i++;
            if (i >= m) {
                double k = NumericalStatisticUtil.MEAN(rsvList.subList(i - m, i).stream().mapToDouble(Number::doubleValue).toArray());
                kList.add(k);
                double d = NumericalStatisticUtil.MEAN(kList.subList(i - m, i).stream().mapToDouble(Number::doubleValue).toArray());
                dList.add(d);
                jList.add(3 * k - 2 * d);
            } else {
                kList.add(Double.NaN);
                dList.add(Double.NaN);
                jList.add(Double.NaN);
            }
        }
        QuantraLineChart lineChart = QuantraLineChart.createFrom(ptrList);
        int substart = kList.size() - size;
        size = kList.size();
        lineChart.addPath("K", Color.WHITE, kList.subList(substart, size));
        lineChart.addPath("D", Color.YELLOW, dList.subList(substart, size));
        lineChart.addPath("J", Color.LIGHTPINK, jList.subList(substart, size));
        return lineChart;
    }

    public static QuantraLineChart BOLL(List<StockInfoPtr> ptrList) {
        int m = 20;
        ArrayList<Number> bollList = new ArrayList<>();
        ArrayList<Number> upperList = new ArrayList<>();
        ArrayList<Number> lowerList = new ArrayList<>();
        for (StockInfoPtr ptr : ptrList) {
            double boll = StockStatisticUtil.MA_CLOSE(ptr, m);
            bollList.add(boll);
            double[] nums = new double[m];
            double std = Double.NaN;
            int i = 0;
            while (i < m && ptr != null) {
                nums[i] = ptr.get().getClose();
                ptr = ptr.prev();
                i++;
            }
            if (i >= m) {
                std = NumericalStatisticUtil.STD_SAMPLE(nums);
            }
            upperList.add(boll + 2 * std);
            lowerList.add(boll - 2 * std);
        }
        QuantraLineChart lineChart = QuantraLineChart.createFrom(ptrList);
        lineChart.addPath("BOLL", Color.WHITE, bollList);
        lineChart.addPath("UPPER", Color.YELLOW, upperList);
        lineChart.addPath("LOWER", Color.LIGHTPINK, lowerList);
        return lineChart;
    }

    public static QuantraLineChart PSY(List<StockInfoPtr> ptrList) {
        int n = 12;
        ArrayList<Number> psyList = new ArrayList<>();
        for (StockInfoPtr ptr : ptrList) {
            int i = 0, count = 0;
            while (i < n && ptr != null) {
                if (ptr.prev() != null && ptr.get().getClose() > ptr.prev().get().getClose()) {
                    count++;
                }
                ptr = ptr.prev();
                i++;
            }
            if (i >= n) {
                psyList.add(count * 1.0 / n * 100);
            } else {
                psyList.add(Double.NaN);
            }
        }
        QuantraLineChart lineChart = QuantraLineChart.createFrom(ptrList);
        lineChart.addPath("PSY", Color.WHITE, psyList);
        return lineChart;
    }

}
