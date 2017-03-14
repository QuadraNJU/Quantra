package nju.quadra.quantra.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import nju.quadra.quantra.data.StockBaseProtos;
import nju.quadra.quantra.data.StockData;
import nju.quadra.quantra.ui.chart.QuantraKChart;
import nju.quadra.quantra.utils.DateUtil;
import nju.quadra.quantra.utils.FXUtil;
import nju.quadra.quantra.utils.StockStatisticUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by MECHREVO on 2017/3/10.
 */
public class StockVC extends VBox {

    @FXML
    private Label labelName, labelPrice, labelRate;
    @FXML
    private JFXDatePicker dateStart, dateEnd;
    @FXML
    private BorderPane paneK;
    @FXML
    private MaterialDesignIconView iconStar, iconPlus;
    private static MaterialDesignIconView iconPlusS;

    private List<StockBaseProtos.StockBase.StockInfo> infoList;
    private int size;
    private QuantraKChart kChart;
    private ArrayList<String> hiddenMAList = new ArrayList<>();
    private static int code;

    public StockVC(int code, String date) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stock.fxml"));
        infoList = StockData.getList().stream().filter(info -> info.getCode() == code).collect(Collectors.toList());
        size = infoList.size();
        this.code = code;
        labelName.setText(infoList.get(0).getName());
        dateStart.setDayCellFactory(DateUtil.dayCellFactory);
        dateEnd.setDayCellFactory(DateUtil.dayCellFactory);
        dateEnd.setValue(DateUtil.parseLocalDate(date));
        dateStart.setValue(dateEnd.getValue().minusDays(30));
        updateInfo();
        dateStart.valueProperty().addListener(observable -> updateInfo());
        dateEnd.valueProperty().addListener(observable -> updateInfo());
        iconPlusS = iconPlus;
        setIconPlusColor();
    }

    public static void setIconPlusColor() {
        iconPlusS.setFill(StockCompareVC.chosenStocks.contains(code) ? Color.RED : Color.valueOf("#eceff1"));
    }

    private void updateInfo() {
        LinkedList<StockBaseProtos.StockBase.StockInfo> linkList = new LinkedList<>();
        LinkedList<Number> ma5List = new LinkedList<>();
        LinkedList<Number> ma10List = new LinkedList<>();
        LinkedList<Number> ma20List = new LinkedList<>();
        LinkedList<Number> ma30List = new LinkedList<>();
        LinkedList<Number> ma60List = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            if (infoList.get(i).getDate().equals(DateUtil.localDateToString(dateEnd.getValue()))) {
                labelPrice.setText(String.valueOf(infoList.get(i).getClose()));
                if (i < size - 1) {
                    double rate = (infoList.get(i).getAdjClose() - infoList.get(i + 1).getAdjClose()) / infoList.get(i).getAdjClose() * 100;
                    labelRate.setText(new DecimalFormat("#.##").format(Math.abs(rate)) + "%");
                    if (rate < 0) {
                        labelRate.getGraphic().setRotate(180);
                    }
                } else {
                    labelRate.setText("-----");
                }
                while (i < size && DateUtil.parseLocalDate(infoList.get(i).getDate()).compareTo(dateStart.getValue()) >= 0) {
                    linkList.addFirst(infoList.get(i));
                    ma5List.addFirst(MA(i, 5));
                    ma10List.addFirst(MA(i, 10));
                    ma20List.addFirst(MA(i, 20));
                    ma30List.addFirst(MA(i, 30));
                    ma60List.addFirst(MA(i, 60));
                    i++;
                }
            }
        }
        kChart = QuantraKChart.createFrom(linkList);
        kChart.addPath("MA5", Color.WHITE, ma5List);
        kChart.addPath("MA10", Color.YELLOW, ma10List);
        kChart.addPath("MA20", Color.LIGHTPINK, ma20List);
        kChart.addPath("MA30", Color.LIGHTGREEN, ma30List);
        kChart.addPath("MA60", Color.LIGHTBLUE, ma60List);
        paneK.setCenter(kChart);
    }


    private Double MA(int startPos, int days) {
        if (startPos + days <= infoList.size()) {
            return StockStatisticUtil.SMA(infoList.subList(startPos, startPos + days));
        } else {
            return null;
        }
    }

    @FXML
    private void onShortcutAction(ActionEvent t) {
        dateStart.setValue(dateEnd.getValue().minusDays(Integer.parseInt(((JFXButton) t.getSource()).getText().replace("日", ""))));
    }

    @FXML
    private void onMAToggle(ActionEvent t) {
        JFXToggleButton toggle = (JFXToggleButton) t.getSource();
        if (toggle.isSelected()) {
            hiddenMAList.remove(toggle.getText());
        } else {
            hiddenMAList.add(toggle.getText());
        }
        kChart.setHiddenPaths(hiddenMAList);
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
