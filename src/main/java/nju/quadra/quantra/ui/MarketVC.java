package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfo;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.ui.chart.QuantraLineChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by RaUkonn on 2017/3/7.
 */
public class MarketVC extends Pane {

    private MarketMiniListVC risingLimit = new MarketMiniListVC(true, "今日涨停");
    private MarketMiniListVC fallingLimit = new MarketMiniListVC(false, "今日跌停");
    private MarketMiniListVC risingOverFivePer = new MarketMiniListVC(true, "上涨超5%");
    private MarketMiniListVC fallingOverFivePer = new MarketMiniListVC(false, "下跌超5%");
    private MarketMiniListVC underLastFivePer = new MarketMiniListVC(true, "上期指数超-5%", "指数");
    private MarketMiniListVC overLastFivePer = new MarketMiniListVC(false, "上期指数超5%", "指数");

    @FXML
    private GridPane gridPane, gridTemp;
    @FXML
    private Label labelDate, labelVolume;
    @FXML
    private JFXDatePicker picker;
    @FXML
    private BorderPane paneVolChart;

    public MarketVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/market.fxml"));
        gridPane.add(risingLimit, 0, 0);
        gridPane.add(fallingLimit, 1, 0);
        gridPane.add(risingOverFivePer, 0, 1);
        gridPane.add(fallingOverFivePer, 1, 1);
        gridPane.add(underLastFivePer, 0, 2);
        gridPane.add(overLastFivePer, 1, 2);
        picker.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadLists(DateUtil.localDateToString(newValue));
        });
        picker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));
        picker.setDayCellFactory(DateUtil.dayCellFactory);
    }

    private String dateParser(String dateFromDataset) {
        String[] dateList = dateFromDataset.split("/");
        return "20" + dateList[2] + "/" + dateList[0] + "/" + dateList[1];
    }

    private void loadLists(String date) {
        UIContainer.showLoading();
        new Thread(() -> {
            int[] cnts = new int[3];
            List<StockInfo> stockRisingLimit = new ArrayList<>();
            List<Double> risingLimitRate = new ArrayList<>();
            List<StockInfo> stockFallingLimit = new ArrayList<>();
            List<Double> fallingLimitRate = new ArrayList<>();
            List<StockInfo> stockRisingOverFivePer = new ArrayList<>();
            List<Double> risingOverFivePerRate = new ArrayList<>();
            List<StockInfo> stockFallingOverFivePer = new ArrayList<>();
            List<Double> fallingOverFivePerRate = new ArrayList<>();
            List<StockInfo> stockOverLastFivePer = new ArrayList<>();
            List<Double> overLastFivePerRate = new ArrayList<>();
            List<StockInfo> stockUnderLastFivePer = new ArrayList<>();
            List<Double> underLastFivePerRate = new ArrayList<>();

            List<StockInfoPtr> ptrs = StockData.getByDate(date);
            if (ptrs.isEmpty()) {
                Platform.runLater(() -> {
                    picker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));
                    UIContainer.alert("错误", "当天无股票数据，请选择其它日期");
                    UIContainer.hideLoading();
                });
                return;
            }
            DateUtil.currentDate = date;
            for (StockInfoPtr ptr : ptrs) {
                if (ptr.prev() != null) {
                    double rate = StockStatisticUtil.RATE(ptr);
                    double otherRate = (ptr.get().getOpen() - ptr.get().getClose()) / ptr.prev().get().getClose();
                    if (rate > 0) {
                        if (rate > 0.05) {
                            stockRisingOverFivePer.add(ptr.get());
                            risingOverFivePerRate.add(rate);
                            if (rate >= 0.1) {
                                stockRisingLimit.add(ptr.get());
                                risingLimitRate.add(rate);
                            }
                        }
                        cnts[0]++;
                    } else if (rate < 0) {
                        if (rate < -0.05) {
                            stockFallingOverFivePer.add(ptr.get());
                            fallingOverFivePerRate.add(rate);
                            if (rate <= -0.1) {
                                stockFallingLimit.add(ptr.get());
                                fallingLimitRate.add(rate);
                            }
                        }
                        cnts[2]++;
                    } else {
                        cnts[1]++;
                    }

                    if (otherRate > 0.05) {
                        stockOverLastFivePer.add(ptr.get());
                        overLastFivePerRate.add(otherRate);
                    } else if (otherRate < -0.05) {
                        stockUnderLastFivePer.add(ptr.get());
                        underLastFivePerRate.add(otherRate);
                    }
                }
            }

            LinkedList<StockInfoPtr> tmpPtrs = new LinkedList<>();
            LinkedList<Number> tmpVols = new LinkedList<>();
            List<StockInfoPtr> tmpPtrList = ptrs;
            LocalDate tmpDate = DateUtil.parseLocalDate(ptrs.get(0).get().getDate());
            for (int i = 0; i < 14; i++) {
                if (!tmpPtrList.isEmpty()) {
                    tmpPtrs.addFirst(tmpPtrList.get(0));
                    long vol = tmpPtrList.stream().mapToLong(ptr -> ptr.get().getVolume()).sum();
                    tmpVols.addFirst(vol);
                    if (i == 0) {
                        Platform.runLater(() -> labelVolume.setText(String.valueOf(vol)));
                    }
                }
                tmpDate = tmpDate.minusDays(1);
                tmpPtrList = StockData.getByDate(DateUtil.localDateToString(tmpDate));
            }
            QuantraLineChart volChart = QuantraLineChart.createFrom(tmpPtrs);
            volChart.addPath("总交易量", Color.LIGHTPINK, tmpVols);

            Platform.runLater(() -> {
                risingLimit.cleanListView();
                fallingLimit.cleanListView();
                risingOverFivePer.cleanListView();
                fallingOverFivePer.cleanListView();
                overLastFivePer.cleanListView();
                underLastFivePer.cleanListView();
                risingLimit.setListView(stockRisingLimit, risingLimitRate, date);
                fallingLimit.setListView(stockFallingLimit, fallingLimitRate, date);
                risingOverFivePer.setListView(stockRisingOverFivePer, risingOverFivePerRate, date);
                fallingOverFivePer.setListView(stockFallingOverFivePer, fallingOverFivePerRate, date);
                overLastFivePer.setListView(stockOverLastFivePer, overLastFivePerRate, date);
                underLastFivePer.setListView(stockUnderLastFivePer, underLastFivePerRate, date);

                labelDate.setText(dateParser(date));
                Format f = new DecimalFormat("#.##");
                int sum = Arrays.stream(cnts).sum();
                String[] tempTitles = {"上涨", "平盘", "下跌"};
                for (int i = 0; i < 3; i++) {
                    double pct = ((double) cnts[i] / sum) * 100;
                    ((Label) gridTemp.getChildren().get(i)).setText(tempTitles[i] + "（" + f.format(pct) + "%）");
                    if (pct < 15) pct = 15;
                    gridTemp.getColumnConstraints().get(i).setPercentWidth(pct);
                }
                paneVolChart.setCenter(volChart);
                UIContainer.hideLoading();
            });
        }).start();
    }

    class MarketMiniListVC extends BorderPane {
        @FXML
        private MaterialDesignIconView titleIcon;
        @FXML
        private Label labelTitle, labelCount, labelRateName;
        @FXML
        private JFXListView<GridPane> listView;

        MarketMiniListVC(boolean up, String title, List<StockInfo> infoList, List<Double> rateList) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/market_minilist.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            if (!up) {
                titleIcon.setGlyphName("TRENDING_DOWN");
            }
            labelTitle.setText(title);
            labelCount.setText("(" + infoList.size() + ")");
            listView.setOnMouseClicked(event -> {
                if (listView.getSelectionModel().getSelectedItem() != null) {
                    listView.getSelectionModel().getSelectedItem().getOnMouseClicked().handle(null);
                }
            });
            setListView(infoList, rateList, StockData.latest);
        }

        MarketMiniListVC(boolean up, String title) throws IOException {
            this(up, title, new ArrayList<>(), new ArrayList<>());
        }

        MarketMiniListVC(boolean up, String title, String rateName) throws IOException {
            this(up, title, new ArrayList<>(), new ArrayList<>());
            labelRateName.setText(rateName);
        }

        void setListView(List<StockInfo> infoList, List<Double> rateList, String date) {
            int n = infoList.size();
            labelCount.setText("(" + infoList.size() + ")");
            for (int i = 0; i < n; i++) {
                StockInfo stock = infoList.get(i);
                GridPane line = getLine(stock, rateList.get(i));
                line.setOnMouseClicked(event -> {
                    try {
                        UIContainer.loadContent(new StockVC(stock.getCode(), date));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                listView.getItems().add(line);
            }
        }

        void cleanListView() {
            listView.getItems().clear();
        }

        private GridPane getLine(StockInfo info, Double rate) {
            GridPane line = new GridPane();
            line.addColumn(0, getCenterLabel(String.format("%06d", info.getCode())));
            line.addColumn(1, getCenterLabel(info.getName()));
            line.addColumn(2, getCenterLabel(Float.toString(info.getClose())));
            line.addColumn(3, getCenterLabel(Math.floor(rate * 1000) / 10.0 + " %"));
            line.getColumnConstraints().setAll(getColumn(20), getColumn(30), getColumn(25), getColumn(25));
            line.setMouseTransparent(true);
            return line;
        }

        private ColumnConstraints getColumn(double precent) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(precent);
            return constraints;
        }

        private Label getCenterLabel(String text) {
            Label label = new Label(text);
            label.setPrefHeight(30);
            GridPane.setHalignment(label, HPos.CENTER);
            return label;
        }
    }

}
