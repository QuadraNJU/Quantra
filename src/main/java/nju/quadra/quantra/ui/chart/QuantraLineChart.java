package nju.quadra.quantra.ui.chart;

import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import nju.quadra.quantra.data.StockInfoPtr;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adn55 on 2017/3/16.
 */
public class QuantraLineChart extends LineChart<String, Number> {

    public List<String> dates;
    private List<List<Number>> dataList = new ArrayList<>();
    private Region plotBackground = (Region) lookup(".chart-plot-background");
    private Label toolTip = new Label();
    private Pane plotArea = new Pane(toolTip);

    private QuantraLineChart(Axis<String> xAxis, Axis<Number> yAxis, List<String> dates) {
        super(xAxis, yAxis);
        this.dates = dates;
        this.setLegendVisible(false);
        this.setCreateSymbols(false);
        this.getStylesheets().setAll(getClass().getResource("QuantraKChart.css").toString());
        getPlotChildren().add(plotArea);
        // Create tooltip
        toolTip.getStyleClass().add("tooltip");
        // Bind mouse events
        plotArea.setOnMouseExited(event -> toolTip.setVisible(false));
        toolTip.setVisible(false);
        plotArea.setOnMouseMoved(event -> {
            double xPos = event.getX();
            double yPos = event.getY();
            String xValue = xAxis.getValueForDisplay(xPos);
            Format f = new DecimalFormat("#.####");
            if (xValue != null) {
                int size = dates.size();
                int i = -1;
                for (String date : dates) {
                    i++;
                    if (date.equals(xValue)) {
                        StringBuilder tip = new StringBuilder();
                        int j = -1;
                        DecimalFormat df = new DecimalFormat("#.######");
                        for (Series<String, Number> series : getData()) {
                            j++;
                            double yValue = dataList.get(j).get(i).doubleValue();
                            if (!Double.isNaN(yValue)) {
                                tip.append("\n").append(series.getName()).append(": ").append(df.format(yValue));
                            }
                        }
                        toolTip.setText(xValue + tip.toString());
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

    public static QuantraLineChart createFrom(List<StockInfoPtr> ptrList) {
        return createFromDates(ptrList.stream().map(ptr -> ptr.get().getDate()).collect(Collectors.toList()));
    }

    public static QuantraLineChart createFromDates(List<String> dates) {
        // Create axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        // Create chart
        QuantraLineChart chart = new QuantraLineChart(xAxis, yAxis, dates);
        return chart;
    }

    public void addPath(String name, Paint color, List<Number> numbers) {
        Series<String, Number> series = new Series<>();
        int size = Math.min(dates.size(), numbers.size());
        for (int i = 0; i < size; i++) {
            if (numbers.get(i) != null && !Double.isNaN(numbers.get(i).doubleValue())) {
                series.getData().add(new Data<>(dates.get(i), numbers.get(i)));
            }
        }
        series.setName(name);
        getData().add(series);
        if (series.getNode() != null) {
            Path path = (Path) series.getNode();
            path.setStroke(color);
            path.setStrokeWidth(2);
        }
        dataList.add(numbers);
    }

    public void removePath(String name) {
        int i = -1;
        for (Series<String, Number> series : getData()) {
            i++;
            if (series.getName().equals(name)) {
                dataList.remove(i);
                getData().remove(i);
                break;
            }
        }
    }

    @Override
    protected void seriesAdded(Series<String, Number> series, int seriesIndex) {
        super.seriesAdded(series, seriesIndex);
        plotArea.toFront();
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        plotArea.resize(getWidth(), getHeight());
    }

}
