package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import nju.quadra.quantra.data.StrategyData;
import nju.quadra.quantra.strategy.AbstractStrategy;
import nju.quadra.quantra.strategy.CustomStrategy;
import nju.quadra.quantra.strategy.PeriodStrategy;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.PythonHighlighter;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by adn55 on 2017/4/1.
 */
public class StrategyEditVC extends BorderPane {

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
    @FXML
    private CodeArea codeArea;

    private AbstractStrategy strategy;

    public StrategyEditVC(AbstractStrategy strategy) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/strategyEdit.fxml"));
        typeGroup.selectedToggleProperty().addListener(o -> {
            boxPeriod.setVisible(!radioCustom.isSelected());
            codeArea.setVisible(radioCustom.isSelected());
        });
        editPeriod.getItems().setAll("5", "10", "20");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges().subscribe(change -> {
            codeArea.setStyleSpans(0, PythonHighlighter.compute(codeArea.getText()));
        });
        typeGroup.selectToggle(radioCustom);

        this.strategy = strategy;
        if (strategy != null) {
            editName.setText(strategy.name);
            editFreq.setText(String.valueOf(strategy.freq));
            if (strategy instanceof PeriodStrategy) {
                editPeriod.setValue(String.valueOf(((PeriodStrategy) strategy).period));
            }
            switch (strategy.type) {
                case "momentum":
                    typeGroup.selectToggle(radioMomentum);
                    break;
                case "mean_reversion":
                    typeGroup.selectToggle(radioMeanReversion);
                    break;
                case "custom":
                    typeGroup.selectToggle(radioCustom);
                    codeArea.replaceText(strategy.getCode());
            }
        } else {
            typeGroup.selectToggle(radioMomentum);
            try {
                InputStream is = getClass().getResourceAsStream("../python/template.py");
                byte[] buf = new byte[is.available()];
                is.read(buf);
                is.close();
                codeArea.replaceText(new String(buf, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
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
                    break;
                case "custom":
                    StrategyData.addStrategy(new CustomStrategy(name, freq, 0, codeArea.getText()));
            }
            onCancelAction();
        }
    }

    @FXML
    private void onCancelAction() throws IOException {
        UIContainer.loadContent(new StrategyListVC());
    }

}
