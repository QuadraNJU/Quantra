package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaUkonn on 2017/3/7.
 */
public class MarketVC extends Pane {

    private MarketMiniListVC risingLimit = new MarketMiniListVC(true, "今日涨停");
    private MarketMiniListVC fallingLimit = new MarketMiniListVC(false, "今日跌停");
    private MarketMiniListVC risingOverFivePer = new MarketMiniListVC(true, "上涨超5%");
    private MarketMiniListVC fallingOverFivePer = new MarketMiniListVC(false, "下跌超5%");
    private MarketMiniListVC overLastFivePer = new MarketMiniListVC(true, "上期指数超5%", true);
    private MarketMiniListVC underLastFivePer = new MarketMiniListVC(false, "上期指数超-5%", true);
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
        gridPane.add(fallingLimit, 0, 1);
        gridPane.add(risingOverFivePer, 1, 0);
        gridPane.add(fallingOverFivePer, 1, 1);
        gridPane.add(overLastFivePer, 2, 0);
        gridPane.add(underLastFivePer, 2, 1);
        labelDate.setText(dateParser(currentDate));
        loadingLists(currentDate);
        picker.setPromptText(dateParser(currentDate));

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
        for(int i = 0; i < StockData.getList().size() - 1; i++) {
            StockBaseProtos.StockBase.StockInfo curr = StockData.getList().get(i);
            StockBaseProtos.StockBase.StockInfo last = StockData.getList().get(i+1);

            if(curr.getDate().equals(date) && last.getCode() == curr.getCode()) {
                double rate = (curr.getClose() - last.getClose()) / last.getClose();
                double otherRate = (curr.getOpen() - curr.getClose()) / last.getClose();
                if(rate >= 0.1) {
                    stockRisingLimit.add(curr);
                    risingLimitRate.add(rate);
                } else if(rate <= -0.1) {
                    stockFallingLimit.add(curr);
                    fallingLimitRate.add(rate);
                } else if(rate > 0.05) {
                    stockRisingOverFivePer.add(curr);
                    risingOverFivePerRate.add(rate);
                } else if(rate < -0.05) {
                    stockFallingOverFivePer.add(curr);
                    fallingOverFivePerRate.add(rate);
                }
                if(otherRate > 0.05) {
                    stockOverLastFivePer.add(curr);
                    overLastFivePerRate.add(otherRate);
                }else if(otherRate < -0.05) {
                    stockUnderLastFivePer.add(curr);
                    underLastFivePerRate.add(otherRate);
                }
            }
        }

        risingLimit.setListView(stockRisingLimit, risingLimitRate);
        fallingLimit.setListView(stockFallingLimit, fallingLimitRate);
        risingOverFivePer.setListView(stockRisingOverFivePer, risingOverFivePerRate);
        fallingOverFivePer.setListView(stockFallingLimit, fallingOverFivePerRate);
        overLastFivePer.setListView(stockOverLastFivePer, overLastFivePerRate);
        underLastFivePer.setListView(stockUnderLastFivePer, underLastFivePerRate);
    }

    public void onActionDateChange() {
        LocalDate date = picker.getValue();
        currentDate = localDateToString(date);
        loadingLists(currentDate);
        labelDate.setText(dateParser(currentDate));
    }

    private String localDateToString(LocalDate date) {
        return String.valueOf(date.getMonthValue())
                + '/' + String.valueOf(date.getDayOfMonth())
                + '/' + String.valueOf(date.getYear()).substring(2, 4);
    }

}
