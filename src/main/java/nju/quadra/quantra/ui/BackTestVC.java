package nju.quadra.quantra.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.data.BackTestHistory;
import nju.quadra.quantra.data.BackTestHistoryData;
import nju.quadra.quantra.data.StockPoolData;
import nju.quadra.quantra.data.StrategyData;
import nju.quadra.quantra.pool.AbstractPool;
import nju.quadra.quantra.pool.CybPool;
import nju.quadra.quantra.pool.HS300Pool;
import nju.quadra.quantra.pool.ZxbPool;
import nju.quadra.quantra.pool.index.BaseIndex;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by adn55 on 2017/3/29.
 */
public class BackTestVC extends Pane {

    @FXML
    JFXDatePicker dateStart, dateEnd;
    @FXML
    private JFXComboBox<AbstractPool> choicePool;
    @FXML
    private JFXComboBox<BaseIndex> choiceIndex;
    @FXML
    private HBox running;
    @FXML
    private JFXProgressBar progress;
    @FXML
    private Label labelStrategy, labelProgress, labelAnnualized, labelBaseAnnualized, labelWinRate, labelAlpha,
            labelBeta, labelSharp, labelMaxDrawdown;
    @FXML
    private BorderPane paneChart, paneHist;
    @FXML
    private TableView<DailyDetail> tableDetails;

    private QuantraLineChart lineChart;
    private AbstractStrategy strategy;
    private AbstractPool pool;

    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<Number> cashs = new ArrayList<>();
    private ArrayList<Number> earnRates = new ArrayList<>();
    private ArrayList<Number> baseEarnRates = new ArrayList<>();
    private JSONObject resultObject = new JSONObject();

    public BackTestVC(AbstractStrategy strategy) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/backTest.fxml"));
        if (strategy != null) {
            this.strategy = strategy;
            labelStrategy.setText(strategy.name + " [ " + strategy.getDescription() + " ]");
        }
        dateStart.setValue(LocalDate.of(2013, 1, 1));
        dateEnd.setValue(LocalDate.of(2013, 12, 31));

        loadPools();
        pool = choicePool.getItems().get(0);
        choicePool.setValue(pool);

        choiceIndex.getItems().addAll(BaseIndex.values());
        choiceIndex.getSelectionModel().selectedItemProperty().addListener(observable -> updateIndex());

        tableDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("date"));
        tableDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("cash"));
        tableDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("baseEarnRate"));
        tableDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("earnRate"));
        ((TableColumn<DailyDetail, Float>) tableDetails.getColumns().get(3)).setCellFactory(column -> new TableCell<DailyDetail, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                TableRow row = getTableRow();
                if (!empty && item != null && row != null) {
                    setText(String.valueOf(item));
                    row.getStyleClass().removeAll("red", "green");
                    if (item > 0) {
                        row.getStyleClass().add("red");
                    } else if (item < 0) {
                        row.getStyleClass().add("green");
                    }
                }
            }
        });
    }

    public BackTestVC(BackTestHistory history) throws IOException {
        this((AbstractStrategy) null);
        AbstractStrategy usedStrategy = null;
        for (AbstractStrategy strategy : StrategyData.getStrategyList()) {
            if (strategy.time == history.strategyKey) {
                usedStrategy = strategy;
                break;
            }
        }
        if (usedStrategy != null) {
            this.strategy = usedStrategy;
            labelStrategy.setText(strategy.name + " [ " + strategy.getDescription() + " ]");
        }

        dateStart.setValue(history.dateStart);
        dateEnd.setValue(history.dateEnd);
        for (AbstractPool pool : choicePool.getItems()) {
            if (pool.name.equals(history.poolName)) {
                choicePool.setValue(pool);
                onChangePoolAction();
                break;
            }
        }

        resultObject = history.resultObject;
        dates = history.dates;
        cashs = history.cashs;
        earnRates = history.earnRates;
        baseEarnRates = history.baseEarnRates;
        loadResults();
    }

    private void loadPools() {
        choicePool.getItems().clear();
        choicePool.getItems().addAll(new HS300Pool(), new ZxbPool(), new CybPool());
        choicePool.getItems().addAll(StockPoolData.getPoolMap().values());
    }

    @FXML
    private void onChangePoolAction() {
        for (AbstractPool p : choicePool.getItems()) {
            if (p.name.equals(choicePool.getValue().name)) {
                pool = p;
                break;
            }
        }
    }

    private void updateIndex() {
        if (lineChart != null) {
            BaseIndex selected = choiceIndex.getSelectionModel().getSelectedItem();
            lineChart.removePath("参考收益率");
            if (selected != null && !selected.equals(BaseIndex.NONE)) {
                JSONObject indexData = selected.getDataObject();
                float first = Float.NaN;
                ArrayList<Number> numbers = new ArrayList<>();
                for (String date : lineChart.dates) {
                    if (indexData.containsKey(date)) {
                        if (Float.isNaN(first)) {
                            first = indexData.getJSONArray(date).getFloat(0);
                        }
                        float today = indexData.getJSONArray(date).getFloat(1);
                        numbers.add((today - first) / first);
                    } else {
                        numbers.add(Float.NaN);
                    }
                }
                lineChart.addPath("参考收益率", Color.LIGHTBLUE, numbers);
            }
        }
    }

    private BarChart<String, Number> createHistogram(List<String> dates, List<Number> rates) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> positive = new XYChart.Series<>();
        XYChart.Series<String, Number> negative = new XYChart.Series<>();
        positive.setName("正收益次数");
        negative.setName("负收益次数");
        int[] roundRates = rates.stream().mapToInt(x -> Math.round(x.floatValue() * 100)).toArray();
        int maxRate = Arrays.stream(roundRates).map(Math::abs).max().getAsInt();
        int[] positiveCount = new int[maxRate + 1];
        int[] negativeCount = new int[maxRate + 1];
        for (int i = 0; i < roundRates.length; i++) {
            float rate = rates.get(i).floatValue();
            int absRate = Math.abs(roundRates[i]);
            if (rate > 0) {
                positiveCount[absRate]++;
            } else if (rate < 0) {
                negativeCount[absRate]++;
            }
        }
        for (int i = 0; i <= maxRate; i++) {
            positive.getData().add(new XYChart.Data<>(i + "%", positiveCount[i]));
            negative.getData().add(new XYChart.Data<>(i + "%", negativeCount[i]));
        }
        barChart.getData().addAll(positive, negative);
        barChart.getStylesheets().add(getClass().getResource("chart/QuantraKChart.css").toString());
        barChart.setBarGap(0);
        barChart.setCategoryGap(1);
        return barChart;
    }

    private void loadResults() {
        lineChart = QuantraLineChart.createFromDates(dates);
        lineChart.addPath("策略收益率", Color.LIGHTPINK, earnRates);
        lineChart.addPath("基准收益率", Color.WHITESMOKE, baseEarnRates);
        updateIndex();

        paneChart.setCenter(lineChart);
        paneHist.setCenter(createHistogram(dates, earnRates));
        DecimalFormat df = new DecimalFormat("#.##");
        labelAnnualized.setText(df.format(resultObject.getFloat("annualized") * 100) + "%");
        labelBaseAnnualized.setText(df.format(resultObject.getFloat("base_annualized") * 100) + "%");
        labelWinRate.setText(df.format(resultObject.getFloat("win_rate") * 100) + "%");
        labelAlpha.setText(df.format(resultObject.getFloat("alpha")));
        labelBeta.setText(df.format(resultObject.getFloat("beta")));
        labelSharp.setText(df.format(resultObject.getFloat("sharp")));
        labelMaxDrawdown.setText(df.format(resultObject.getFloat("max_drawdown")));

        tableDetails.getItems().addAll(
                IntStream.range(0, dates.size())
                        .mapToObj(i -> new DailyDetail(dates.get(i), cashs.get(i).floatValue(), earnRates.get(i).floatValue(), baseEarnRates.get(i).floatValue()))
                        .collect(Collectors.toList())
        );
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
        tableDetails.getItems().clear();
        new Thread(() -> {
            try {
                PPAP.extractEngine("data/python");
                strategy.extract("data/python");
                PPAP ppap = new PPAP("python engine.py", "data/python");
                dates.clear();
                cashs.clear();
                earnRates.clear();
                baseEarnRates.clear();
                final boolean[] success = {false};
                ppap.setOutputHandler(out -> {
                    JSONObject jsonObject = JSON.parseObject(out);
                    if (jsonObject.containsKey("success")) {
                        success[0] = true;
                        resultObject = jsonObject;
                        Platform.runLater(this::loadResults);
                        BackTestHistoryData.pushBackTestInfo(new BackTestHistory(
                                System.currentTimeMillis(),
                                strategy.time,
                                labelStrategy.getText(),
                                pool.name,
                                dateStart.getValue(),
                                dateEnd.getValue(),
                                resultObject,
                                dates,
                                cashs,
                                earnRates,
                                baseEarnRates
                        ));
                    } else {
                        dates.add(jsonObject.getString("date"));
                        cashs.add(jsonObject.getFloat("cash"));
                        earnRates.add(jsonObject.getFloat("earn_rate"));
                        baseEarnRates.add(jsonObject.getFloat("base_earn_rate"));
                        Platform.runLater(() -> {
                            progress.setProgress(jsonObject.getInteger("progress") / 100.0);
                            labelProgress.setText(jsonObject.getInteger("progress") + "%");
                        });
                    }
                });
                StringBuilder errMsg = new StringBuilder();
                ppap.setErrorHandler(err -> errMsg.append(err).append("\n"));
                ppap.sendInput("{\"start_date\":\"" + DateUtil.localDateToString(dateStart.getValue())
                        + "\",\"end_date\":\"" + DateUtil.localDateToString(dateEnd.getValue())
                        + "\",\"universe\":" + JSON.toJSONString(pool.getStockPool())
                        + ",\"frequency\":" + strategy.freq
                        + ",\"params\":" + JSON.toJSONString(strategy.getParams())
                        + "}");
                ppap.waitEnd();
                if (!success[0]) {
                    Platform.runLater(() -> UIContainer.alert("Python 运行时错误", errMsg.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                running.setVisible(false);
            }
        }).start();
    }

    public class DailyDetail {
        private String date;
        private float cash, earnRate, baseEarnRate;

        DailyDetail(String date, float cash, float earnRate, float baseEarnRate) {
            this.date = date;
            this.cash = cash;
            this.earnRate = earnRate;
            this.baseEarnRate = baseEarnRate;
        }

        public String getDate() {
            return date;
        }

        public float getCash() {
            return cash;
        }

        public float getEarnRate() {
            return earnRate;
        }

        public float getBaseEarnRate() {
            return baseEarnRate;
        }
    }

}
