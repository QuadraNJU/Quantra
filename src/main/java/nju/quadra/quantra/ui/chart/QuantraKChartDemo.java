package nju.quadra.quantra.ui.chart;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;
import nju.quadra.quantra.data.StockData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by adn55 on 2017/3/4.
 */
public class QuantraKChartDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Graph Test");
        List<StockInfo> infoList = StockData.getList();
        Stream<StockInfo> stream = infoList.stream().filter(info -> info.getDate().startsWith("3/") && info.getDate().endsWith("/14") && info.getCode() == 1);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        QuantraKChart kChart = new QuantraKChart(xAxis, yAxis);
        kChart.setTitle("深发展A 2014/03");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (XYChart.Data<String, Number> data : stream.map(info -> new XYChart.Data<String, Number>(info.getDate(), info.getOpen(), info)).collect(Collectors.toList())) {
            series.getData().add(0, data);
        }
        kChart.setData(FXCollections.observableArrayList(series));

        stage.setScene(new Scene(kChart, 800, 450));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
