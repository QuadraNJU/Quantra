package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXListView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import nju.quadra.quantra.data.StockBaseProtos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adn55 on 2017/3/8.
 */
public class MarketMiniListVC extends BorderPane {

    @FXML
    private MaterialDesignIconView titleIcon;
    @FXML
    private Label labelTitle, labelCount, labelRateName;
    @FXML
    private JFXListView listView;

    public MarketMiniListVC(boolean up, String title, List<StockBaseProtos.StockBase.StockInfo> infoList, List<Double> rateList) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/market_minilist.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        if (!up) {
            titleIcon.setGlyphName("TRENDING_DOWN");
        }
        labelTitle.setText(title);
        labelCount.setText("(" + infoList.size() + ")");
        setListView(infoList, rateList);
    }

    public MarketMiniListVC(boolean up, String title) throws IOException {
        this(up, title, new ArrayList<>(), new ArrayList<>());
    }

    public MarketMiniListVC(boolean up, String title, String rateName) throws IOException {
        this(up, title, new ArrayList<>(), new ArrayList<>());
        labelRateName.setText(rateName);
    }

    public void setListView(List<StockBaseProtos.StockBase.StockInfo> infoList, List<Double> rateList) {
        int n = infoList.size();
        labelCount.setText("(" + infoList.size() + ")");
        for (int i = 0; i < n; i++) {
            listView.getItems().add(getLine(infoList.get(i), rateList.get(i)));
        }
    }

    public void cleanListView() {
        listView.getItems().clear();
    }

    private GridPane getLine(StockBaseProtos.StockBase.StockInfo info, Double rate) {
        GridPane line = new GridPane();
        line.addColumn(0, getCenterLabel(String.format("%06d", info.getCode())));
        line.addColumn(1, getCenterLabel(info.getName()));
        line.addColumn(2, getCenterLabel(Float.toString(info.getClose())));
        line.addColumn(3, getCenterLabel(Math.floor(rate * 1000) / 10.0 + " %"));
        line.getColumnConstraints().setAll(getColumn(20), getColumn(30), getColumn(25), getColumn(25));
        line.setMouseTransparent(true);
        return line;
    }

    private ColumnConstraints getColumn(double precent) {
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPercentWidth(precent);
        return constraints;
    }

    private Label getCenterLabel(String text) {
        Label label = new Label(text);
        label.setPrefHeight(30);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

}
