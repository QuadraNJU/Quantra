package nju.quadra.quantra.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.data.StockPoolData;
import nju.quadra.quantra.pool.AbstractPool;
import nju.quadra.quantra.pool.CybPool;
import nju.quadra.quantra.pool.HS300Pool;
import nju.quadra.quantra.pool.ZxbPool;
import nju.quadra.quantra.strategy.PeriodStrategy;
import nju.quadra.quantra.ui.chart.QuantraLineChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.PPAP;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by RaUkonn on 2017/4/6.
 */
public class StrategyArenaVC extends Pane {
    @FXML
    private JFXRadioButton radioMean, radioMom;
    @FXML
    private JFXDatePicker dateStart, dateEnd;
    @FXML
    private JFXComboBox<AbstractPool> comboPool;
    @FXML
    private JFXComboBox<String> comboPeriod;
    @FXML
    private JFXTextField textPeriod;
    @FXML
    private HBox running;
    @FXML
    private JFXProgressBar progress;
    @FXML
    private Label labelProgress;
    @FXML
    private BorderPane paneAbnormalReturn, paneWinningRate;

    public StrategyArenaVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/strategyArena.fxml"));
        radioMom.setSelected(true);
        comboPeriod.getItems().setAll("指定形成期", "指定持有期");
        comboPeriod.setValue(comboPeriod.getItems().get(0));
        dateStart.setValue(LocalDate.of(2013, 1, 1));
        dateEnd.setValue(LocalDate.of(2013, 12, 31));
        comboPool.getItems().setAll(Arrays.asList(new HS300Pool(), new ZxbPool(), new CybPool()));
        comboPool.getItems().addAll(StockPoolData.getPoolMap().values());
        comboPool.setValue(comboPool.getItems().get(0));
    }

    private String getArgs(int freq, int period) {
        return "{\"start_date\":\"" + DateUtil.localDateToString(dateStart.getValue()) + "\"," +
                "\"end_date\":\"" + DateUtil.localDateToString(dateEnd.getValue()) + "\"," +
                "\"universe\":" + JSON.toJSONString(comboPool.getValue().getStockPool()) + "," +
                "\"frequency\":" + freq + "," +
                "\"params\":{\"period\":" + period + "}}";
    }

    @FXML
    private void onRunAction() {
        int staticTime;
        PeriodStrategy strategy;
        try {
            staticTime = Integer.parseInt(textPeriod.getText());
            if (staticTime <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            UIContainer.alert("提示", comboPeriod.getValue() + "需为正整数，请重新输入！");
            return;
        }
        if (radioMean.isSelected()) {
            strategy = new PeriodStrategy("", "mean_reversion", 0, 0, 0);
        } else {
            strategy = new PeriodStrategy("", "momentum", 0, 0, 0);
        }

        ArrayList<Number> abnormalReturnList = new ArrayList<>();
        ArrayList<Number> winRateList = new ArrayList<>();
        ArrayList<String> timeSpans = new ArrayList<>();
        ArrayList<String> argsList = new ArrayList<>();
        boolean dynFreq = comboPeriod.getSelectionModel().getSelectedIndex() == 0;
        for (int span = 5; span <= 30; span += 5) {
            timeSpans.add(String.valueOf(span));
            if (dynFreq) {
                argsList.add(getArgs(span, staticTime));
            } else {
                argsList.add(getArgs(staticTime, span));
            }
        }

        labelProgress.setText("0%");
        progress.setProgress(0);
        running.setVisible(true);
        new Thread(() -> {
            try {
                PPAP.extractEngine("data/python");
                PPAP.extractEngine("arena", "data/python");
                strategy.extract("data/python");
                PPAP ppap = new PPAP("python arena.py", "data/python");
                int[] progressArray = new int[argsList.size()];
                JSONObject[] resultArray = new JSONObject[argsList.size()];
                ppap.setOutputHandler(out -> {
                    JSONObject jsonObject = JSON.parseObject(out);
                    int thread = jsonObject.getInteger("thread");
                    if (jsonObject.containsKey("success")) {
                        resultArray[thread] = jsonObject;
                    } else {
                        progressArray[thread] = jsonObject.getInteger("progress");
                        Platform.runLater(() -> {
                            int avgProgress = (int) Arrays.stream(progressArray).average().getAsDouble();
                            progress.setProgress(avgProgress / 100.0);
                            labelProgress.setText(avgProgress + "%");
                        });
                    }
                });
                ppap.setErrorHandler(System.err::println);
                ppap.sendInput("[" + String.join(",", argsList) + "]");
                ppap.waitEnd();

                for (JSONObject o : resultArray) {
                    float abnormalReturn = o.getFloat("annualized") - o.getFloat("base_annualized");
                    float winRate = o.getFloat("win_rate");
                    abnormalReturnList.add(abnormalReturn);
                    winRateList.add(winRate);
                }
                Platform.runLater(() -> {
                    QuantraLineChart abnormalReturnChart = QuantraLineChart.createFromDates(timeSpans.stream()
                            .map(String::valueOf).collect(Collectors.toList()));
                    abnormalReturnChart.addPath("超额收益率", Color.YELLOW, abnormalReturnList);
                    QuantraLineChart strategyWinRateChart = QuantraLineChart.createFromDates(timeSpans.stream()
                            .map(String::valueOf).collect(Collectors.toList()));
                    strategyWinRateChart.addPath("策略胜率", Color.LIGHTPINK, winRateList);
                    paneAbnormalReturn.setCenter(abnormalReturnChart);
                    paneWinningRate.setCenter(strategyWinRateChart);
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                running.setVisible(false);
            }
        }).start();
    }

}
