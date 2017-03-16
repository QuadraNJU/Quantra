package nju.quadra.quantra.ui.chart;

import nju.quadra.quantra.data.StockInfoPtr;

import java.util.List;

/**
 * Created by adn55 on 2017/3/16.
 */
public class StockCharts {

    public static QuantraBarChart VOL(List<StockInfoPtr> ptrList) {
        return QuantraBarChart.createFrom(ptrList, "成交量");
    }

    public static QuantraLineChart MACD(List<StockInfoPtr> ptrList) {
        QuantraLineChart lineChart = QuantraLineChart.createFrom(ptrList);
        //lineChart.addPath("DIF", Color.WHITE, ptrList.stream().mapToDouble(ptr -> ptr.get().));
        return lineChart;
    }

}
