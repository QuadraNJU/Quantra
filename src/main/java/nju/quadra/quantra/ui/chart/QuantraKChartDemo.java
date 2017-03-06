package nju.quadra.quantra.ui.chart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;
import nju.quadra.quantra.data.StockData;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by adn55 on 2017/3/4.
 */
public class QuantraKChartDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new Thread(() -> {
            Stream<StockInfo> stream = StockData.getList().stream().filter(info -> info.getDate().startsWith("3/") && info.getDate().endsWith("/14") && info.getCode() == 1);
            QuantraKChart kChart = QuantraKChart.createFrom(stream.collect(Collectors.toList()));
            Platform.runLater(() -> {
                stage.setTitle("Graph Test");
                stage.setScene(new Scene(kChart, 800, 450));
                stage.show();
            });
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
