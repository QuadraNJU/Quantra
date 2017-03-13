package nju.quadra.quantra.ui.chart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;
import nju.quadra.quantra.data.StockData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/4.
 */
public class QuantraKChartDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new Thread(() -> {
            List<StockInfo> infoList = StockData.getList().stream().filter(info -> info.getDate().startsWith("3/") && info.getDate().endsWith("/14") && info.getCode() == 1).collect(Collectors.toList());
            QuantraKChart kChart = QuantraKChart.createFrom(infoList);
            kChart.addPath("AdjClose", Color.YELLOW, infoList.stream().map(StockInfo::getAdjClose).collect(Collectors.toList()));
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
