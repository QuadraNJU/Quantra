package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;

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

        addColumn("代码", param -> new ReadOnlyObjectWrapper<>(String.format("%06d", param.getValue().get().getCode())));
        addColumn("名称", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getName()));
        addColumn("涨幅(%)", param -> new ReadOnlyObjectWrapper<>(Math.floor(StockStatisticUtil.RATE(param.getValue()) * 10000) / 100.0));
        addColumn("今收", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getClose()));
        addColumn("交易量", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getVolume()));
        addColumn("今开", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getOpen()));
        addColumn("最高", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getHigh()));
        addColumn("最低", param -> new ReadOnlyObjectWrapper<>(param.getValue().get().getLow()));
        addColumn("昨收", param -> new ReadOnlyObjectWrapper<>(param.getValue().prev() != null ? param.getValue().prev().get().getClose() : ""));
        ((TableColumn<StockInfoPtr, Double>) table.getColumns().get(2)).setCellFactory(column -> new TableCell<StockInfoPtr, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                TableRow row = getTableRow();
                if (!empty && item != null && row != null) {
                    setText(String.valueOf(item));
                    row.getStyleClass().removeAll("red", "green");
                    if (item >= 0.01) {
                        row.getStyleClass().add("red");
                    } else if (item <= -0.01) {
                        row.getStyleClass().add("green");
                    }
                }
            }
        });

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                StockInfoPtr selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        UIContainer.loadContent(new StockVC(selected.get().getCode(), selected.get().getDate()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        datePicker.valueProperty().addListener(observable -> updateInfo());
        datePicker.setValue(DateUtil.parseLocalDate(DateUtil.currentDate));
    }

    private void addColumn(String title, Callback<TableColumn.CellDataFeatures<StockInfoPtr, Object>, ObservableValue<Object>> factory) {
        TableColumn<StockInfoPtr, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
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
