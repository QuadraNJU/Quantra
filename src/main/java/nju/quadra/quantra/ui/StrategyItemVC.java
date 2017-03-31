package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;

/**
 * Created by MECHREVO on 2017/3/30.
 */
public class StrategyItemVC extends HBox{
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelSubTitle;

    private AbstractStrategy strategy;

    public StrategyItemVC(AbstractStrategy strategy) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/strategy.fxml"));
        this.strategy =strategy;
        labelTitle.setText(strategy.name);
        labelSubTitle.setText(strategy.getDescription());
    }

    @FXML
    private void onBackTestAction() throws IOException {
        UIContainer.loadContent(new BackTestVC(strategy));
    }
}
