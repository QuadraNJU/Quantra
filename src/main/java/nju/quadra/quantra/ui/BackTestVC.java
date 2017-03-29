package nju.quadra.quantra.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.ui.chart.QuantraLineChart;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.PPAP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adn55 on 2017/3/29.
 */
public class BackTestVC extends Pane {

    @FXML
    Label labelProgress, labelEarnRate, labelCash;
    @FXML
    BorderPane paneChart;

    public BackTestVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/backTest.fxml"));
    }

    @FXML
    public void onRunAction() {
        labelProgress.setText("Wait...");
        new Thread(() -> {
            try {
                PPAP ppap = new PPAP("python engine.py", "python");
                ppap.sendInput("{\"start_date\":\"1/4/13\",\"end_date\":\"12/31/13\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\"}");
                ArrayList<String> dates = new ArrayList<>();
                ppap.setOutputHandler(out -> {
                    JSONObject jsonObject = JSON.parseObject(out);
                    if (jsonObject.containsKey("success")) {
                        QuantraLineChart chart = QuantraLineChart.createFromDates(dates);
                        chart.addPath("策略收益率", Color.LIGHTBLUE, jsonObject.getObject("daily_earnings_rate", List.class));
                        Platform.runLater(() -> {
                            labelProgress.setText("100%");
                            paneChart.setCenter(chart);
                        });
                    } else {
                        Platform.runLater(() -> {
                            labelProgress.setText(jsonObject.getInteger("progress") + "%");
                            labelCash.setText(String.valueOf(jsonObject.getFloat("cash")));
                            labelEarnRate.setText(String.valueOf(jsonObject.getFloat("earn_rate")));
                            dates.add(jsonObject.getString("date"));
                        });
                    }
                });
                ppap.waitEnd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
