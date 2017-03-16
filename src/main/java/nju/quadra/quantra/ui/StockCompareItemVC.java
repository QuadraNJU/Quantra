package nju.quadra.quantra.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.ui.chart.LowHighChart;
import nju.quadra.quantra.ui.chart.RisingAndFallingChart;
import nju.quadra.quantra.ui.chart.VolumeChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;


/**
 * Created by RaUkonn on 2017/3/10.
 */
public class StockCompareItemVC extends VBox {
    @FXML
    private Label labelName, /*labelMax, labelMin, */labelLogReturnVar;
    @FXML
    private VBox paneCharts;
    @FXML
    private static int code;
    private String dateStart, dateEnd;

    public StockCompareItemVC(int code, String dateStart, String dateEnd) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stockCompareItem.fxml"));

        List<StockBaseProtos.StockBase.StockInfo> list = StockData.getByCode(code);
        //初始日期直接掩盖掉实际的第一天好了，便于计算
        int startIndex = list.size() - 2, endIndex = 0;
        String listStartDate = list.get(startIndex).getDate();
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        if(DateUtil.compare(list.get(0).getDate(), dateEnd) < 0)
            this.dateEnd = list.get(0).getDate();
        if(DateUtil.compare(listStartDate, dateStart) > 0)
            this.dateStart = dateStart;

        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getDate().equals(this.dateEnd)) {
                endIndex = i;
                break;
            }
        }
        for(int i = endIndex; i < list.size(); i++) {
            if(list.get(i).getDate().equals(this.dateStart)) {
                startIndex = i + 1; //找到所选那天的数据，还要往前再取一天
                break;
            }
        }

        list = list.subList(endIndex, startIndex + 1);
        labelName.setText(list.get(0).getName());
        paneCharts.getChildren().add(RisingAndFallingChart.createFrom(list));
        paneCharts.getChildren().add(VolumeChart.createFrom(list));
        paneCharts.getChildren().add(LowHighChart.createFrom(list, true));
        paneCharts.getChildren().add(LowHighChart.createFrom(list, false));
        this.code = code;

        Format f = new DecimalFormat("#.##");

        //因为实际取了n+1天，所以计算最值需要把第n+1天剔除
//        labelMax.setText(f.format(list.subList(0, list.size() - 1)
//                .stream()
//                .mapToDouble(u -> u.getAdjClose())
//                .max()
//                .getAsDouble()));
//        labelMin.setText(f.format(list.subList(0, list.size() - 1)
//                .stream()
//                .mapToDouble(u -> u.getAdjClose())
//                .min()
//                .getAsDouble()));
        labelLogReturnVar.setText(f.format(StockStatisticUtil.DAILY_LOG_RETURN_VAR(list) * 100) + "%");

    }

    @FXML
    private void onPlusClickedAction(MouseEvent t) throws IOException{
        CommonEventController.onPlusClickedEvent(t, code);
        StockCompareVC.load();
    }


}
