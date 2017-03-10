package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RaUkonn on 2017/3/10.
 */
public class StockCompareVC extends Pane {
    private static List<Integer> chosenStocks = new ArrayList<>();
    @FXML
    private GridPane gridStocks;

    public StockCompareVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockCompare.fxml"));
        load();
    }

    public static void addToList(int code) {
        chosenStocks.add(code);
    }

    private void load() throws IOException {
        gridStocks.getChildren().clear();
        for (int i = 0; i < chosenStocks.size(); i++) {
            gridStocks.add(new StockCompareItemVC(chosenStocks.get(i)), i, 0);
        }
    }

}
