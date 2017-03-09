package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXListView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import nju.quadra.quantra.data.StockBaseProtos;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;

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

    public MarketMiniListVC(boolean up, String title, boolean isFormerRate) throws IOException {
        this(up, title, new ArrayList<>(), new ArrayList<>());
        if(isFormerRate) labelRateName.setText("上期指数");
    }

    public void setListView(List<StockBaseProtos.StockBase.StockInfo> infoList, List<Double> rateList) {
        int n = infoList.size();
        if (n == 0) return;
        for (int i = 0; i < n; i++) {
            listView.getItems().add(getLine(infoList.get(i), rateList.get(i)));
        }
        labelCount.setText("(" + infoList.size() + ")");
    }

    private GridPane getLine(StockBaseProtos.StockBase.StockInfo info, Double rate) {
        GridPane line = new GridPane();
        line.addColumn(0, new Label(Integer.toString(info.getCode())));
        line.addColumn(1, new Label(info.getName()));
        line.addColumn(2, new Label(Float.toString(info.getClose())));
        line.addColumn(3, new Label(String.valueOf(((int)(rate*100))/100.0)));
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        line.getColumnConstraints().setAll(column, column, column, column);
        line.setMouseTransparent(true);
        return line;
    }

}
