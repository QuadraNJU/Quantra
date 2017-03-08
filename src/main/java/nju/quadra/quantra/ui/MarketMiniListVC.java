package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXListView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import nju.quadra.quantra.data.StockBaseProtos;

import java.util.List;

/**
 * Created by adn55 on 2017/3/8.
 */
public class MarketMiniListVC extends Parent {

    @FXML
    private MaterialDesignIconView titleIcon;
    @FXML
    private Label labelTitle, labelCount;
    @FXML
    private JFXListView listView;

    public MarketMiniListVC(boolean up, String title, List<StockBaseProtos.StockBase.StockInfo> infoList) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/market_minilist.fxml"));
        loader.setController(this);
        getChildren().add(loader.load());

        if (!up) {
            titleIcon.setGlyphName("TRENDING_DOWN");
        }
        labelTitle.setText(title);
        labelCount.setText("(" + infoList.size() + ")");
        listView.getItems().add(new Label("test"));
    }

}
