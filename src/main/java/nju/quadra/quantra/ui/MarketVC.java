package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.utils.DateUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static nju.quadra.quantra.utils.DateUtil.localDateToString;

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
    private GridPane gridPane;
    private String currentDate;
    @FXML
    private Label labelDate;
    @FXML
    private JFXDatePicker picker;

    public MarketVC() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/market.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        currentDate = StockData.getList().get(0).getDate();
        gridPane.add(risingLimit, 0, 0);
        gridPane.add(fallingLimit, 1, 0);
        gridPane.add(risingOverFivePer, 0, 1);
        gridPane.add(fallingOverFivePer, 1, 1);
        gridPane.add(underLastFivePer, 0, 2);
        gridPane.add(overLastFivePer, 1, 2);
        labelDate.setText(dateParser(currentDate));
        loadingLists(currentDate);
        picker.setDayCellFactory(dayCellFactory);
        picker.setValue(DateUtil.parseLocalDate(currentDate));

    }


    private String dateParser(String dateFromDataset) {
        String[] dateList = dateFromDataset.split("/");
        return "20" + dateList[2] + "/" + dateList[0] + "/" + dateList[1];
    }

    private void loadingLists(String date) {
        risingLimit.cleanListView();
        fallingLimit.cleanListView();
        risingOverFivePer.cleanListView();
        fallingOverFivePer.cleanListView();
        overLastFivePer.cleanListView();
        underLastFivePer.cleanListView();

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

        StockBaseProtos.StockBase.StockInfo last = StockData.getList().get(0);
        for (int i = 1; i < StockData.size; i++) {
            StockBaseProtos.StockBase.StockInfo curr = last;
            last = StockData.getList().get(i);

            if (curr.getDate().equals(date) && last.getCode() == curr.getCode()) {
                double rate = (curr.getAdjClose() - last.getAdjClose()) / last.getAdjClose();
                double otherRate = (curr.getOpen() - curr.getClose()) / last.getClose();
                if (rate > 0.05) {
                    stockRisingOverFivePer.add(curr);
                    risingOverFivePerRate.add(rate);
                    if (rate >= 0.1) {
                        stockRisingLimit.add(curr);
                        risingLimitRate.add(rate);
                    }
                }

                if (rate < -0.05) {
                    stockFallingOverFivePer.add(curr);
                    fallingOverFivePerRate.add(rate);
                    if (rate <= -0.1) {
                        stockFallingLimit.add(curr);
                        fallingLimitRate.add(rate);
                    }
                }

                if (otherRate > 0.05) {
                    stockOverLastFivePer.add(curr);
                    overLastFivePerRate.add(otherRate);
                } else if (otherRate < -0.05) {
                    stockUnderLastFivePer.add(curr);
                    underLastFivePerRate.add(otherRate);
                }
            }
        }

        risingLimit.setListView(stockRisingLimit, risingLimitRate);
        fallingLimit.setListView(stockFallingLimit, fallingLimitRate);
        risingOverFivePer.setListView(stockRisingOverFivePer, risingOverFivePerRate);
        fallingOverFivePer.setListView(stockFallingOverFivePer, fallingOverFivePerRate);
        overLastFivePer.setListView(stockOverLastFivePer, overLastFivePerRate);
        underLastFivePer.setListView(stockUnderLastFivePer, underLastFivePerRate);
    }

    public void onActionDateChange() {
        LocalDate date = picker.getValue();
        currentDate = localDateToString(date);
        loadingLists(currentDate);
        labelDate.setText(dateParser(currentDate));
    }

    final Callback<DatePicker, DateCell> dayCellFactory =
            new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item.isAfter(picker.getValue())) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }
                            long p = ChronoUnit.DAYS.between(
                                    picker.getValue(), item
                            );
                        }
                    };
                }
            };

}
