package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.ui.chart.QuantraKChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by MECHREVO on 2017/3/10.
 */
public class StockVC extends Pane {

    @FXML
    private Label labelName, labelPrice, labelRate;
    @FXML
    private JFXDatePicker dateStart, dateEnd;
    @FXML
    private BorderPane paneK;
    @FXML
    private MaterialDesignIconView iconStar, iconPlus;

    private List<StockBaseProtos.StockBase.StockInfo> infoList;

    public StockVC(int code, String date) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stock.fxml"));
        infoList = StockData.getList().stream().filter(info -> info.getCode() == code).collect(Collectors.toList());
        labelName.setText(infoList.get(0).getName());
        dateStart.setDayCellFactory(DateUtil.dayCellFactory);
        dateEnd.setDayCellFactory(DateUtil.dayCellFactory);
        dateEnd.setValue(DateUtil.parseLocalDate(date));
        dateStart.setValue(dateEnd.getValue().minusDays(30));
        updateInfo();
        dateStart.valueProperty().addListener(observable -> updateInfo());
        dateEnd.valueProperty().addListener(observable -> updateInfo());
        iconPlus.setFill(StockCompareVC.chosenStocks.contains(code)? Color.RED: Color.valueOf("#eceff1"));
    }

    private void updateInfo() {
        LinkedList<StockBaseProtos.StockBase.StockInfo> linkList = new LinkedList<>();
        int size = infoList.size();
        for (int i = 0; i < size; i++) {
            if (infoList.get(i).getDate().equals(DateUtil.localDateToString(dateEnd.getValue()))) {
                labelPrice.setText(String.valueOf(infoList.get(i).getClose()));
                if (i < size - 1) {
                    labelRate.setText(new DecimalFormat("#.##").format((infoList.get(i).getAdjClose() - infoList.get(i + 1).getAdjClose()) / infoList.get(i).getAdjClose() * 100) + "%");
                } else {
                    labelRate.setText("-----");
                }
                while (i < size && DateUtil.parseLocalDate(infoList.get(i).getDate()).compareTo(dateStart.getValue()) >= 0) {
                    linkList.addFirst(infoList.get(i));
                    i++;
                }
            }
        }
        QuantraKChart kChart = QuantraKChart.createFrom(linkList);
        kChart.addPath("AdjClose", linkList.stream().map(StockBaseProtos.StockBase.StockInfo::getAdjClose).collect(Collectors.toList()));
        paneK.setCenter(kChart);
    }

    @FXML
    private void onShortcutAction(ActionEvent t) {
        dateStart.setValue(dateEnd.getValue().minusDays(Integer.parseInt(((JFXButton) t.getSource()).getText().replace("日", ""))));
    }

    @FXML
    private void onKChartScroll(ScrollEvent t) {
        LocalDate newDate = dateStart.getValue().plusDays(t.getDeltaY() < 0 ? -15 : 15);
        if (newDate.compareTo(dateEnd.getValue()) > 0) {
            newDate = dateEnd.getValue();
        }
        dateStart.setValue(newDate);
        t.consume();
    }

    @FXML
    private void onPlusClickedAction(MouseEvent t) {
        int code = infoList.get(0).getCode();
        CommonEventController.onPlusClickedEvent(t, code);
    }


}
