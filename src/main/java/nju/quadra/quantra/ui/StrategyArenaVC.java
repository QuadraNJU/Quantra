package nju.quadra.quantra.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.*;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private PeriodStrategy strategy;
    private QuantraLineChart abnormalReturnChart;
    private QuantraLineChart strategyWinRateChart;

    public StrategyArenaVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/strategyArena.fxml"));
        radioMean.setSelected(true);
        comboPeriod.getItems().setAll("形成期", "持有期");
        comboPeriod.setValue(comboPeriod.getItems().get(0));
        dateStart.setValue(LocalDate.of(2013, 1, 1));
        dateEnd.setValue(LocalDate.of(2013, 12, 31));
        comboPool.getItems().setAll(Arrays.asList(new HS300Pool(), new ZxbPool(), new CybPool()));
        comboPool.getItems().addAll(StockPoolData.getPoolMap().values());
        comboPool.setValue(comboPool.getItems().get(0));
    }

    private String getStrategyString(boolean isFreq, int timeSpan) {
        if (isFreq)
            return "{\"start_date\":\"" + DateUtil.localDateToString(dateStart.getValue()) + "\"," +
                    "\"end_date\":\"" + DateUtil.localDateToString(dateEnd.getValue()) + "\"," +
                    "\"universe\":" + JSON.toJSONString(comboPool.getValue().getStockPool()) + "," +
                    "\"frequency\":" + strategy.freq + "," +
                    "\"strategy\":\"" + strategy.type + "\"," +
                    "\"params\":{\"period\":" + timeSpan + "}}";
        else
            return "{\"start_date\":\"" + DateUtil.localDateToString(dateStart.getValue()) + "\"," +
                    "\"end_date\":\"" + DateUtil.localDateToString(dateEnd.getValue()) + "\"," +
                    "\"universe\":" + JSON.toJSONString(comboPool.getValue().getStockPool()) + "," +
                    "\"frequency\":" + timeSpan + "," +
                    "\"strategy\":\"" + strategy.type + "\"," +
                    "\"params\":{\"period\":" + strategy.period + "}}";
    }

    @FXML
    private void onRunAction() {
        boolean isFreq = comboPeriod.getValue().equals("持有期");
        int timeSpan;
        try {
            if ((timeSpan = Integer.parseInt(textPeriod.getText())) < 0) {
                UIContainer.alert("警告", comboPeriod.getValue() + "需为正整数，请重新输入！");
                return;
            }
        } catch (Exception e) {
            UIContainer.alert("警告", "请输入正整数");
            textPeriod.clear();
            return;
        }

        if (radioMean.isSelected()) {
            strategy = new PeriodStrategy("mean_reversion", "mean_reversion", isFreq ? timeSpan : 0, 0, isFreq ? 0 : timeSpan);
        } else {
            strategy = new PeriodStrategy("momentum", "momentum", isFreq ? timeSpan : 0, 0, isFreq ? 0 : timeSpan);
        }

        List<Number> abnormalReturnList = new ArrayList<>();
        List<Number> winRateList = new ArrayList<>();
        List<Number> timeSpans = new ArrayList<>();
        ArrayList<String> strategies = new ArrayList<>();

        try {
            for (int period = 5; period <= 50; period += 5) {
                timeSpans.add(period);
                strategies.add(getStrategyString(isFreq, period));
            }
            PPAP.extractEngine("data/python");
            PPAP.extractEngine("engine_for_arena", "data/python");
            strategy.extract("data/python");
            PPAP ppap = new PPAP("python engine_for_arena.py", "data/python");

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < strategies.size(); i++) {
                if (i < strategies.size() - 1) {
                    if (i == 0)
                        sb.append("[");
                    sb.append(strategies.get(i) + ",");
                } else
                    sb.append(strategies.get(i) + "]");
            }

            ppap.sendInput(sb.toString());
            ppap.setErrorHandler(System.err::println);
            ppap.waitEnd();

            InputStream is = new FileInputStream("data/result.json");
            byte[] buf = new byte[is.available()];
            is.read(buf);
            is.close();
            JSONArray jsonArray = JSON.parseArray(new String(buf, "UTF-8"));
            for (int i = jsonArray.size() - 1; i >= 0; i--) {
                JSONObject o = jsonArray.getJSONObject(i);
                Float abnormalReturn = o.getFloat("abnormal_return");
                Float winRate = o.getFloat("win_rate");
                abnormalReturnList.add(abnormalReturn);
                winRateList.add(winRate);
            }
            abnormalReturnChart = QuantraLineChart.createFromDates(timeSpans.stream()
                    .map(Number::toString).collect(Collectors.toList()));
            abnormalReturnChart.addPath("超额收益率", Color.YELLOW, abnormalReturnList);
            strategyWinRateChart = QuantraLineChart.createFromDates(timeSpans.stream()
                    .map(Number::toString).collect(Collectors.toList()));
            strategyWinRateChart.addPath("策略胜率", Color.LIGHTPINK, winRateList);
            paneAbnormalReturn.setCenter(abnormalReturnChart);
            paneWinningRate.setCenter(strategyWinRateChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
