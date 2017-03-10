package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.ui.chart.QuantraKChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by MECHREVO on 2017/3/10.
 */
public class StockVC extends Pane{

    @FXML
    private Label labelName,labelPrice,labelRate;
    @FXML
    private JFXDatePicker dateStart,dateEnd;
    @FXML
    private BorderPane paneK;

    private StockBaseProtos.StockBase.StockInfo stockInfo;

    public StockVC(StockBaseProtos.StockBase.StockInfo stockInfo, String date) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stock.fxml"));
        this.stockInfo = stockInfo;
        labelName.setText(stockInfo.getName());
        labelPrice.setText(String.valueOf(stockInfo.getClose()));
        dateEnd.setValue(DateUtil.parseLocalDate(date));
        dateStart.setValue(dateEnd.getValue().minusDays(30));
        updateRate();
        dateStart.valueProperty().addListener(observable -> updateRate());
        dateEnd.valueProperty().addListener(observable -> updateRate());
        dateStart.setDayCellFactory(DateUtil.dayCellFactory);
        dateEnd.setDayCellFactory(DateUtil.dayCellFactory);
    }

    private void updateRate(){
        List<StockBaseProtos.StockBase.StockInfo> stockList = StockData.getList();
        LinkedList<StockBaseProtos.StockBase.StockInfo> linkList = new LinkedList<>();
        for (int i = 0; i < StockData.size-1; i++) {
            StockBaseProtos.StockBase.StockInfo stock = stockList.get(i);
            if(stock.getCode() == stockInfo.getCode()){
                LocalDate localDate = DateUtil.parseLocalDate(stock.getDate());
                if(localDate.compareTo(dateStart.getValue()) >=0 && localDate.compareTo(dateEnd.getValue()) <= 0) {
                    linkList.addFirst(stock);
                }
                if(stock.getSerial() == stockInfo.getSerial()) {
                    stock = stockList.get(i + 1);
                    if (stock.getCode() == stockInfo.getCode()) {
                        labelRate.setText(new DecimalFormat("#.##").format((stockInfo.getAdjClose() - stock.getAdjClose()) / stock.getAdjClose() * 100) + "%");
                    }
                }
            }
        }
        paneK.setCenter(QuantraKChart.createFrom(linkList));
    }

    @FXML
    private void onShortcutAction(ActionEvent t) {
        dateStart.setValue(dateEnd.getValue().minusDays(Integer.parseInt(((JFXButton) t.getSource()).getText().replace("日", ""))));
    }

}
