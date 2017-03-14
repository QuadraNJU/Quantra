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
    public static List<Integer> chosenStocks = new ArrayList<>();
    @FXML
    private GridPane gridStocks;
    private static GridPane gridStocksS;

    public StockCompareVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockCompare.fxml"));
        gridStocksS = gridStocks;
        load();
    }

    public static int addToList(int code) {
        if(chosenStocks.size() < 2) {
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
            gridStocksS.add(new StockCompareItemVC(chosenStocks.get(i), "4/25/14", "5/1/14"), i, 0);
        }
    }

}
