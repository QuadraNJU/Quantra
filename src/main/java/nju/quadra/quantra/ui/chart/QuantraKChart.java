package nju.quadra.quantra.ui.chart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import nju.quadra.quantra.data.StockBaseProtos.StockBase.StockInfo;
import nju.quadra.quantra.data.StockInfoPtr;

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
    private Label toolTip = new Label();
    private Pane plotArea = new Pane(toolTip);
    private Label yTip = new Label();

    private QuantraKChart(Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
        this.setMaxWidth(Double.POSITIVE_INFINITY);
        this.setMaxHeight(Double.POSITIVE_INFINITY);
        // Create overlay pane
        plotArea.getChildren().addAll(horiLine, vertLine);
        getPlotChildren().add(plotArea);
        // Create lines
        horiLine.getStyleClass().add("line");
        vertLine.getStyleClass().add("line");
        // Create tooltip
        toolTip.getStyleClass().add("tooltip");
        // Create Ytip
        getChildren().add(yTip);
        yTip.getStyleClass().add("tooltip");
        yTip.resize(60, 30);
        // Bind mouse events
        plotArea.setOnMouseExited(event -> {
            horiLine.setVisible(false);
            vertLine.setVisible(false);
            toolTip.setVisible(false);
            yTip.setVisible(false);
        });
        plotArea.getOnMouseExited().handle(null);
        plotArea.setOnMouseMoved(event -> {
            double yPos = event.getY();
            yTip.setText(yAxis.getValueForDisplay(yPos).toString());
            yTip.relocate(5, yPos);
            horiLine.relocate(0, event.getY());
            String xValue = xAxis.getValueForDisplay(event.getX());
            if (xValue != null) {
                int size = getData().get(0).getData().size();
                for (int i = 0; i < size; i++) {
                    StockInfo info = (StockInfo) getData().get(0).getData().get(i).getExtraValue();
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
                        String tip = xValue + "\n开: " + info.getOpen() + " 收: " + info.getClose() + "\n高: " + info.getHigh() + " 低: " + info.getLow();
                        for (Series<String, Number> series : getData()) {
                            if (series.getNode() != null) {
                                tip += "\n" + series.getName() + ": " + series.getData().get(i).getYValue();
                            }
                        }
                        toolTip.setText(tip);
                        toolTip.setVisible(true);
                        break;
                    }
                }
            }
            horiLine.setVisible(true);
            vertLine.setVisible(true);
            yTip.setVisible(true);
        });
    }

    public static QuantraKChart createFrom(List<StockInfoPtr> infoList) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        // Create series and add data
        Series<String, Number> series = new Series<>();
        for (StockInfoPtr ptr : infoList) {
            series.getData().add(new Data<>(ptr.get().getDate(), ptr.get().getOpen(), ptr.get()));
        }
        // Create chart
        QuantraKChart kChart = new QuantraKChart(xAxis, yAxis);
        kChart.setData(FXCollections.observableArrayList(series));
        return kChart;
    }

    public void addPath(String name, Paint color, List<Number> numbers) {
        Series<String, Number> series = new Series<>();
        ObservableList<Data<String, Number>> list = getData().get(0).getData();
        int size = Math.min(list.size(), numbers.size());
        for (int i = 0; i < size; i++) {
            if (numbers.get(i) != null && !Double.isNaN(numbers.get(i).doubleValue())) {
                series.getData().add(new Data<>(list.get(i).getXValue(), numbers.get(i)));
            }
        }
        Path path = new Path();
        path.setStroke(color);
        path.setStrokeWidth(2);
        series.setNode(path);
        series.setName(name);
        getData().add(series);
    }

    public void setHiddenPaths(List<String> hiddenPaths) {
        for (Series<String, Number> series : getData()) {
            if (series.getNode() != null) {
                series.getNode().setVisible(!hiddenPaths.contains(series.getName()));
            }
        }
    }

    public double getXGap() {
        return ((CategoryAxis) getXAxis()).getCategorySpacing();
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
        if (series.getNode() == null) {
            for (int i = series.getData().size() - 1; i >= 0; i--) {
                dataItemAdded(series, i, series.getData().get(i));
            }
        } else {
            getPlotChildren().add(series.getNode());
        }
    }

    @Override
    protected void seriesRemoved(Series<String, Number> series) {
        if (series.getNode() == null) {
            for (Data<String, Number> item : series.getData()) {
                dataItemRemoved(item, series);
            }
        } else {
            getPlotChildren().remove(series.getNode());
        }
    }

    @Override
    protected void updateAxisRange() {
        super.updateAxisRange();
        List<Number> yData = new ArrayList<>();
        for (int i = getData().size() - 1; i >= 0; i--) {
            Series<String, Number> series = getData().get(i);
            for (Data<String, Number> item : series.getData()) {
                if (i > 0) {
                    yData.add(item.getYValue());
                } else {
                    StockInfo info = (StockInfo) item.getExtraValue();
                    if (info != null) {
                        yData.add(info.getLow());
                        yData.add(info.getHigh());
                    }
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
            if (series.getNode() != null) {
                ((Path) series.getNode()).getElements().clear();
            }
            Iterator<Data<String, Number>> iter = getDisplayedDataIterator(series);
            while (iter.hasNext()) {
                Data<String, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                if (series.getNode() != null) {
                    Path path = (Path) series.getNode();
                    if (path.getElements().isEmpty()) {
                        path.getElements().add(new MoveTo(x, getYAxis().getDisplayPosition(item.getYValue())));
                    } else {
                        path.getElements().add(new LineTo(x, getYAxis().getDisplayPosition(item.getYValue())));
                    }
                } else {
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
        }
        // lines
        horiLine.setEndX(getWidth());
        vertLine.setEndY(getHeight());
        plotArea.resize(getWidth(), getHeight());
        // Make sure that overlay pane is at the front
        plotArea.toFront();
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
