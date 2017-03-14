package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by Lenovo on 2017/3/14.
 */
public class StockListVC extends Pane {

    @FXML
    private TableView<StockBaseProtos.StockBase.StockInfo> table;
    @FXML
    private JFXDatePicker datePicker;

    public StockListVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockList.fxml"));
        String[] props = {"code", "name", "close", "open", "high", "low", "volume", "market"};
        for (int i = 0; i < table.getColumns().size(); i++) {
            table.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(props[i]));
        }
        datePicker.valueProperty().addListener(observable -> updateInfo());
        datePicker.setValue(DateUtil.parseLocalDate(StockData.latest));
    }

    private void updateInfo() {
        List<StockBaseProtos.StockBase.StockInfo> list = StockData.getList();
        String date = DateUtil.localDateToString(datePicker.getValue());
        table.getItems().clear();
        for (int i = 0; i < StockData.size; i++) {
            if (list.get(i).getDate().equals(date)) {
                table.getItems().add(list.get(i));
            }
        }
    }

}
