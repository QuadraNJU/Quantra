package nju.quadra.quantra.ui.chart;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;

import java.util.Iterator;

/**
 * Created by adn55 on 2017/3/4.
 */
public class QuantraKChart extends XYChart<String, Number> {

    public QuantraKChart(Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
    }

    @Override
    protected void dataItemAdded(Series<String, Number> series, int itemIndex, Data<String, Number> item) {
        Node candle = item.getNode();
        if (candle == null) {
            candle = new Candle();
            item.setNode(candle);
        }
        getPlotChildren().add(candle);
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
        for (Data item : series.getData()) {
            dataItemRemoved(item, series);
        }
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
                Node itemNode = item.getNode();
                StockInfo info = (StockInfo) item.getExtraValue();
                if (itemNode instanceof Candle && info != null) {
                    Candle candle = (Candle) itemNode;
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
    }

    class Candle extends Group {
        private Line highLowLine = new Line();
        private Region bar = new Region();
        private boolean openAboveClose = true;
        private Tooltip tooltip = new Tooltip();

        Candle() {
            setAutoSizeChildren(false);
            getChildren().addAll(highLowLine, bar);
            updateStyleClasses();
            Tooltip.install(bar, tooltip);
        }

        public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth) {
            openAboveClose = closeOffset > 0;
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
        }

        public void updateTooltip(double open, double close, double high, double low) {
            tooltip.setText("Open: " + open + "\nClose: " + close + "\nHigh: " + high + "\nLow: " + low);
        }

        private void updateStyleClasses() {
            highLowLine.getStyleClass().setAll("candlestick-line", openAboveClose ? "open-above-close" : "close-above-open");
            bar.getStyleClass().setAll("candlestick-bar", openAboveClose ? "open-above-close" : "close-above-open");
        }
    }

}
