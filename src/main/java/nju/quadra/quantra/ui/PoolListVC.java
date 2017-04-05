package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nju.quadra.quantra.data.StockPoolData;
import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;

/**
 * Created by RaUkonn on 2017/4/4.
 */
public class PoolListVC extends Pane {
    @FXML
    private VBox vboxPool;

    public PoolListVC() throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/poolList.fxml"));
        updatePoolList();
    }

    public void updatePoolList() {
        for (CustomPool pool : StockPoolData.getPoolMap().values()) {
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

    class StockPoolItemVC extends HBox {
        @FXML
        private Label labelTitle;

        private CustomPool pool;

        public StockPoolItemVC(CustomPool pool) throws IOException {
            FXUtil.loadFXML(this, getClass().getResource("assets/poolItem.fxml"));
            this.pool = pool;
            labelTitle.setText(pool.name);
        }

        @FXML
        private void onModifyAction() throws IOException {
            UIContainer.loadContent(new PoolEditVC(this, pool));
        }

        @FXML
        private void onDeleteAction() throws IOException {
            UIContainer.confirm("确认", "确认删除这个股池吗？", t -> {
                StockPoolData.removePool(pool);
                try {
                    UIContainer.loadContent(new PoolListVC());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
