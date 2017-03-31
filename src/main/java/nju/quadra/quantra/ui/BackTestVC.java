package nju.quadra.quantra.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.ui.chart.QuantraLineChart;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.PPAP;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by adn55 on 2017/3/29.
 */
public class BackTestVC extends Pane {

    @FXML
    HBox running;
    @FXML
    JFXProgressBar progress;
    @FXML
    Label labelProgress, labelAnnualized, labelBaseAnnualized, labelAlpha, labelBeta, labelSharp;
    @FXML
    BorderPane paneChart;

    public BackTestVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/backTest.fxml"));
    }

    @FXML
    public void onRunAction() {
        labelProgress.setText("0%");
        progress.setProgress(0);
        running.setVisible(true);
        new Thread(() -> {
            try {
                PPAP ppap = new PPAP("python engine.py", "python");
                ppap.sendInput("{\"start_date\":\"1/4/13\",\"end_date\":\"12/31/13\",\"universe\":[300001],\"frequency\":10,\"strategy\":\"momentum\"}");
                ArrayList<String> dates = new ArrayList<>();
                ArrayList<Number> earnRates = new ArrayList<>();
                ArrayList<Number> baseEarnRates = new ArrayList<>();
                ppap.setOutputHandler(out -> {
                    JSONObject jsonObject = JSON.parseObject(out);
                    if (jsonObject.containsKey("success")) {
                        QuantraLineChart chart = QuantraLineChart.createFromDates(dates);
                        chart.addPath("策略收益率", Color.LIGHTPINK, earnRates);
                        chart.addPath("基准收益率", Color.WHITESMOKE, baseEarnRates);
                        Platform.runLater(() -> {
                            paneChart.setCenter(chart);
                            DecimalFormat df = new DecimalFormat("#.##");
                            labelAnnualized.setText(df.format(jsonObject.getFloat("annualized") * 100) + "%");
                            labelBaseAnnualized.setText(df.format(jsonObject.getFloat("base_annualized") * 100) + "%");
                            labelAlpha.setText(df.format(jsonObject.getFloat("alpha")));
                            labelBeta.setText(df.format(jsonObject.getFloat("beta")));
                            labelSharp.setText(df.format(jsonObject.getFloat("sharp")));
                        });
                    } else {
                        Platform.runLater(() -> {
                            dates.add(jsonObject.getString("date"));
                            earnRates.add(jsonObject.getFloat("earn_rate"));
                            baseEarnRates.add(jsonObject.getFloat("base_earn_rate"));
                            progress.setProgress(jsonObject.getInteger("progress") / 100.0);
                            labelProgress.setText(jsonObject.getInteger("progress") + "%");
                        });
                    }
                });
                ppap.waitEnd();
                running.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
