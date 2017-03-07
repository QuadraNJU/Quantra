package nju.quadra.quantra.ui.controller;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaUkonn on 2017/3/7.
 */
public class MarketVC extends Parent {
    @FXML
    Label labelDate, labelRisingLimit, labelFallingLimit, labelRisingOverFivePer, labelFallingOverFivePer;
    @FXML
    Label labelOverLastFivePer, labelUnderLastFivePer;
    @FXML
    JFXDatePicker picker;
    @FXML
    JFXTreeTableView tableRising1, tableRising2, tableRising3;
    @FXML
    JFXTreeTableView tableFalling1, tableFalling2, tableFalling3;
    @FXML
    JFXTreeTableColumn columnRising1Code, columnRising1Name, columnRising1Close, columnRising1RisingRate;
    @FXML
    JFXTreeTableColumn columnRising2Code, columnRising2Name, columnRising2Close, columnRising2RisingRate;
    @FXML
    JFXTreeTableColumn columnRising3Code, columnRising3Name, columnRising3Close, columnRising3RisingRate;
    @FXML
    JFXTreeTableColumn columnFalling1Code, columnFalling1Name, columnFalling1Close, columnFalling1RisingRate;
    @FXML
    JFXTreeTableColumn columnFalling2Code, columnFalling2Name, columnFalling2Close, columnFalling2RisingRate;
    @FXML
    JFXTreeTableColumn columnFalling3Code, columnFalling3Name, columnFalling3Close, columnFalling3RisingRate;

    private String currentDate;

    public MarketVC() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("nju/quadra/quantra/ui/assets/market.fxml"));
        currentDate = StockData.getList().get(0).getDate();

        loadingLists(currentDate);
    }


    private String dateParser(String dateFromDataset) {
        String[] dateList = dateFromDataset.split("/");
        return "20" + dateList[2] + "/" + dateList[0] + "/" + dateList[1];
    }

    private void loadingLists(String date) {
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
                if(rate > 0.1) {
                    stockRisingLimit.add(curr);
                    risingLimitRate.add(rate);
                } else if(rate < -0.1) {
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

        labelRisingLimit.setText("(" + stockRisingLimit.size() + ")");
        labelFallingLimit.setText("(" + stockFallingLimit.size() + ")");
        labelFallingOverFivePer.setText("(" + stockFallingOverFivePer + ")");
        labelRisingOverFivePer.setText("(" + stockRisingOverFivePer + ")");
        labelOverLastFivePer.setText("(" + stockOverLastFivePer + ")");
        labelUnderLastFivePer.setText("(" + stockUnderLastFivePer + ")");

    }

    class StockItem extends RecursiveTreeObject<StockItem> {
        IntegerProperty code;
        StringProperty name;
        DoubleProperty close;
        DoubleProperty risingRate;

        public StockItem(int code, String name, double close, double risingRate) {
            this.code = new SimpleIntegerProperty(code);
            this.name = new SimpleStringProperty(name);
            this.close = new SimpleDoubleProperty(close);
            this.risingRate = new SimpleDoubleProperty(risingRate);
        }
    }
}
