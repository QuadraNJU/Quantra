package nju.quadra.quantra.ui.chart;

import javafx.scene.chart.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.List;

/**
 * Created by adn55 on 2017/3/16.
 */
public class QuantraLineChart extends LineChart<String, Number> {

    private List<StockInfoPtr> ptrList;

    private QuantraLineChart(Axis<String> xAxis, Axis<Number> yAxis, List<StockInfoPtr> ptrList) {
        super(xAxis, yAxis);
        this.ptrList = ptrList;
        this.setLegendVisible(false);
        this.setCreateSymbols(false);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
    }

    public static QuantraLineChart createFrom(List<StockInfoPtr> ptrList) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        // Create chart
        QuantraLineChart chart = new QuantraLineChart(xAxis, yAxis, ptrList);
        return chart;
    }

    public void addPath(String name, Paint color, List<Number> numbers) {
        Series<String, Number> series = new Series<>();
        int size = Math.min(ptrList.size(), numbers.size());
        for (int i = 0; i < size; i++) {
            if (numbers.get(i) != null) {
                series.getData().add(new Data<>(ptrList.get(i).get().getDate(), numbers.get(i)));
            }
        }
        series.setName(name);
        getData().add(series);
        if (series.getNode() != null) {
            Path path = (Path) series.getNode();
            path.setStroke(color);
            path.setStrokeWidth(2);
        }
    }

}
