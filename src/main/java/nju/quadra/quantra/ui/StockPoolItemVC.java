package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;

/**
 * Created by RaUkonn on 2017/4/4.
 */
public class StockPoolItemVC extends HBox {
    @FXML
    private Label labelTitle;

    private CustomPool pool;

    public StockPoolItemVC(CustomPool pool) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockPoolItem.fxml"));
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
            if (!pool.removePool()) {
                UIContainer.alert("警告", "数据错误，请于刷新后重试");
            }
            try {
                UIContainer.loadContent(new StockPoolListVC());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
