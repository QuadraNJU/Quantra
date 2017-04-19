package nju.quadra.quantra.utils;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.ui.StockVC;
import nju.quadra.quantra.ui.UIContainer;

import java.io.IOException;

/**
 * Created by adn55 on 2017/4/18.
 */
public class StockTableUtil {

    public static void addColumn(TableView table, String title, Callback<TableColumn.CellDataFeatures<StockInfoPtr, Object>, ObservableValue<Object>> factory) {
        TableColumn<StockInfoPtr, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
    }

    public static void addColors(TableColumn column) {
        column.setCellFactory(col -> new TableCell<StockInfoPtr, Double>() {
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
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
    }

    public static void addDblClick(TableView<StockInfoPtr> table) {
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
    }

}
