package nju.quadra.quantra.ui.chart;

import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adn55 on 2017/3/4.
 */
public class QuantraKChart extends XYChart<String, Number> {

    private Line horiLine = new Line(0, 0, 0, 0);
    private Line vertLine = new Line(0, 0, 0, 0);
    private Region plotBackground = (Region) lookup(".chart-plot-background");
    private Region plotArea = new Region();
    private Label toolTip = new Label();
    private Label yTip = new Label();

    private QuantraKChart(Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
        this.setMaxWidth(Double.POSITIVE_INFINITY);
        this.setMaxHeight(Double.POSITIVE_INFINITY);
        // Create lines
        getPlotChildren().addAll(horiLine, vertLine, plotArea, toolTip);
        horiLine.getStyleClass().add("line");
        vertLine.getStyleClass().add("line");
        // Create tooltip
        toolTip.getStyleClass().add("tooltip");
        toolTip.resize(120, 60);
        toolTip.setMouseTransparent(true);
        toolTip.setVisible(false);
        // Create Ytip
        getChildren().add(yTip);
        yTip.getStyleClass().add("tooltip");
        yTip.resize(50, 30);
        yTip.setVisible(false);
        // Bind mouse events
        plotArea.setOnMouseEntered(event -> {
            horiLine.setVisible(true);
            vertLine.setVisible(true);
            toolTip.setVisible(true);
            yTip.setVisible(true);
        });
        plotArea.setOnMouseExited(event -> {
            horiLine.setVisible(false);
            vertLine.setVisible(false);
            toolTip.setVisible(false);
            yTip.setVisible(false);
        });
        plotArea.setOnMouseMoved(event -> {
            horiLine.relocate(0, event.getY());
            String xValue = xAxis.getValueForDisplay(event.getX());
            double yPos = event.getY();
            yTip.setText(yAxis.getValueForDisplay(yPos).toString());
            yTip.relocate(5, yPos - 10);
            if (xValue != null) {
                Iterator<Data<String, Number>> dataIterator = getDisplayedDataIterator(getData().get(0));
                while (dataIterator.hasNext()) {
                    StockInfo info = (StockInfo) dataIterator.next().getExtraValue();
                    if (info != null && info.getDate().equals(xValue)) {
                        double xPos = xAxis.getDisplayPosition(xValue);
                        vertLine.relocate(xPos, 0);
                        if (xPos + 10 + toolTip.getWidth() > plotBackground.getWidth()) {
                            xPos -= toolTip.getWidth() + 20;
                        }
                        if (yPos + 10 + toolTip.getHeight() > plotBackground.getHeight()) {
                            yPos -= toolTip.getHeight() + 20;
                        }
                        toolTip.relocate(xPos + 10, yPos + 10);
                        toolTip.setText(xValue + "\n开: " + info.getOpen() + " 收: " + info.getClose() + "\n高: " + info.getHigh() + " 低: " + info.getLow());
                        break;
                    }
                }
            }
        });
    }

    public static QuantraKChart createFrom(List<StockInfo> infoList) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        // Create series and add data
        Series<String, Number> series = new Series<>();
        for (StockInfo info : infoList) {
            series.getData().add(new Data<>(info.getDate(), info.getOpen(), info));
        }
        // Create chart
        QuantraKChart kChart = new QuantraKChart(xAxis, yAxis);
        kChart.setData(FXCollections.observableArrayList(series));
        return kChart;
    }

    @Override
    protected void dataItemAdded(Series<String, Number> series, int itemIndex, Data<String, Number> item) {
        Node candle = item.getNode();
        if (candle == null) {
            candle = new Candle();
            item.setNode(candle);
        }
        getPlotChildren().add(candle);
        // Make sure that lines are at the front
        horiLine.toFront();
        vertLine.toFront();
        plotArea.toFront();
        toolTip.toFront();
    }

    @Override
    protected void dataItemRemoved(Data<String, Number> item, Series<String, Number> series) {
        getPlotChildren().remove(item.getNode());
    }

    @Override
    protected void dataItemChanged(Data<String, Number> item) {
    }

    @Override
    protected void seriesAdded(Series<String, Number> series, int seriesIndex) {
        for (int i = series.getData().size() - 1; i >= 0; i--) {
            dataItemAdded(series, i, series.getData().get(i));
        }
    }

    @Override
    protected void seriesRemoved(Series<String, Number> series) {
        for (Data<String, Number> item : series.getData()) {
            dataItemRemoved(item, series);
        }
    }

    @Override
    protected void updateAxisRange() {
        super.updateAxisRange();
        List<Number> yData = new ArrayList<>();
        for (Series<String, Number> series : getData()) {
            for (Data item : series.getData()) {
                StockInfo info = (StockInfo) item.getExtraValue();
                if (info != null) {
                    yData.add(info.getLow());
                    yData.add(info.getHigh());
                }
            }
        }
        getYAxis().invalidateRange(yData);
    }

    @Override
    protected void layoutPlotChildren() {
        if (getData() == null) {
            return;
        }
        for (Series<String, Number> series : getData()) {
            Iterator<Data<String, Number>> iter = getDisplayedDataIterator(series);
            while (iter.hasNext()) {
                Data<String, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                Candle candle = (Candle) item.getNode();
                StockInfo info = (StockInfo) item.getExtraValue();
                if (candle != null && info != null) {
                    double closePos = getYAxis().getDisplayPosition(info.getClose());
                    double highPos = getYAxis().getDisplayPosition(info.getHigh());
                    double lowPos = getYAxis().getDisplayPosition(info.getLow());
                    double candleWidth = ((CategoryAxis) getXAxis()).getCategorySpacing() * .9;
                    candle.update(closePos - y, highPos - y, lowPos - y, candleWidth);
                    candle.setLayoutX(x);
                    candle.setLayoutY(y);
                }
            }
        }
        // lines
        horiLine.setEndX(getWidth());
        vertLine.setEndY(getHeight());
        plotArea.resize(getWidth(), getHeight());
    }

    class Candle extends Group {
        private Line highLowLine = new Line();
        private Region bar = new Region();
        private boolean openAboveClose = false;
        private boolean closeAboveOpen = false;

        Candle() {
            setAutoSizeChildren(false);
            getChildren().addAll(highLowLine, bar);
            updateStyleClasses();
        }

        void update(double closeOffset, double highOffset, double lowOffset, double candleWidth) {
            openAboveClose = closeOffset > 0;
            closeAboveOpen = closeOffset < 0;
            updateStyleClasses();
            highLowLine.setStartY(highOffset);
            highLowLine.setEndY(lowOffset);
            if (candleWidth == -1) {
                candleWidth = bar.prefWidth(-1);
            }
            if (openAboveClose) {
                bar.resizeRelocate(-candleWidth / 2, 0, candleWidth, closeOffset);
            } else {
                bar.resizeRelocate(-candleWidth / 2, closeOffset, candleWidth, closeOffset * -1);
            }
            if (bar.getHeight() < 1) {
                bar.resize(bar.getWidth(), 1);
            }
        }

        private void updateStyleClasses() {
            highLowLine.getStyleClass().setAll("candlestick-line", openAboveClose ? "open-above-close" : (closeAboveOpen ? "close-above-open" : ""));
            bar.getStyleClass().setAll("candlestick-bar", openAboveClose ? "open-above-close" : (closeAboveOpen ? "close-above-open" : ""));
        }
    }

}
