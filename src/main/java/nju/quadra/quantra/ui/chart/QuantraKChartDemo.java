package nju.quadra.quantra.ui.chart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/4.
 */
public class QuantraKChartDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new Thread(() -> {
            List<StockInfoPtr> infoList = StockData.getPtrList().stream().filter(ptr -> ptr.get().getDate().startsWith("3/") && ptr.get().getDate().endsWith("/14") && ptr.get().getCode() == 1).collect(Collectors.toList());
            QuantraKChart kChart = QuantraKChart.createFrom(infoList);
            kChart.addPath("AdjClose", Color.YELLOW, infoList.stream().map(ptr -> ptr.get().getAdjClose()).collect(Collectors.toList()));
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
