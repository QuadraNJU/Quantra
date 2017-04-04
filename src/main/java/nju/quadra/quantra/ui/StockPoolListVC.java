package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;

/**
 * Created by RaUkonn on 2017/4/4.
 */
public class StockPoolListVC extends Pane {
    @FXML
    private VBox vboxPool;

    public StockPoolListVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockPoolList.fxml"));
        updateStrategy();
    }

    public void updateStrategy() {
        for (CustomPool pool : CustomPool.createTotalCustomPoolList()) {
            try {
                vboxPool.getChildren().add(new StockPoolItemVC(pool));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onAddAction() throws IOException {
        new Thread(() -> {
            try {
                UIContainer.showLoading();
                UIContainer.loadContent(new PoolEditVC(this));
                UIContainer.hideLoading();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
