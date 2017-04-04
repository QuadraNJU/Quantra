package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXDialog;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import nju.quadra.quantra.pool.CustomPool;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;


/**
 * Created by RaUkonn on 2017/4/4.
 */
public class SmallPoolItemVC extends HBox {
    @FXML
    private Label labelName;
    private int code;
    private CustomPool pool;
    private JFXDialog parent;

    public SmallPoolItemVC(CustomPool pool, int code) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/smallPoolItem.fxml"));
        this.pool = pool;
        this.code = code;
        labelName.setText(pool.name);
    }

    public void setParent(JFXDialog parent) {
        this.parent = parent;
    }

    @FXML
    private void onMouseEnteredAction() {
        this.setStyle("-fx-background-color: red");
    }

    @FXML
    private void onMouseExitedAction() {
        this.setStyle("-fx-background-color: transparent");
    }

    @FXML
    private void onMouseClickedAction() {
        if (pool.addStock(code)) {
            UIContainer.alert("提示", "股票添加成功");
            parent.close();
        } else {
            UIContainer.alert("提示", "该股票已经在所选股池中，不必重复添加");
        }
    }
}
