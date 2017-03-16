package nju.quadra.quantra.ui.chart;

import javafx.collections.FXCollections;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/3/10.
 */
public class LowHighChart extends LineChart<String, Number> {

    public LowHighChart(Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
    }

    public static LowHighChart createFrom(List<StockBaseProtos.StockBase.StockInfo> infoList, boolean isHigh) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("日期");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        yAxis.setLabel(isHigh? "最大值": "最小值");
        // Create series and add data
        infoList = infoList.subList(0, infoList.size());
        List<Double> dataList = infoList.stream().mapToDouble(u -> (isHigh? u.getHigh(): u.getLow())).boxed().collect(Collectors.toList());
        Series<String, Number> series = new Series<>();
        for (int i = 0; i < infoList.size() - 1; i++) {
            series.getData().add(new Data<>(infoList.get(i).getDate(), dataList.get(i), infoList.get(0)));
        }
        // Create chart
        LowHighChart chart = new LowHighChart(xAxis, yAxis);
        chart.setData(FXCollections.observableArrayList(series));
        chart.setTitle(infoList.get(0).getName() + "近" + (infoList.size() - 1) + "个交易日" + (isHigh? "最大值": "最小值") + "趋势");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getStylesheets().add("nju/quadra/quantra/ui/assets/charts.css");
        return chart;
    }
}
