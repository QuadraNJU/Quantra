package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.List;

import static nju.quadra.quantra.ui.StockCompareVC.removeFromList;
import static nju.quadra.quantra.ui.UIContainer.loadCompareList;
import static nju.quadra.quantra.ui.UIContainer.paneCompareListS;

/**
 * Created by RaUkonn on 2017/3/14.
 */
public class CompareSmallWindowItemVC extends HBox {
    @FXML
    Label labelCode, labelName;
    private int code;

    public CompareSmallWindowItemVC(int code) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/compareSmallWindowItem.fxml"));
        List<StockInfoPtr> list = StockData.getByCode(code);
        labelName.setText(list.get(0).get().getName());
        labelCode.setText(String.format("%06d", list.get(0).get().getCode()));
        this.code = code;
    }

    @FXML
    private void onDeleteAction() throws IOException {
        removeFromList(code);
        loadCompareList();
        StockVC.setIconPlusColor();
        StockCompareVC.load();
    }
}
