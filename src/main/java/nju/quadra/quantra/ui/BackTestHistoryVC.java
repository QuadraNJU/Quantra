package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import nju.quadra.quantra.data.BackTestHistory;
import nju.quadra.quantra.data.BackTestHistoryData;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.List;

public class BackTestHistoryVC extends BorderPane {
    @FXML
    private TableView<BackTestHistory> tableBackTestHistory;

    public BackTestHistoryVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/backTestHistory.fxml"));
        List<TableColumn<BackTestHistory, ?>> cols = tableBackTestHistory.getColumns();
        cols.get(0).setCellValueFactory(new PropertyValueFactory<>("time"));
        cols.get(1).setCellValueFactory(new PropertyValueFactory<>("strategyDescription"));
        cols.get(2).setCellValueFactory(new PropertyValueFactory<>("poolName"));
        cols.get(3).setCellValueFactory(new PropertyValueFactory<>("relativeRate"));
        tableBackTestHistory.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                BackTestHistory selected = tableBackTestHistory.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        UIContainer.loadContent(new BackTestVC(selected));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        List<BackTestHistory> histories = BackTestHistoryData.getBackTestHistories();
        for (int i = histories.size() - 1; i >= 0; i--) {
            tableBackTestHistory.getItems().add(histories.get(i));
        }
    }

    @FXML
    private void onBackAction() throws IOException {
        UIContainer.loadContent(new StrategyListVC());
    }
}
