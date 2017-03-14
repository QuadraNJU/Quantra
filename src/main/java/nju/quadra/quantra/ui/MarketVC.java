package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Label labelDate;
    @FXML
    private JFXDatePicker picker;

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
        picker.setValue(DateUtil.parseLocalDate(StockData.getList().get(0).getDate()));
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
            List<StockBaseProtos.StockBase.StockInfo> stockRisingLimit = new ArrayList<>();
            List<Double> risingLimitRate = new ArrayList<>();
            List<StockBaseProtos.StockBase.StockInfo> stockFallingLimit = new ArrayList<>();
            List<Double> fallingLimitRate = new ArrayList<>();
            List<StockBaseProtos.StockBase.StockInfo> stockRisingOverFivePer = new ArrayList<>();
            List<Double> risingOverFivePerRate = new ArrayList<>();
            List<StockBaseProtos.StockBase.StockInfo> stockFallingOverFivePer = new ArrayList<>();
            List<Double> fallingOverFivePerRate = new ArrayList<>();
            List<StockBaseProtos.StockBase.StockInfo> stockOverLastFivePer = new ArrayList<>();
            List<Double> overLastFivePerRate = new ArrayList<>();
            List<StockBaseProtos.StockBase.StockInfo> stockUnderLastFivePer = new ArrayList<>();
            List<Double> underLastFivePerRate = new ArrayList<>();

            for (StockInfoPtr ptr : StockData.getByDate(date)) {
                if (ptr.getYesterday() != null) {
                    double rate = StockStatisticUtil.RATE(ptr);
                    double otherRate = (ptr.getToday().getOpen() - ptr.getToday().getClose()) / ptr.getYesterday().getClose();
                    if (rate > 0) {
                        if (rate > 0.05) {
                            stockRisingOverFivePer.add(ptr.getToday());
                            risingOverFivePerRate.add(rate);
                            if (rate >= 0.1) {
                                stockRisingLimit.add(ptr.getToday());
                                risingLimitRate.add(rate);
                            }
                        }
                        cnts[0]++;
                    } else if (rate < 0) {
                        if (rate < -0.05) {
                            stockFallingOverFivePer.add(ptr.getToday());
                            fallingOverFivePerRate.add(rate);
                            if (rate <= -0.1) {
                                stockFallingLimit.add(ptr.getToday());
                                fallingLimitRate.add(rate);
                            }
                        }
                        cnts[2]++;
                    } else {
                        cnts[1]++;
                    }

                    if (otherRate > 0.05) {
                        stockOverLastFivePer.add(ptr.getToday());
                        overLastFivePerRate.add(otherRate);
                    } else if (otherRate < -0.05) {
                        stockUnderLastFivePer.add(ptr.getToday());
                        underLastFivePerRate.add(otherRate);
                    }
                }
            }

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
                UIContainer.hideLoading();
            });
        }).start();
    }

}
