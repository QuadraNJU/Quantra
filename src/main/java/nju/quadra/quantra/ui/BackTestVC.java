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
import nju.quadra.quantra.data.StockPoolData;
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
    private Label labelStrategy, labelProgress, labelAnnualized, labelBaseAnnualized, labelWinRate, labelAlpha, labelBeta, labelSharp;
    @FXML
    private BorderPane paneChart, paneHist;
    @FXML
    private TableView<DailyDetail> tableDetails;

    private QuantraLineChart lineChart;
    private AbstractStrategy strategy;
    private AbstractPool pool;

    public BackTestVC(AbstractStrategy strategy) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/backTest.fxml"));
        this.strategy = strategy;
        labelStrategy.setText(strategy.name + " [ " + strategy.getDescription() + " ]");
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
        System.out.println(pool.name);
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
                System.out.println(JSON.toJSONString(pool.getStockPool()));
                PPAP ppap = new PPAP("python engine.py", "data/python");
                ppap.sendInput("{\"start_date\":\"" + DateUtil.localDateToString(dateStart.getValue())
                        + "\",\"end_date\":\"" + DateUtil.localDateToString(dateEnd.getValue())
                        + "\",\"universe\":" + JSON.toJSONString(pool.getStockPool())
                        + ",\"frequency\":" + strategy.freq + "}");
                ArrayList<String> dates = new ArrayList<>();
                ArrayList<Number> earnRates = new ArrayList<>();
                ArrayList<Number> baseEarnRates = new ArrayList<>();
                ppap.setOutputHandler(out -> {
                    JSONObject jsonObject = JSON.parseObject(out);
                    if (jsonObject.containsKey("success")) {
                        lineChart = QuantraLineChart.createFromDates(dates);
                        lineChart.addPath("策略收益率", Color.LIGHTPINK, earnRates);
                        lineChart.addPath("基准收益率", Color.WHITESMOKE, baseEarnRates);
                        updateIndex();
                        Platform.runLater(() -> {
                            paneChart.setCenter(lineChart);
                            paneHist.setCenter(createHistogram(dates, earnRates));
                            DecimalFormat df = new DecimalFormat("#.##");
                            labelAnnualized.setText(df.format(jsonObject.getFloat("annualized") * 100) + "%");
                            labelBaseAnnualized.setText(df.format(jsonObject.getFloat("base_annualized") * 100) + "%");
                            labelWinRate.setText(df.format(jsonObject.getFloat("win_rate") * 100) + "%");
                            labelAlpha.setText(df.format(jsonObject.getFloat("alpha")));
                            labelBeta.setText(df.format(jsonObject.getFloat("beta")));
                            labelSharp.setText(df.format(jsonObject.getFloat("sharp")));
                        });
                    } else {
                        Platform.runLater(() -> {
                            String date = jsonObject.getString("date");
                            Float cash = jsonObject.getFloat("cash");
                            Float earnRate = jsonObject.getFloat("earn_rate");
                            Float baseEarnRate = jsonObject.getFloat("base_earn_rate");
                            dates.add(date);
                            earnRates.add(earnRate);
                            baseEarnRates.add(baseEarnRate);
                            progress.setProgress(jsonObject.getInteger("progress") / 100.0);
                            labelProgress.setText(jsonObject.getInteger("progress") + "%");
                            tableDetails.getItems().add(new DailyDetail(date, cash, earnRate, baseEarnRate));
                        });
                    }
                });
                ppap.setErrorHandler(System.err::println);
                ppap.waitEnd();
                running.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
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
