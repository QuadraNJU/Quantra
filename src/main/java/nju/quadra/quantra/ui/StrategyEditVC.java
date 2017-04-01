package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import nju.quadra.quantra.data.StrategyData;
import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.strategy.PeriodStrategy;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;

/**
 * Created by adn55 on 2017/4/1.
 */
public class StrategyEditVC extends Pane {

    @FXML
    private JFXTextField editName, editFreq;
    @FXML
    private JFXComboBox<String> editPeriod;
    @FXML
    private JFXRadioButton radioMomentum, radioMeanReversion, radioCustom;
    @FXML
    private ToggleGroup typeGroup;
    @FXML
    private HBox boxPeriod;

    private AbstractStrategy strategy;

    public StrategyEditVC(AbstractStrategy strategy) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/strategyEdit.fxml"));
        typeGroup.selectedToggleProperty().addListener(o -> boxPeriod.setVisible(!radioCustom.isSelected()));

        this.strategy = strategy;
        if (strategy != null) {
            editName.setText(strategy.name);
            editFreq.setText(String.valueOf(strategy.freq));
            if (strategy instanceof PeriodStrategy) {
                editPeriod.setValue(String.valueOf(((PeriodStrategy) strategy).period));
            }
            switch (strategy.type) {
                case "momentum":
                    radioMomentum.fire();
                    break;
                case "mean_reversion":
                    radioMeanReversion.fire();
                    break;
                case "custom":
                    radioCustom.fire();
            }
        }
    }

    @FXML
    private void onSaveAction() throws IOException {
        String name = editName.getText().trim();
        String type = "";
        if (radioMomentum.isSelected()) {
            type = "momentum";
        } else if (radioMeanReversion.isSelected()) {
            type = "mean_reversion";
        } else if (radioCustom.isSelected()) {
            type = "custom";
        }
        int freq = -1;
        int period = -1;
        try {
            freq = Integer.parseInt(editFreq.getText());
            period = Integer.parseInt(editPeriod.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (name.isEmpty() || type.isEmpty() || freq <= 0 || (!type.equals("custom") && period <= 0)) {
            UIContainer.alert("错误", "策略信息不完整或有错误，请检查");
        } else {
            if (strategy != null) {
                StrategyData.removeStrategy(strategy);
            }
            switch (type) {
                case "momentum":
                case "mean_reversion":
                    StrategyData.addStrategy(new PeriodStrategy(name, type, freq, 0, period));
                case "custom":
                    // todo
            }
            onCancelAction();
        }
    }

    @FXML
    private void onCancelAction() throws IOException {
        UIContainer.loadContent(new StrategyListVC());
    }

}
