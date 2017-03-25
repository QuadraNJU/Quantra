package nju.quadra.quantra.ui.chart;

import javafx.collections.FXCollections;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import nju.quadra.quantra.data.StockInfo;
import nju.quadra.quantra.data.StockInfoPtr;

import java.util.List;

/**
 * Created by adn55 on 2017/3/16.
 */
public class QuantraBarChart extends BarChart<String, Number> {

    private Region plotBackground = (Region) lookup(".chart-plot-background");
    private Label toolTip = new Label();
    private Pane plotArea = new Pane(toolTip);

    private QuantraBarChart(String name, Axis<String> xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
        this.setLegendVisible(false);
        this.setBarGap(1);
        this.setCategoryGap(0);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
        getPlotChildren().add(plotArea);
        // Create tooltip
        toolTip.getStyleClass().add("tooltip");
        toolTip.setVisible(false);
        // Bind mouse events
        plotArea.setOnMouseExited(event -> toolTip.setVisible(false));
        plotArea.setOnMouseMoved(event -> {
            double xPos = event.getX();
            double yPos = event.getY();
            String xValue = xAxis.getValueForDisplay(xPos);
            if (xValue != null) {
                int size = getData().get(0).getData().size();
                for (int i = 0; i < size; i++) {
                    StockInfo info = (StockInfo) getData().get(0).getData().get(i).getExtraValue();
                    if (info != null && info.getDate().equals(xValue)) {
                        toolTip.setText(xValue + "\n" + name + ": " + getData().get(0).getData().get(i).getYValue());
                        if (xPos + 10 + toolTip.getWidth() > plotBackground.getWidth()) {
                            xPos -= toolTip.getWidth() + 20;
                        }
                        if (yPos + 10 + toolTip.getHeight() > plotBackground.getHeight()) {
                            yPos -= toolTip.getHeight() + 20;
                        }
                        toolTip.relocate(xPos + 10, yPos + 10);
                        toolTip.setVisible(true);
                        break;
                    }
                }
            }
        });
    }

    public static QuantraBarChart createFrom(List<StockInfoPtr> infoList, String name) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        // Create series and add data
        Series<String, Number> series = new Series<>();
        for (StockInfoPtr ptr : infoList) {
            series.getData().add(new Data<>(ptr.get().getDate(), ptr.get().getVolume(), ptr.get()));
        }
        // Create chart
        QuantraBarChart chart = new QuantraBarChart(name, xAxis, yAxis);
        chart.setData(FXCollections.observableArrayList(series));
        return chart;
    }

    @Override
    protected void seriesAdded(Series<String, Number> series, int seriesIndex) {
        super.seriesAdded(series, seriesIndex);
        for (Data<String, Number> item : series.getData()) {
            if (item.getNode() != null && item.getExtraValue() != null) {
                StockInfo info = (StockInfo) item.getExtraValue();
                if (info.getClose() > info.getOpen()) {
                    item.getNode().getStyleClass().add("close-above-open");
                } else {
                    item.getNode().getStyleClass().add("open-above-close");
                }
            }
        }
        plotArea.toFront();
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        plotArea.resize(getWidth(), getHeight());
    }

}
