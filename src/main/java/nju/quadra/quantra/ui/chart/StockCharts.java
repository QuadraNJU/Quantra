package nju.quadra.quantra.ui.chart;

import javafx.scene.paint.Color;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.NumericalStatisticUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.util.ArrayList;
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
