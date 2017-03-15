package nju.quadra.quantra.ui.chart;

import javafx.collections.FXCollections;
import javafx.scene.chart.*;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/3/10.
 */
public class VolumeChart extends BarChart<String, Number> {

    public VolumeChart(Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
    }

    public static VolumeChart createFrom(List<StockBaseProtos.StockBase.StockInfo> infoList) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("日期");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        yAxis.setLabel("交易量");
        // Create series and add data
        infoList = infoList.subList(0, infoList.size());
        List<Integer> volumeRate = infoList.stream().mapToInt(u -> u.getVolume()).boxed().collect(Collectors.toList());
        Series<String, Number> series = new Series<>();
        for (int i = 0; i < infoList.size() - 1; i++) {
            series.getData().add(new Data<>(infoList.get(i).getDate(), volumeRate.get(i), infoList.get(0)));
        }
        // Create chart
        VolumeChart chart = new VolumeChart(xAxis, yAxis);
        chart.setData(FXCollections.observableArrayList(series));
        chart.setTitle(infoList.get(0).getName() + "近" + (infoList.size() - 1) + "个交易日该股交易量");
        chart.setLegendVisible(false);
        chart.getStylesheets().add("nju/quadra/quantra/ui/assets/charts.css");
        return chart;
    }
}
