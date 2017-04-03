package nju.quadra.quantra.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.pool.*;
import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.ui.chart.QuantraLineChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.PPAP;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by adn55 on 2017/3/29.
 */
public class BackTestVC extends Pane {

    @FXML
    JFXDatePicker dateStart, dateEnd;
    @FXML
    private HBox running;
    @FXML
    private JFXProgressBar progress;
    @FXML
    private Label labelStrategy, labelProgress, labelAnnualized, labelBaseAnnualized, labelAlpha, labelBeta, labelSharp;
    @FXML
    private BorderPane paneChart;
    @FXML
    private ChoiceBox<String> choicePool;

    private AbstractStrategy strategy;
    private AbstractPool pool;
    private List<AbstractPool> pools = new ArrayList<>();

    public BackTestVC(AbstractStrategy strategy) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/backTest.fxml"));
        this.strategy = strategy;
        labelStrategy.setText(strategy.name + " [ " + strategy.getDescription() + " ]");
        dateStart.setValue(LocalDate.of(2013, 1, 1));
        dateEnd.setValue(LocalDate.of(2013, 12, 31));
        loadPools();
        choicePool.setValue("沪深300");
        pool = new HS300Pool();
    }

    private void loadPools() {
        choicePool.getItems().clear();
        choicePool.getItems().addAll("沪深300", "中小板", "创业板");
        pools.clear();
        pools.addAll(Arrays.asList(new HS300Pool(), new ZxbPool(), new CybPool()));
        List<String> list = CustomPool.getCustomPoolList();
        if (list != null) {
            choicePool.getItems().addAll(list);
            pools.addAll(CustomPool.createPoolListFromFileList(list));
        }
    }

    @FXML
    private void onChangePoolAction() {
        for (AbstractPool p : pools) {
            if (p.name.equals(choicePool.getValue())) {
                pool = p;
                break;
            }
        }
        System.out.println(pool.name);
    }

    @FXML
    private void onBackAction() throws IOException {
        UIContainer.loadContent(new StrategyListVC());
    }

    @FXML
    private void onRunAction() {
        labelProgress.setText("0%");
        progress.setProgress(0);
        running.setVisible(true);
        new Thread(() -> {
            try {
                PPAP.extractEngine("data/python");
                strategy.extract("data/python");
                PPAP ppap = new PPAP("python engine.py", "data/python");
                ppap.sendInput("{\"start_date\":\"" + DateUtil.localDateToString(dateStart.getValue())
                        + "\",\"end_date\":\"" + DateUtil.localDateToString(dateEnd.getValue())
                        + "\",\"universe\":" + JSON.toJSONString(pool.getStockPool())
                        + ",\"frequency\":" + strategy.freq + "}");
                ArrayList<String> dates = new ArrayList<>();
                ArrayList<Number> earnRates = new ArrayList<>();
                ArrayList<Number> baseEarnRates = new ArrayList<>();
                final boolean[] success = {false};
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
                        success[0] = true;
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
                StringBuilder errMsg = new StringBuilder();
                ppap.setErrorHandler(err -> errMsg.append(err).append("\n"));
                ppap.waitEnd();
                if (!success[0]) {
                    Platform.runLater(() -> UIContainer.alert("Python 运行时错误", errMsg.toString()));
                }
                running.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
