package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaUkonn on 2017/3/10.
 */
public class StockCompareVC extends Pane {
    public static List<Integer> chosenStocks = new ArrayList<>();
    @FXML
    private GridPane gridStocks;
    @FXML
    private JFXDatePicker pickerStart, pickerEnd;
    private static String dateStart, dateEnd;
    private static GridPane gridStocksS;

    public StockCompareVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockCompare.fxml"));
        gridStocksS = gridStocks;
        pickerStart.setDayCellFactory(DateUtil.dayCellFactory);
        pickerStart.setValue(DateUtil.parseLocalDate(StockData.latest).minusDays(15));
        pickerEnd.setDayCellFactory(DateUtil.dayCellFactory);
        pickerEnd.setValue(DateUtil.parseLocalDate(StockData.latest));
        dateStart = DateUtil.localDateToString(pickerStart.getValue());
        dateEnd = DateUtil.localDateToString(pickerEnd.getValue());
        load();
    }

    public static int addToList(int code) {
        if (chosenStocks.size() < 2) {
            chosenStocks.add(code);
            return 0;
        }
        return -1;
    }

    public static void removeFromList(int code) {
        chosenStocks.removeIf(u -> u == code);
    }

    public static void load() throws IOException {
        gridStocksS.getChildren().clear();
        for (int i = 0; i < chosenStocks.size(); i++) {
            gridStocksS.add(new StockCompareItemVC(chosenStocks.get(i), dateStart, dateEnd), i, 0);
        }
    }

    @FXML
    private void onPickerAction() throws IOException {
        LocalDate dateStart = pickerStart.getValue();
        LocalDate dateEnd = pickerEnd.getValue();
        if (dateStart.compareTo(dateEnd) > 0) {
            Tooltip t = new Tooltip("起始时间晚于结束时间，请重新选择");
            t.setAutoHide(true);
            pickerEnd.setValue(dateStart.plusDays(1));
            t.show(pickerStart, pickerStart.getLayoutX(), pickerStart.getLayoutY());
        }
        this.dateStart = DateUtil.localDateToString(pickerStart.getValue());
        this.dateEnd = DateUtil.localDateToString(pickerEnd.getValue());
        load();
    }
}
