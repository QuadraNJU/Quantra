package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;

import static nju.quadra.quantra.utils.StockTableUtil.addColors;
import static nju.quadra.quantra.utils.StockTableUtil.addColumn;
import static nju.quadra.quantra.utils.StockTableUtil.addDblClick;

/**
 * Created by Lenovo on 2017/3/14.
 */
public class StockListVC extends BorderPane {

    @FXML
    private TableView<StockInfoPtr> table;
    @FXML
    private JFXDatePicker datePicker;

    public StockListVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockList.fxml"));

        addColumn(table, "代码", param -> new ReadOnlyObjectWrapper<>(String.format("%06d", param.getValue().get().getCode())));
        addColumn(table, "名称", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getName()));
        addColumn(table, "涨幅(%)", param -> new ReadOnlyObjectWrapper<>(Math.floor(StockStatisticUtil.RATE(param.getValue()) * 10000) / 100.0));
        addColumn(table, "今收", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getClose()));
        addColumn(table, "交易量", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getVolume()));
        addColumn(table, "今开", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getOpen()));
        addColumn(table, "最高", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getHigh()));
        addColumn(table, "最低", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getLow()));
        addColumn(table, "昨收", param -> new ReadOnlyObjectWrapper<>(param.getValue().prev() != null ? param.getValue().prev().get().getClose() : ""));
        addColors(table.getColumns().get(2));
        addDblClick(table);

        datePicker.valueProperty().addListener(observable -> updateInfo());
        datePicker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));
    }

    private void updateInfo() {
        String date = DateUtil.localDateToString(datePicker.getValue());
        table.getItems().setAll(StockData.getByDate(date));
        if (table.getItems().isEmpty()) {
            datePicker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));
            UIContainer.alert("错误", "当天无股票数据，请选择其它日期");
        } else {
            DateUtil.currentDate = date;
        }
    }

}
