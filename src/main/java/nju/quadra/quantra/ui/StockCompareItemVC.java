package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.ui.chart.RisingAndFallingChart;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.util.List;


/**
 * Created by RaUkonn on 2017/3/10.
 */
public class StockCompareItemVC extends FlowPane {
    @FXML
    private Label labelName;
    @FXML
    private FlowPane flowCharts;

    public StockCompareItemVC(int code) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockCompareItem.fxml"));

        List<StockBaseProtos.StockBase.StockInfo> list = StockData.getByCode(code);
        labelName.setText(list.get(0).getName());
        flowCharts.getChildren().add(RisingAndFallingChart.createFrom(list));
        flowCharts.getChildren().add(RisingAndFallingChart.createFrom(list));
    }
}
