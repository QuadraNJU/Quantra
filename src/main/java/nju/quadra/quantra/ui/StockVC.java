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
import nju.quadra.quantra.data.StockInfoPtr;
import nju.quadra.quantra.ui.chart.QuantraBarChart;
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

/**
 * Created by MECHREVO on 2017/3/10.
 */
public class StockVC extends VBox {

    @FXML
    private Label labelName, labelPrice, labelRate;
    @FXML
    private JFXDatePicker dateStart, dateEnd;
    @FXML
    private BorderPane paneK, paneEx;
    @FXML
    private MaterialDesignIconView iconStar, iconPlus;
    private static MaterialDesignIconView iconPlusS;

    private List<StockInfoPtr> infoList;
    private int size;
    private QuantraKChart kChart;
    private QuantraBarChart volChart;
    private ArrayList<String> hiddenMAList = new ArrayList<>();
    private static int code;

    public StockVC(int code, String date) throws IOException {
        FXUtil.loadFXML(this, getClass().getResource("assets/stock.fxml"));
        infoList = StockData.getPtrByCode(code);
        size = infoList.size();
        StockVC.code = code;
        labelName.setText(infoList.get(0).getToday().getName());
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
        if (dateStart.getValue().compareTo(dateEnd.getValue()) >= 0) {
            dateStart.setValue(dateEnd.getValue().minusDays(1));
        }
        LinkedList<StockBaseProtos.StockBase.StockInfo> linkList = new LinkedList<>();
        LinkedList<Number> ma5List = new LinkedList<>();
        LinkedList<Number> ma10List = new LinkedList<>();
        LinkedList<Number> ma20List = new LinkedList<>();
        LinkedList<Number> ma30List = new LinkedList<>();
        LinkedList<Number> ma60List = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            if (DateUtil.parseLocalDate(infoList.get(i).getToday().getDate()).compareTo(dateEnd.getValue()) <= 0) {
                labelPrice.setText(String.valueOf(infoList.get(i).getToday().getClose()));
                if (infoList.get(i).getYesterday() != null) {
                    double rate = StockStatisticUtil.RATE(infoList.get(i));
                    labelRate.setText(new DecimalFormat("#.##").format(Math.abs(rate * 100)) + "%");
                    if (rate < 0) {
                        labelRate.getGraphic().setRotate(180);
                    }
                } else {
                    labelRate.setText("-----");
                }
                while (i < size && DateUtil.parseLocalDate(infoList.get(i).getToday().getDate()).compareTo(dateStart.getValue()) >= 0) {
                    linkList.addFirst(infoList.get(i).getToday());
                    ma5List.addFirst(MA(i, 5));
                    ma10List.addFirst(MA(i, 10));
                    ma20List.addFirst(MA(i, 20));
                    ma30List.addFirst(MA(i, 30));
                    ma60List.addFirst(MA(i, 60));
                    i++;
                }
                break;
            }
        }
        if (!linkList.isEmpty()) {
            kChart = QuantraKChart.createFrom(linkList);
            kChart.addPath("MA5", Color.WHITE, ma5List);
            kChart.addPath("MA10", Color.YELLOW, ma10List);
            kChart.addPath("MA20", Color.LIGHTPINK, ma20List);
            kChart.addPath("MA30", Color.LIGHTGREEN, ma30List);
            kChart.addPath("MA60", Color.LIGHTBLUE, ma60List);
            paneK.setCenter(kChart);
            volChart = QuantraBarChart.createFrom(linkList, "成交量");
            paneEx.setCenter(volChart);
        } else {
            dateEnd.setValue(dateEnd.getValue().minusDays(1));
        }
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

    private double kChartDragX, kChartXGap;

    @FXML
    private void onKChartDragStart(MouseEvent t) {
        kChartDragX = t.getX();
        kChartXGap = kChart.getXGap();
    }

    @FXML
    private void onKChartDrag(MouseEvent t) {
        double dX = t.getX() - kChartDragX;
        if (dX > kChartXGap || dX < -kChartXGap) {
            dateEnd.setValue(dateEnd.getValue().minusDays(2 * (long) (dX / kChartXGap)));
            dateStart.setValue(dateStart.getValue().minusDays(2 * (long) (dX / kChartXGap)));
            kChartDragX = t.getX();
        }
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
        int code = infoList.get(0).getToday().getCode();
        CommonEventController.onPlusClickedEvent(t, code);
    }

}
