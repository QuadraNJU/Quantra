package nju.quadra.quantra.ui.chart;

import javafx.collections.FXCollections;
import javafx.scene.chart.*;
import nju.quadra.quantra.data.StockBaseProtos;

import java.util.List;

/**
 * Created by adn55 on 2017/3/16.
 */
public class QuantraBarChart extends BarChart<String, Number> {

    private QuantraBarChart(Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
        this.setLegendVisible(false);
        this.setBarGap(1);
        this.setCategoryGap(0);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
    }

    public static QuantraBarChart createFrom(List<StockBaseProtos.StockBase.StockInfo> infoList) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        // Create series and add data
        Series<String, Number> series = new Series<>();
        for (StockBaseProtos.StockBase.StockInfo info : infoList) {
            series.getData().add(new Data<>(info.getDate(), info.getVolume(), info));
        }
        // Create chart
        QuantraBarChart chart = new QuantraBarChart(xAxis, yAxis);
        chart.setData(FXCollections.observableArrayList(series));
        return chart;
    }

    @Override
    protected void seriesAdded(Series<String, Number> series, int seriesIndex) {
        super.seriesAdded(series, seriesIndex);
        for (Data<String, Number> item : series.getData()) {
            if (item.getNode() != null) {
                StockBaseProtos.StockBase.StockInfo info = (StockBaseProtos.StockBase.StockInfo) item.getExtraValue();
                if (info.getClose() > info.getOpen()) {
                    item.getNode().getStyleClass().add("close-above-open");
                } else {
                    item.getNode().getStyleClass().add("open-above-close");
                }
            }
        }
    }

}
